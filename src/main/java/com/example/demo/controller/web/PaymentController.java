package com.example.demo.controller.web;

import com.example.demo.dto.payment.PaymentConfirmDto;
import com.example.demo.model.PaymentStatus;
import com.example.demo.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/start/{orderId}")
    public String start(@AuthenticationPrincipal UserDetails user,
                        @PathVariable Long orderId) {

        var payment = paymentService.startPayment(user.getUsername(), orderId);
        return "redirect:/payment/checkout/" + payment.getId();
    }

    @GetMapping("/checkout/{paymentId}")
    public String checkout(@AuthenticationPrincipal UserDetails user,
                           @PathVariable Long paymentId,
                           Model model,
                           @RequestParam(value = "err", required = false) String err) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);

        model.addAttribute("payment", payment);
        model.addAttribute("form", new PaymentConfirmDto());
        model.addAttribute("err", err != null);

        return "payment/checkout";
    }

    @PostMapping("/checkout/{paymentId}")
    public String confirm(@AuthenticationPrincipal UserDetails user,
                          @PathVariable Long paymentId,
                          @Valid @ModelAttribute("form") PaymentConfirmDto form,
                          BindingResult br,
                          Model model) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);

        if (br.hasErrors()) {
            model.addAttribute("payment", payment);
            return "payment/checkout";
        }

        // ✅ outcome qərarı + save service-də olmalıdır (controller-də set edib saxlamırsan)
        paymentService.confirmAndProcess(user.getUsername(), paymentId, form);

        return "redirect:/payment/processing/" + paymentId;
    }

    @GetMapping("/processing/{paymentId}")
    public String processing(@AuthenticationPrincipal UserDetails user,
                             @PathVariable Long paymentId,
                             Model model) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);
        model.addAttribute("payment", payment);
        return "payment/processing";
    }

    // polling endpoint
    @GetMapping("/api/{paymentId}")
    @ResponseBody
    public Object poll(@AuthenticationPrincipal UserDetails user,
                       @PathVariable Long paymentId) {

        var p = paymentService.pollAndMaybeFinalize(user.getUsername(), paymentId);

        Long orderId = (p.getOrder() != null ? p.getOrder().getId() : null);

        return java.util.Map.of(
                "id", p.getId(),
                "status", p.getStatus().name(),
                "orderId", orderId
        );
    }

    @GetMapping("/result/{paymentId}")
    public String result(@AuthenticationPrincipal UserDetails user,
                         @PathVariable Long paymentId,
                         Model model) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);

        boolean success = payment.getStatus() == PaymentStatus.SUCCEEDED;
        Long orderId = payment.getOrder() != null ? payment.getOrder().getId() : null;

        model.addAttribute("payment", payment);
        model.addAttribute("success", success);
        model.addAttribute("orderId", orderId);

        return "payment/result";
    }
}

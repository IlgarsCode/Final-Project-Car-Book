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

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/odenis")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/baslat/{orderId}")
    public String baslat(@AuthenticationPrincipal UserDetails user,
                         @PathVariable Long orderId) {

        var payment = paymentService.startPayment(user.getUsername(), orderId);
        return "redirect:/odenis/kassa/" + payment.getId();
    }

    @GetMapping("/kassa/{paymentId}")
    public String kassa(@AuthenticationPrincipal UserDetails user,
                        @PathVariable Long paymentId,
                        Model model,
                        @RequestParam(value = "xeta", required = false) String xeta) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);

        model.addAttribute("payment", payment);
        model.addAttribute("form", new PaymentConfirmDto());
        model.addAttribute("xeta", xeta != null);

        return "payment/checkout";
    }

    @PostMapping("/kassa/{paymentId}")
    public String tesdiqle(@AuthenticationPrincipal UserDetails user,
                           @PathVariable Long paymentId,
                           @Valid @ModelAttribute("form") PaymentConfirmDto form,
                           BindingResult br,
                           Model model) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);

        if (br.hasErrors()) {
            model.addAttribute("payment", payment);
            return "payment/checkout";
        }

        paymentService.confirmAndProcess(user.getUsername(), paymentId, form);

        return "redirect:/odenis/emal/" + paymentId;
    }

    @GetMapping("/emal/{paymentId}")
    public String emal(@AuthenticationPrincipal UserDetails user,
                       @PathVariable Long paymentId,
                       Model model) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);
        model.addAttribute("payment", payment);
        return "payment/processing";
    }

    @GetMapping("/api/{paymentId}")
    @ResponseBody
    public Object poll(@AuthenticationPrincipal UserDetails user,
                       @PathVariable Long paymentId) {

        var p = paymentService.pollAndMaybeFinalize(user.getUsername(), paymentId);

        // IMPORTANT: /sifarislerim/{no} userOrderNo gözləyir
        Long orderNo = (p.getOrder() != null ? p.getOrder().getUserOrderNo() : null);

        return Map.of(
                "id", p.getId(),
                "status", p.getStatus().name(),
                "orderNo", orderNo
        );
    }

    @GetMapping("/netice/{paymentId}")
    public String netice(@AuthenticationPrincipal UserDetails user,
                         @PathVariable Long paymentId,
                         Model model) {

        var payment = paymentService.getPaymentForUser(user.getUsername(), paymentId);

        boolean success = payment.getStatus() == PaymentStatus.SUCCEEDED;

        // IMPORTANT: /sifarislerim/{no} userOrderNo gözləyir
        Long orderNo = payment.getOrder() != null ? payment.getOrder().getUserOrderNo() : null;

        model.addAttribute("payment", payment);
        model.addAttribute("success", success);
        model.addAttribute("orderNo", orderNo);

        return "payment/result";
    }
}
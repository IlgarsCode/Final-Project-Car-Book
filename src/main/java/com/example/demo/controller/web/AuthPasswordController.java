package com.example.demo.controller.web;

import com.example.demo.dto.auth.ForgotPasswordDto;
import com.example.demo.dto.auth.ResetPasswordDto;
import com.example.demo.dto.auth.VerifyOtpDto;
import com.example.demo.services.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthPasswordController {

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String forgotPage(@RequestParam(value = "sent", required = false) String sent,
                             Model model) {
        model.addAttribute("form", new ForgotPasswordDto());
        model.addAttribute("sent", sent != null);
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgot(@Valid @ModelAttribute("form") ForgotPasswordDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) return "auth/forgot-password";

        try {
            passwordResetService.requestOtp(form.getEmail());
        } catch (ResponseStatusException ex) {
            // 429 və s. mesajı göstərək
            model.addAttribute("serverError", ex.getReason());
            return "auth/forgot-password";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/forgot-password";
        }

        // təhlükəsizlik: hər halda “göndərildi” deyirik
        return "redirect:/auth/verify-otp?email=" + form.getEmail().trim().toLowerCase();
    }

    @GetMapping("/verify-otp")
    public String verifyPage(@RequestParam("email") String email,
                             @RequestParam(value = "err", required = false) String err,
                             Model model) {
        VerifyOtpDto dto = new VerifyOtpDto();
        dto.setEmail(email);
        model.addAttribute("form", dto);
        model.addAttribute("err", err != null);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verify(@Valid @ModelAttribute("form") VerifyOtpDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) return "auth/verify-otp";

        try {
            String token = passwordResetService.verifyOtp(form.getEmail(), form.getCode());
            return "redirect:/auth/reset-password?token=" + token;
        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/verify-otp";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/verify-otp";
        }
    }

    @GetMapping("/reset-password")
    public String resetPage(@RequestParam("token") String token,
                            @RequestParam(value = "ok", required = false) String ok,
                            Model model) {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setToken(token);
        model.addAttribute("form", dto);
        model.addAttribute("ok", ok != null);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String reset(@Valid @ModelAttribute("form") ResetPasswordDto form,
                        BindingResult br,
                        Model model) {

        if (br.hasErrors()) return "auth/reset-password";

        try {
            passwordResetService.resetPassword(form.getToken(), form.getNewPassword(), form.getConfirmNewPassword());
            // loginə yönləndir
            return "redirect:/auth/login?logout=true";
        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/reset-password";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/reset-password";
        }
    }
}

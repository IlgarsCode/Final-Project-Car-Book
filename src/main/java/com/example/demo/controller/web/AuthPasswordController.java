package com.example.demo.controller.web;

import com.example.demo.dto.auth.ForgotPasswordDto;
import com.example.demo.dto.auth.ResetPasswordDto;
import com.example.demo.dto.auth.VerifyOtpDto;
import com.example.demo.services.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class AuthPasswordController {

    private final PasswordResetService passwordResetService;

    // ✅ /sifreni-unutdum
    @GetMapping("/sifreni-unutdum")
    public String forgotPage(@RequestParam(value = "gonderildi", required = false) String gonderildi,
                             Model model) {
        model.addAttribute("form", new ForgotPasswordDto());
        model.addAttribute("sent", gonderildi != null);
        return "auth/forgot-password";
    }

    // ✅ /sifreni-unutdum
    @PostMapping("/sifreni-unutdum")
    public String forgot(@Valid @ModelAttribute("form") ForgotPasswordDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) return "auth/forgot-password";

        try {
            passwordResetService.requestOtp(form.getEmail());
        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/forgot-password";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/forgot-password";
        }

        // ✅ əvvəl: /auth/verify-otp?email=...
        return "redirect:/otp-tesdiq?email=" + form.getEmail().trim().toLowerCase();
    }

    // ✅ /otp-tesdiq
    @GetMapping("/otp-tesdiq")
    public String verifyPage(@RequestParam("email") String email,
                             @RequestParam(value = "xeta", required = false) String xeta,
                             Model model) {
        VerifyOtpDto dto = new VerifyOtpDto();
        dto.setEmail(email);
        model.addAttribute("form", dto);
        model.addAttribute("err", xeta != null);
        return "auth/verify-otp";
    }

    // ✅ /otp-tesdiq
    @PostMapping("/otp-tesdiq")
    public String verify(@Valid @ModelAttribute("form") VerifyOtpDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) return "auth/verify-otp";

        try {
            String token = passwordResetService.verifyOtp(form.getEmail(), form.getCode());

            // ✅ əvvəl: /auth/reset-password?token=...
            return "redirect:/sifre-yenile?token=" + token;

        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/verify-otp";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/verify-otp";
        }
    }

    // ✅ /sifre-yenile
    @GetMapping("/sifre-yenile")
    public String resetPage(@RequestParam("token") String token,
                            @RequestParam(value = "ok", required = false) String ok,
                            Model model) {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setToken(token);
        model.addAttribute("form", dto);
        model.addAttribute("ok", ok != null);
        return "auth/reset-password";
    }

    // ✅ /sifre-yenile
    @PostMapping("/sifre-yenile")
    public String reset(@Valid @ModelAttribute("form") ResetPasswordDto form,
                        BindingResult br,
                        Model model) {

        if (br.hasErrors()) return "auth/reset-password";

        try {
            passwordResetService.resetPassword(
                    form.getToken(),
                    form.getNewPassword(),
                    form.getConfirmNewPassword()
            );

            // ✅ əvvəl: /auth/login?logout=true
            return "redirect:/giris?cixis=true";

        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/reset-password";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/reset-password";
        }
    }
}
package com.example.demo.controller.web;

import com.example.demo.dto.auth.RegisterDto;
import com.example.demo.dto.auth.VerifyRegisterOtpDto;
import com.example.demo.services.RegisterOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final RegisterOtpService registerOtpService;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            Model model
    ) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        model.addAttribute("registered", registered != null);
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterDto());
        return "auth/register";
    }

    // ✅ dəyişdi: indi OTP göndərir
    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("form") RegisterDto form,
                             BindingResult br,
                             Model model) {

        if (br.hasErrors()) return "auth/register";

        try {
            registerOtpService.requestRegisterOtp(form);
        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/register";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/register";
        }

        return "redirect:/auth/register-verify?email=" + form.getEmail().trim().toLowerCase();
    }

    // ✅ yeni səhifə
    @GetMapping("/register-verify")
    public String registerVerifyPage(@RequestParam("email") String email,
                                     Model model) {
        VerifyRegisterOtpDto dto = new VerifyRegisterOtpDto();
        dto.setEmail(email);
        model.addAttribute("form", dto);
        return "auth/register-verify";
    }

    // ✅ yeni post
    @PostMapping("/register-verify")
    public String registerVerify(@Valid @ModelAttribute("form") VerifyRegisterOtpDto form,
                                 BindingResult br,
                                 Model model) {

        if (br.hasErrors()) return "auth/register-verify";

        try {
            registerOtpService.verifyRegisterOtpAndCreateUser(form.getEmail(), form.getCode());
            return "redirect:/auth/login?registered=1";
        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/register-verify";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/register-verify";
        }
    }
}

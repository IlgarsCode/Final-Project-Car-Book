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
public class AuthController {

    private final RegisterOtpService registerOtpService;
    @GetMapping("/giris")
    public String loginPage(@RequestParam(value = "xeta", required = false) String xeta,
                            @RequestParam(value = "cixis", required = false) String cixis,
                            @RequestParam(value = "qeydiyyat", required = false) String qeydiyyat,
                            Model model) {

        model.addAttribute("error", xeta != null);
        model.addAttribute("logout", cixis != null);
        model.addAttribute("registered", qeydiyyat != null);
        return "auth/login";
    }

    @GetMapping("/qeydiyyat")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterDto());
        return "auth/register";
    }
    @PostMapping("/qeydiyyat")
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

        return "redirect:/qeydiyyat-tesdiq?email=" + form.getEmail().trim().toLowerCase();
    }

    @GetMapping("/qeydiyyat-tesdiq")
    public String registerVerifyPage(@RequestParam("email") String email,
                                     Model model) {
        VerifyRegisterOtpDto dto = new VerifyRegisterOtpDto();
        dto.setEmail(email);
        model.addAttribute("form", dto);
        return "auth/register-verify";
    }

    @PostMapping("/qeydiyyat-tesdiq")
    public String registerVerify(@Valid @ModelAttribute("form") VerifyRegisterOtpDto form,
                                 BindingResult br,
                                 Model model) {

        if (br.hasErrors()) return "auth/register-verify";

        try {
            registerOtpService.verifyRegisterOtpAndCreateUser(form.getEmail(), form.getCode());

            return "redirect:/giris?qeydiyyat=1";

        } catch (ResponseStatusException ex) {
            model.addAttribute("serverError", ex.getReason());
            return "auth/register-verify";
        } catch (Exception ex) {
            model.addAttribute("serverError", "Xəta baş verdi");
            return "auth/register-verify";
        }
    }
}
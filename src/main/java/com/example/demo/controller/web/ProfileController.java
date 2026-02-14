package com.example.demo.controller.web;

import com.example.demo.dto.profile.PasswordChangeDto;
import com.example.demo.dto.profile.ProfileUpdateDto;
import com.example.demo.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String profilePage(Model model, Authentication auth,
                              @RequestParam(value = "ok", required = false) String ok,
                              @RequestParam(value = "pwdOk", required = false) String pwdOk,
                              @RequestParam(value = "avatarOk", required = false) String avatarOk,
                              @RequestParam(value = "emailChanged", required = false) String emailChanged) {

        String email = auth.getName();
        var me = profileService.getMe(email);

        ProfileUpdateDto form = new ProfileUpdateDto();
        form.setEmail(me.getEmail());
        form.setFullName(me.getFullName());
        form.setPhone(me.getPhone());

        model.addAttribute("me", me);
        model.addAttribute("form", form);
        model.addAttribute("pwdForm", new PasswordChangeDto());

        model.addAttribute("ok", ok != null);
        model.addAttribute("pwdOk", pwdOk != null);
        model.addAttribute("avatarOk", avatarOk != null);
        model.addAttribute("emailChanged", emailChanged != null);

        return "profile/index";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("form") ProfileUpdateDto form,
                                BindingResult br,
                                Authentication auth,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        String currentEmail = auth.getName();
        var oldEmail = currentEmail.toLowerCase();

        if (br.hasErrors()) {
            var me = profileService.getMe(currentEmail);
            model.addAttribute("me", me);
            model.addAttribute("pwdForm", new PasswordChangeDto());
            return "profile/index";
        }

        profileService.updateProfile(currentEmail, form);

        // email dəyişibsə session-da #authentication.name köhnə qalır → ən təmiz yol logout edib yenidən login etdirməkdir
        String newEmail = form.getEmail().trim().toLowerCase();
        if (!newEmail.equals(oldEmail)) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            return "redirect:/auth/login?logout=true";
        }

        return "redirect:/profile?ok=1";
    }

    @PostMapping("/avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile avatar,
                               Authentication auth) {
        profileService.updateAvatar(auth.getName(), avatar);
        return "redirect:/profile?avatarOk=1";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("pwdForm") PasswordChangeDto pwdForm,
                                 BindingResult br,
                                 Authentication auth,
                                 Model model) {

        if (br.hasErrors()) {
            var me = profileService.getMe(auth.getName());
            ProfileUpdateDto form = new ProfileUpdateDto();
            form.setEmail(me.getEmail());
            form.setFullName(me.getFullName());
            form.setPhone(me.getPhone());

            model.addAttribute("me", me);
            model.addAttribute("form", form);
            return "profile/index";
        }

        profileService.changePassword(auth.getName(), pwdForm);
        return "redirect:/profile?pwdOk=1";
    }
}

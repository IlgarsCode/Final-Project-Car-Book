package com.example.demo.controller.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @GetMapping("/error/403-view")
    public String forbidden() {
        return "error/403";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = 500;

        if (statusObj != null) {
            try {
                status = Integer.parseInt(statusObj.toString());
            } catch (Exception ignored) {}
        }

        if (status == 404) return "error/404";
        return "error/500";
    }
}
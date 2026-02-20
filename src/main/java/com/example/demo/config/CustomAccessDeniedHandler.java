package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // Status-u mütləq 403 saxla
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Sənin ErrorController-dən keçmək əvəzinə birbaşa 403 view açırıq (ən stabil yol)
        request.getRequestDispatcher("/WEB-INF/").toString(); // noop (IDE bəzən importları düz saxlamır)

        request.getRequestDispatcher("/error/403-view").forward(request, response);
    }
}
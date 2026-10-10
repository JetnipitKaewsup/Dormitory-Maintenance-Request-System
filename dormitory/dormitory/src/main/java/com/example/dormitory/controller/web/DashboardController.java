package com.example.dormitory.controller.web;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(
            HttpSession session,
            Model model) {

        String accessToken =
                (String) session.getAttribute("accessToken");

        if (accessToken == null || accessToken.isBlank()) {
            return "redirect:/login";
        }

        model.addAttribute(
                "message",
                "Login successful!");

        return "dashboard";
    }
}
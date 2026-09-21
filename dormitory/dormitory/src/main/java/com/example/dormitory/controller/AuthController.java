package com.example.dormitory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {


    @GetMapping("/login")
    public String showLoginPage(Model model) {

        model.addAttribute(
                "loginRequest",
                new LoginRequest()
        );

        return "login";
    }


    // =========================
    // LOGIN PROCESS
    // =========================

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest,
            Model model) {

        // Logic to authenticate would go here
        System.out.println(
                "Username: " + loginRequest.getUsername()
        );

        // For demo, just return to the same page
        return "login";
    }


    // =========================
    // REGISTER PAGE
    // =========================

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "register";
    }


    // =========================
    // REGISTER PROCESS
    // =========================

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute RegisterRequest registerRequest,
            Model model) {

        // Print submitted information
        System.out.println(
                "First Name: " + registerRequest.getFirstName()
        );

        System.out.println(
                "Last Name: " + registerRequest.getLastName()
        );

        System.out.println(
                "Username: " + registerRequest.getUsername()
        );

        System.out.println(
                "Email: " + registerRequest.getEmail()
        );

        System.out.println(
                "Room Address: " + registerRequest.getRoomAddress()
        );

        System.out.println(
                "Room Number: " + registerRequest.getRoomNumber()
        );


        // For demo, redirect to login
        return "redirect:/login";
    }


    // =========================
    // LOGIN REQUEST
    // =========================

    public static class LoginRequest {

        private String username;
        private String password;


        public String getUsername() {
            return username;
        }


        public void setUsername(String username) {
            this.username = username;
        }


        public String getPassword() {
            return password;
        }


        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {

        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private String roomAddress;
        private String roomNumber;
        private String password;
        private String confirmPassword;


        public String getFirstName() {
            return firstName;
        }


        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }


        public String getLastName() {
            return lastName;
        }


        public void setLastName(String lastName) {
            this.lastName = lastName;
        }


        public String getUsername() {
            return username;
        }


        public void setUsername(String username) {
            this.username = username;
        }


        public String getEmail() {
            return email;
        }


        public void setEmail(String email) {
            this.email = email;
        }


        public String getRoomAddress() {
            return roomAddress;
        }


        public void setRoomAddress(String roomAddress) {
            this.roomAddress = roomAddress;
        }


        public String getRoomNumber() {
            return roomNumber;
        }


        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }


        public String getPassword() {
            return password;
        }


        public void setPassword(String password) {
            this.password = password;
        }


        public String getConfirmPassword() {
            return confirmPassword;
        }


        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
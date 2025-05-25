package org.example.practica1.controller;

import org.example.practica1.model.User;
import org.example.practica1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный логин или пароль!");
        }
        if (logout != null) {
            model.addAttribute("logout", "Вы успешно вышли из системы!");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register-process")
    public String processRegistration(@ModelAttribute("user") User user,
                                    BindingResult bindingResult,
                                    Model model) {
        // Проверяем, существует ли пользователь с таким email
        User existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            bindingResult.rejectValue("email", "error.user",
                    "Пользователь с таким email уже существует");
            return "auth/register";
        }

        // Если нет ошибок, сохраняем пользователя
        userService.saveUser(user);
        return "redirect:/login?registered";
    }
} 
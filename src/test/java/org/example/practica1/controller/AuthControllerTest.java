package org.example.practica1.controller;

import org.example.practica1.config.TestSecurityConfig;
import org.example.practica1.model.User;
import org.example.practica1.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void loginPageShouldDisplayLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeDoesNotExist("error", "logout"));
    }

    @Test
    void loginPageShouldShowErrorMessage() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("error", "Неверный логин или пароль!"));
    }

    @Test
    void loginPageShouldShowLogoutMessage() throws Exception {
        mockMvc.perform(get("/login").param("logout", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("logout", "Вы успешно вышли из системы!"));
    }

    @Test
    void registerPageShouldDisplayRegistrationForm() throws Exception {
        mockMvc.perform(get("/register")
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void registerProcessShouldCreateNewUser() throws Exception {
        when(userService.getUserByEmail(anyString())).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/register-process")
                .with(SecurityMockMvcRequestPostProcessors.anonymous())
                .with(csrf())
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("name", "Test User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    void registerProcessShouldFailWithExistingEmail() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(existingUser);

        mockMvc.perform(post("/register-process")
                .with(SecurityMockMvcRequestPostProcessors.anonymous())
                .with(csrf())
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("name", "Test User"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("user", "email"));

        verify(userService, never()).saveUser(any(User.class));
    }
} 
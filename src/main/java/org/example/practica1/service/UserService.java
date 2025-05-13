package org.example.practica1.service;

import org.example.practica1.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
}
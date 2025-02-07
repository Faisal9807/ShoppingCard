package com.ecom.service;

import com.ecom.model.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);

    User findByEmail(String name);

    List<User> getAllUsers(String userRole);

    boolean updateStatus(int userId, boolean status);
}

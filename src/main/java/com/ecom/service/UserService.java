package com.ecom.service;

import com.ecom.model.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);

    User findByEmail(String name);

    List<User> getAllUsers(String userRole);

    boolean updateStatus(int userId, boolean status);

    public void increaseFailedAttempt(User user);
    public void userAccountLocked(User user);
    public boolean unlockAccountTimeExpired(User user);
    public void resetAttempt(int userId);

    void updateUserResetToken(String email, String resetToken);

    User findByResetToken(String token);

    void updateUser(User user);
}

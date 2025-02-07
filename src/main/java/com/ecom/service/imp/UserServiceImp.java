package com.ecom.service.imp;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String name) {
        return userRepository.findByEmail(name);
    }

    @Override
    public List<User> getAllUsers(String userRole) {
        return userRepository.findByRole(userRole);
    }

    @Override
    public boolean updateStatus(int userId, boolean status) {
        Optional<User> option=userRepository.findById(userId);
        if (option.isPresent()) {
            User user=option.get();
            user.setEnabled(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }

}

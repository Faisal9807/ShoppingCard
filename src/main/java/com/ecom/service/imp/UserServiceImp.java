package com.ecom.service.imp;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.setLockTime(null);
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

    @Override
    public void increaseFailedAttempt(User user) {
        int attempt =user.getFailedAttempt()+1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLocked(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(User user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime=lockTime + AppConstant.UNLOCK_DURATION_TIME;
        long currentTime=new Date().getTime();
        if(unlockTime<currentTime){
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }

}

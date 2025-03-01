package com.ecom.config;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImp extends SimpleUrlAuthenticationFailureHandler{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email=request.getParameter("username");

        User userDetails=userRepository.findByEmail(email);
        if(userDetails.isEnabled()){
            if(userDetails.isAccountNonLocked()){
                if(userDetails.getFailedAttempt()<AppConstant.ATTEMPT_TIME){
                    userService.increaseFailedAttempt(userDetails);
                }
                else{
                    userService.userAccountLocked(userDetails);
                    exception=new LockedException("Your Account is Locked !! failed attempt 3");
                }
            }
            else{
                if(userService.unlockAccountTimeExpired(userDetails)){
                    exception=new LockedException("Your Account is UnLocked !! Please Try To Login Again");
                }
                else{
                    exception=new LockedException("Your Account is Locked !! Please Try After Sometime");
                }
            }
        }
        else{
            exception=new LockedException("Your Account is Inactive");
        }

        super.setDefaultFailureUrl("/signIn?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
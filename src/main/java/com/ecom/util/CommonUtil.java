package com.ecom.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;
    public boolean sendMail(String url, String email) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("daspabitra55@gmail.com", "Shooping Cart");
        helper.setTo(email);

        String content = "<p>Dear User,</p>" +
                "<p>We received a request to reset your password. If you made this request, please click the link below to set a new password:</p>" +
                "<p><a href=\"" + url + "\">Reset My Password</a></p>" +
                "<p>If you did not request a password reset, please ignore this email. Your account security remains intact.</p>" +
                "<p>For any assistance, feel free to contact our support team.</p>" +
                "<p>Best regards,</p>" +
                "<p><strong>Shopping Cart Team</strong></p>";
        helper.setSubject("Password Reset");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        // it given http://localhost:8081/forgotPassword this path
        String siteUrl=request.getRequestURL().toString();

        // i need http://localhost:8081
        return siteUrl.replace(request.getServletPath(),"");

    }
}

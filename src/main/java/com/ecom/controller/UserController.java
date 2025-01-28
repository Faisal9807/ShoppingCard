package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p , Model model){
        if(p!=null){
            User user=userService.findByEmail(p.getName());
            model.addAttribute("user",user);
        }
        List<Category> categories=categoryService.getAllActiveCategory();
        model.addAttribute("category",categories);
    }
}

package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.User;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Controller
public class HomeController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	@ModelAttribute
	public void getUserDetails(Principal p , Model model){
		if(p!=null){
            User user=userService.findByEmail(p.getName());
            model.addAttribute("user",user);
        }
		List<Category> categories=categoryService.getAllActiveCategory();
		model.addAttribute("category",categories);
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/signIn")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(Model m,@RequestParam(value="category",defaultValue = "") String category) {
		m.addAttribute("page", "products");
		m.addAttribute("categories",categoryService.getAllActiveCategory());
		m.addAttribute("products",productService.getAllActiveProduct(category));
		m.addAttribute("paramValue",category);
		return "products";
	}
	@GetMapping("/viewProduct/{id}")
	public String viewProduct(@PathVariable int id, Model m) {
		m.addAttribute("product",productService.getProductById(id));
		return "viewProduct";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(image);
		User userObject=userService.saveUser(user);
		if(!ObjectUtils.isEmpty(userObject)){
			if(!file.isEmpty()){
				File saveFile =new ClassPathResource("static/img").getFile();

				Path path= Paths.get(saveFile.getAbsolutePath()+ File.separator+file.getOriginalFilename());

				long copy = Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg","Register Successfully");
		}
		else{
			session.setAttribute("errorMsg","Something Went Wrong");
		}
		return "redirect:/register";
	}

	//Forgot Password
	@GetMapping("/forgotPassword")
	public String showForgotPassword(){
		return "forgot_password.html";
	}

	@PostMapping("/forgotPassword")
	public String ForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
		User user=userService.findByEmail(email);
		if(ObjectUtils.isEmpty(user)){
			session.setAttribute("errorMsg","Invalid Email Address");
		}
		else{
			String resetToken= UUID.randomUUID().toString();
			userService.updateUserResetToken(email,resetToken);

			//generate a URL for reset http://localhost:8081/resetPassword?token=udhbdhdhfhfd
			String url=CommonUtil.generateUrl(request)+"/resetPassword?token="+resetToken;


			boolean sendEmail= commonUtil.sendMail(url,email);
			if(sendEmail){
				session.setAttribute("succMsg","Reset Link has been sent to the email address");
			}
			else{
				session.setAttribute("errorMsg","Something went wrong");
			}
		}
		return "redirect:/forgotPassword";
	}

	@GetMapping("/resetPassword")
	public String showResetPassword(@RequestParam String token, HttpSession session,Model model){
		User user=userService.findByResetToken(token);
		if(ObjectUtils.isEmpty(user)){
			model.addAttribute("msg","Invalid URL");
			return "message";
		}
		model.addAttribute("token",token);
		return "reset_password.html";
	}
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,Model model){
		User user=userService.findByResetToken(token);
		if(ObjectUtils.isEmpty(user)){
			model.addAttribute("msg","Invalid URL");
			return "message";
		}
		else{
			user.setPassword(passwordEncoder.encode(password));
			user.setResetToken(null);
            userService.updateUser(user);
			model.addAttribute("msg","Password changed successfully");
			return "message";
		}
	}
}

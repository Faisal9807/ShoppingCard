package com.ecom.controller;

import com.ecom.model.User;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
public class HomeController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;

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
}

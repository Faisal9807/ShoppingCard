package com.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;


@Controller
public class HomeController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("/login")
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
	
}

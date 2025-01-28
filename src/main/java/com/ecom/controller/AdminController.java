package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import com.ecom.model.User;
import com.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
    private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;


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
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> category = categoryService.getAllCategory();
		m.addAttribute("categories", category);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m) {
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		boolean exitCategory = categoryService.existCategory(category.getName());
		if (exitCategory) {
			session.setAttribute("errorMsg", "Category Name Alreday Exist");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not Saved ! internal sever error");
			} else {

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				System.out.print(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Data saved successfully");
			}
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable long id, HttpSession session) {

		boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("succMsg", "Delete Data successfully");

		} else {
			session.setAttribute("errorMsg", "Something wrong");
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());

		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(oldCategory)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}
		Category updatedCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updatedCategory)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Update Category Successfully");
		} else {
			session.setAttribute("errorMsg", "Something went Wrong");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		product.setImage(imageName);

		// Set the discount Price
		product.setDiscount(product.getDiscount());

		// calculate the discount price
		int discountPrecentage = product.getDiscount();

		double orignalPrice = product.getPrice();

		double discountPrice = orignalPrice - (orignalPrice * discountPrecentage / 100);

		product.setDiscountPrice(discountPrice);

		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + image.getOriginalFilename());

			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product Save Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}

		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/product")
	public String loadViewProduct(Model m) {
		m.addAttribute("products", productService.getAllProduct());
		return "admin/product";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		boolean product = productService.deleteProduct(id);

		if (product) {
			session.setAttribute("succMsg", "Product delete Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}

		return "redirect:/admin/product";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) {

		
		Product updateProduct = productService.updateProduct(product, image);

		if (!ObjectUtils.isEmpty(updateProduct)) {
			session.setAttribute("succMsg", "Update Data SuccessFully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong");
		}

		return "redirect:/admin/editProduct/" + product.getId();
	}
}

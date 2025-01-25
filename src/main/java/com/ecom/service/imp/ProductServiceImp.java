package com.ecom.service.imp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

@Service
public class ProductServiceImp implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProduct() {
		return productRepository.findAll();
	}

	@Override
	public boolean deleteProduct(int id) {
		Product product = productRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(int id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) {

		Product oldProduct = getProductById(product.getId());
		String newImage = image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();

		// now we set new updated data in oldProdcut
		
		// Set the discount Price
		oldProduct.setDiscount(product.getDiscount());

		// calculate the discount price
		int discountPrecentage = product.getDiscount();

		double orignalPrice = product.getPrice();

		double discountPrice = orignalPrice - (orignalPrice * discountPrecentage / 100);

		oldProduct.setDiscountPrice(discountPrice);

		oldProduct.setTitle(product.getTitle());
		oldProduct.setCategory(product.getCategory());
		oldProduct.setDescription(product.getDescription());
		oldProduct.setPrice(product.getPrice());
		oldProduct.setStock(product.getStock());
		oldProduct.setIsActive(product.getIsActive());
		oldProduct.setImage(newImage);

		Product updateProduct = productRepository.save(oldProduct);

		if (!ObjectUtils.isEmpty(updateProduct)) {
			if (!image.isEmpty()) {

				try {
					File saveFile = new ClassPathResource("static/img/").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + image.getOriginalFilename());

					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return oldProduct;
		}

		return null;
	}

	@Override
	public List<Product> getAllActiveProduct(String category) {
		List<Product> activeProducts=null;
		if(ObjectUtils.isEmpty(category)) {
			activeProducts=productRepository.findByIsActiveTrue();
		}
		else {
			activeProducts=productRepository.findByCategory(category);
		}
		return activeProducts;
	}

}

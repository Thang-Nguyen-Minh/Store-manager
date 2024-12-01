package com.example.shop_manager.Service;

import com.example.shop_manager.Entity.Product;

import java.util.List;

public interface IProductService<Product> {
	void addProduct(Product product);
	void updateProduct(Product product);
	void deleteProduct(int id);
	List<Product> loadData();
	void searchProductByName(String productName);
}

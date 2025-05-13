package org.example.practica1.service;

import org.example.practica1.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(Long categoryId);
    Product getProductById(Long id);
    Product saveProduct(Product product);
    void deleteProduct(Long id);
    List<Product> searchProducts(String name);
}

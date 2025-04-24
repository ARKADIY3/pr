package org.example.practica1.impl;

import org.example.practica1.model.Product;

import java.util.List;

public interface ImplProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    void saveProduct(Product product);
    void deleteProduct(Long id);

    List<Product> searchProducts(String name);
}


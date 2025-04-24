package org.example.practica1.impl;

import org.example.practica1.model.Category;

import java.util.List;

public interface ImplCategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category saveCategory(Category category);
    void deleteCategory(Long id);
    List<Category> searchCategories(String name);
}
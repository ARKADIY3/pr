package org.example.practica1.service;

import org.example.practica1.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category saveCategory(Category category);
    void deleteCategory(Long id);
    List<Category> searchCategoriesByName(String name);
}

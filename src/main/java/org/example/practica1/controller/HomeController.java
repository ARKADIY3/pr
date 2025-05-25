package org.example.practica1.controller;

import org.example.practica1.model.Category;
import org.example.practica1.model.Product;
import org.example.practica1.service.CategoryService;
import org.example.practica1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private final CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public HomeController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Long categoryId,
            Model model
    ) {
        // Устанавливаем флаг главной страницы
        model.addAttribute("isHomePage", true);
        
        // Добавляем категории для меню
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        // Добавляем товары (все или по категории)
        List<Product> products;
        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
            // Добавляем выбранную категорию
            Category selectedCategory = categoryService.getCategoryById(categoryId);
            model.addAttribute("selectedCategory", selectedCategory);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);

        return "index";
    }

    @GetMapping("/product-details")
    public String productDetails(@RequestParam Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        
        // Добавляем категории для меню
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        
        // Устанавливаем флаг, что это не главная страница 
        // но мы всё равно хотим показать меню категорий
        model.addAttribute("isHomePage", true);
        
        return "product-details";
    }

    @GetMapping("/guide")
    public String userGuide(Model model) {
        // Добавляем категории для меню
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("isHomePage", true);
        
        return "guide";
    }
} 
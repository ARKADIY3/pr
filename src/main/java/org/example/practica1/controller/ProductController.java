package org.example.practica1.controller;

import org.example.practica1.model.Category;
import org.example.practica1.model.Product;
import org.example.practica1.service.CategoryService;
import org.example.practica1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "products/create";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            System.out.println("Product not found with id: " + id);
            return "redirect:/products"; // Или другая обработка ошибки
        }
        model.addAttribute("product", product);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "products/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute("product") Product product) {
        Category category = categoryService.getCategoryById(product.getCategory().getId());
        product.setCategory(category);
        product.setId(id);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam String name,
            Model model
    ) {
        List<Product> products = productService.searchProducts(name);
        model.addAttribute("products", products);
        return "products/list";
    }
}
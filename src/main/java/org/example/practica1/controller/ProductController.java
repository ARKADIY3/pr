package org.example.practica1.controller;

import org.example.practica1.model.Category;
import org.example.practica1.model.Product;
import org.example.practica1.service.CategoryService;
import org.example.practica1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
@PreAuthorize("hasRole('ADMIN')")
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
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Product product = new Product();
        // Установим пустую категорию, чтобы избежать NullPointerException при доступе к category.id
        product.setCategory(new Category());
        model.addAttribute("product", product);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "products/create";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryService.getCategoryById(product.getCategory().getId());
            product.setCategory(category);
        }
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
        
        // Если у товара нет категории, создадим пустую, чтобы избежать NullPointerException
        if (product.getCategory() == null) {
            product.setCategory(new Category());
        }
        
        model.addAttribute("product", product);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "products/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        product.setId(id);
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryService.getCategoryById(product.getCategory().getId());
            product.setCategory(category);
        }
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

    @GetMapping("/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "products/details";
    }
}
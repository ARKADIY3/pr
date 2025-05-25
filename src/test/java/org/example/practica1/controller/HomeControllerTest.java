package org.example.practica1.controller;

import org.example.practica1.config.TestSecurityConfig;
import org.example.practica1.model.Category;
import org.example.practica1.model.Product;
import org.example.practica1.service.CategoryService;
import org.example.practica1.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@Import(TestSecurityConfig.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    private List<Category> categories;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        categories = Arrays.asList(category1, category2);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setCategory(category1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("200.00"));
        product2.setCategory(category2);

        products = Arrays.asList(product1, product2);
    }

    @Test
    void homePageShouldDisplayAllProductsWhenNoCategorySelected() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(categories);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("categories", categories))
                .andExpect(model().attribute("products", products))
                .andExpect(model().attribute("isHomePage", true));

        verify(categoryService).getAllCategories();
        verify(productService).getAllProducts();
    }

    @Test
    void homePageShouldDisplayProductsByCategoryWhenCategorySelected() throws Exception {
        Long categoryId = 1L;
        Category selectedCategory = categories.get(0);
        List<Product> categoryProducts = Arrays.asList(products.get(0));

        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryService.getCategoryById(categoryId)).thenReturn(selectedCategory);
        when(productService.getProductsByCategory(categoryId)).thenReturn(categoryProducts);

        mockMvc.perform(get("/").param("categoryId", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("categories", categories))
                .andExpect(model().attribute("products", categoryProducts))
                .andExpect(model().attribute("selectedCategory", selectedCategory))
                .andExpect(model().attribute("isHomePage", true));

        verify(categoryService).getAllCategories();
        verify(categoryService).getCategoryById(categoryId);
        verify(productService).getProductsByCategory(categoryId);
    }

    @Test
    void productDetailsShouldDisplayProductInformation() throws Exception {
        Long productId = 1L;
        Product product = products.get(0);

        when(productService.getProductById(productId)).thenReturn(product);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/product-details").param("id", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attribute("product", product))
                .andExpect(model().attribute("categories", categories))
                .andExpect(model().attribute("isHomePage", true));

        verify(productService).getProductById(productId);
        verify(categoryService).getAllCategories();
    }
} 
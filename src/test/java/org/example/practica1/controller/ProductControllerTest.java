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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(TestSecurityConfig.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    private Product testProduct;
    private Category testCategory;
    private List<Product> products;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory(testCategory);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setCategory(testCategory);

        products = Arrays.asList(testProduct, product2);
        categories = Arrays.asList(testCategory);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listProductsShouldReturnAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", products));

        verify(productService).getAllProducts();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateFormShouldDisplayCreatePage() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/create"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("allCategories", categories));

        verify(categoryService).getAllCategories();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveProductShouldCreateNewProduct() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(productService.saveProduct(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(post("/products/save")
                        .with(csrf())
                        .param("name", "New Product")
                        .param("price", "99.99")
                        .param("category.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).saveProduct(any(Product.class));
        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditFormShouldDisplayEditPage() throws Exception {
        when(productService.getProductById(1L)).thenReturn(testProduct);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/products/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/edit"))
                .andExpect(model().attribute("product", testProduct))
                .andExpect(model().attribute("allCategories", categories));

        verify(productService).getProductById(1L);
        verify(categoryService).getAllCategories();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductShouldRemoveProduct() throws Exception {
        mockMvc.perform(get("/products/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).deleteProduct(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchProductsShouldReturnMatchingProducts() throws Exception {
        String searchTerm = "Test";
        when(productService.searchProducts(searchTerm)).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/products/search")
                        .param("name", searchTerm))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attribute("products", Arrays.asList(testProduct)));

        verify(productService).searchProducts(searchTerm);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showProductDetailsShouldDisplayProductInformation() throws Exception {
        when(productService.getProductById(1L)).thenReturn(testProduct);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/details"))
                .andExpect(model().attribute("product", testProduct));

        verify(productService).getProductById(1L);
    }

    @Test
    void unauthorizedUserShouldBeRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/products")
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
} 
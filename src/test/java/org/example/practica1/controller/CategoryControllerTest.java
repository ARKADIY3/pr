package org.example.practica1.controller;

import org.example.practica1.config.TestSecurityConfig;
import org.example.practica1.model.Category;
import org.example.practica1.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(TestSecurityConfig.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private List<Category> categories;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        categories = Arrays.asList(testCategory, category2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listCategoriesShouldReturnAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"))
                .andExpect(model().attribute("categories", categories));

        verify(categoryService).getAllCategories();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateFormShouldDisplayCreatePage() throws Exception {
        mockMvc.perform(get("/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/create"))
                .andExpect(model().attributeExists("category"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveCategoryShouldCreateNewCategory() throws Exception {
        when(categoryService.saveCategory(any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(post("/categories/save")
                        .with(csrf())
                        .param("name", "New Category"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).saveCategory(any(Category.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditFormShouldDisplayEditPage() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);

        mockMvc.perform(get("/categories/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/edit"))
                .andExpect(model().attribute("category", testCategory));

        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategoryShouldRemoveCategory() throws Exception {
        mockMvc.perform(get("/categories/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchCategoriesShouldReturnMatchingCategories() throws Exception {
        String searchTerm = "Test";
        when(categoryService.searchCategoriesByName(searchTerm))
                .thenReturn(Arrays.asList(testCategory));

        mockMvc.perform(get("/categories/search")
                        .param("name", searchTerm))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"))
                .andExpect(model().attribute("categories", Arrays.asList(testCategory)));

        verify(categoryService).searchCategoriesByName(searchTerm);
    }

    @Test
    void unauthorizedUserShouldBeRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/categories")
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
} 
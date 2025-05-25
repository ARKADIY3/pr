package org.example.practica1.controller;

import org.example.practica1.config.TestSecurityConfig;
import org.example.practica1.model.Order;
import org.example.practica1.model.Product;
import org.example.practica1.service.OrderService;
import org.example.practica1.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ProductService productService;

    private Order testOrder;
    private Product testProduct;
    private List<Order> orders;
    private List<Product> products;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerName("Test Customer");
        testOrder.setOrderDate(new Date());
        testOrder.setProduct(testProduct);
        testOrder.setQuantity(2);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomerName("Customer 2");
        order2.setOrderDate(new Date());
        order2.setProduct(testProduct);
        order2.setQuantity(1);

        orders = Arrays.asList(testOrder, order2);
        products = Arrays.asList(testProduct);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listOrdersShouldReturnAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attribute("orders", orders));

        verify(orderService).getAllOrders();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateFormShouldDisplayCreatePage() throws Exception {
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/create"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("products", products));

        verify(productService).getAllProducts();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrderShouldSaveNewOrder() throws Exception {
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        mockMvc.perform(post("/orders/save")
                        .with(csrf())
                        .param("customerName", "Test Customer")
                        .param("quantity", "2")
                        .param("product.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).createOrder(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditFormShouldDisplayEditPage() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/orders/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/edit"))
                .andExpect(model().attribute("order", testOrder))
                .andExpect(model().attribute("products", products));

        verify(orderService).getOrderById(1L);
        verify(productService).getAllProducts();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrderShouldRemoveOrder() throws Exception {
        mockMvc.perform(get("/orders/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).deleteOrder(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchOrdersShouldReturnMatchingOrders() throws Exception {
        String customerName = "Test";
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";

        when(orderService.searchOrders(eq(customerName), any(Date.class), any(Date.class)))
                .thenReturn(Arrays.asList(testOrder));

        mockMvc.perform(get("/orders/search")
                        .param("customerName", customerName)
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attribute("orders", Arrays.asList(testOrder)));

        verify(orderService).searchOrders(eq(customerName), any(Date.class), any(Date.class));
    }

    @Test
    void unauthorizedUserShouldBeRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/orders")
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
} 
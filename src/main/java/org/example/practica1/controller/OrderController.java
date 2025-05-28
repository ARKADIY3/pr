package org.example.practica1.controller;

import org.example.practica1.model.Order;
import org.example.practica1.model.Product;
import org.example.practica1.service.OrderService;
import org.example.practica1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/orders")
@PreAuthorize("hasRole('ADMIN')")
public class OrderController {
    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public OrderController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Order order = new Order();
        // Устанавливаем текущую дату для нового заказа
        order.setOrderDate(new Date());
        
        List<Product> products = productService.getAllProducts();
        System.out.println("Количество загруженных товаров: " + products.size()); // Отладочное сообщение
        
        model.addAttribute("order", order);
        model.addAttribute("allProducts", products);
        return "orders/create";
    }

    @PostMapping("/save")
    public String createOrder(@ModelAttribute Order order) {
        try {
            if (order.getProduct() != null && order.getProduct().getId() != null) {
                Product product = productService.getProductById(order.getProduct().getId());
                if (product == null) {
                    // Если товар не найден, перенаправляем обратно на форму
                    return "redirect:/orders/new?error=product_not_found";
                }
                order.setProduct(product);
            } else {
                // Если товар не выбран, перенаправляем обратно на форму
                return "redirect:/orders/new?error=product_required";
            }
            
            // Устанавливаем текущую дату для заказа, если она не установлена
            if (order.getOrderDate() == null) {
                order.setOrderDate(new Date());
            }
            
            orderService.createOrder(order);
            return "redirect:/orders";
        } catch (Exception e) {
            // Логируем ошибку и перенаправляем на форму с сообщением об ошибке
            return "redirect:/orders/new?error=save_failed";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        model.addAttribute("allProducts", productService.getAllProducts());
        return "orders/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }

    @GetMapping("/search")
    public String searchOrders(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) throws ParseException {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedStartDate = startDate != null && !startDate.isEmpty() ? dateFormat.parse(startDate) : null;
        Date parsedEndDate = endDate != null && !endDate.isEmpty() ? dateFormat.parse(endDate) : null;
        
        List<Order> orders = orderService.searchOrders(
                customerName != null ? customerName : "",
                parsedStartDate,
                parsedEndDate
        );
        
        model.addAttribute("orders", orders);
        return "orders/list";
    }
}
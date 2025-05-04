package org.example.practica1.controller;

import org.example.practica1.impl.ImplProductService;
import org.example.practica1.model.Order;
import org.example.practica1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final ImplProductService implProductService;

    @Autowired
    public OrderController(OrderService orderService, ImplProductService implProductService) {
        this.orderService = orderService;
        this.implProductService = implProductService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("allProducts", implProductService.getAllProducts());
        return "orders/create";
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order) {
        order.setOrderDate(new Date());
        orderService.saveOrder(order);
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("allProducts", implProductService.getAllProducts());
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
            Model model) {
        model.addAttribute("orders",
                orderService.searchOrders(customerName, startDate, endDate));
        return "orders/list";
    }
}
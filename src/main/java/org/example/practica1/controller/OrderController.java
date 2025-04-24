package org.example.practica1.controller;

import org.example.practica1.impl.ImplProductService;
import org.example.practica1.model.Order;
import org.example.practica1.model.Product;
import org.example.practica1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

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
        try {
            model.addAttribute("orders", orderService.getAllOrders());
            return "orders/list";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке заказов");
            return "error";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("allProducts", implProductService.getAllProducts());
        return "orders/create";
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order, Model model) {
        try {
            order.setOrderDate(new Date());
            orderService.saveOrder(order);
            return "redirect:/orders";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при сохранении заказа");
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("allProducts", implProductService.getAllProducts());
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                model.addAttribute("error", "Заказ не найден");
                return "error";
            }
            model.addAttribute("order", order);
            return "orders/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке заказа");
            return "error";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, Model model) {
        try {
            orderService.deleteOrder(id);
            return "redirect:/orders";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при удалении заказа");
            return "error";
        }
    }

    @GetMapping("/search")
    public String searchOrders(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        try {
            model.addAttribute("orders",
                    orderService.searchOrders(customerName, startDate, endDate));
            return "orders/list";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при поиске заказов");
            return "error";
        }
    }
}
package org.example.practica1.service;

import org.example.practica1.model.Order;
import java.util.List;
import java.util.Date;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order createOrder(Order order);
    void deleteOrder(Long id);
    List<Order> searchOrders(String customerName, Date startDate, Date endDate);
} 
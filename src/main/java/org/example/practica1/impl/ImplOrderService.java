package org.example.practica1.impl;

import org.example.practica1.model.Order;
import java.util.List;

public interface ImplOrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order saveOrder(Order order);
    void deleteOrder(Long id);
    List<Order> searchOrders(String customerName, String startDate, String endDate);
}

package org.example.practica1.service.impl;

import org.example.practica1.model.Order;
import org.example.practica1.repository.OrderRepository;
import org.example.practica1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    
    @Override
    public List<Order> searchOrders(String customerName, Date startDate, Date endDate) {
        return orderRepository.findByCustomerNameContainingAndOrderDateBetween(
            customerName, startDate, endDate
        );
    }
} 
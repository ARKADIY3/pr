package org.example.practica1.service;

import org.example.practica1.impl.ImplOrderService;
import org.example.practica1.model.Order;
import org.example.practica1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderService implements ImplOrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
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
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> searchOrders(String customerName, String startDate, String endDate) {
        try {
            Date parsedStartDate = startDate != null ? new SimpleDateFormat("yyyy-MM-dd").parse(startDate) : null;
            Date parsedEndDate = endDate != null ? new SimpleDateFormat("yyyy-MM-dd").parse(endDate) : null;

            return orderRepository.findByCustomerNameContainingAndOrderDateBetween(
                    customerName != null ? customerName : "",
                    parsedStartDate,
                    parsedEndDate
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске заказов", e);
        }
    }
}
package org.example.practica1.repository;

import org.example.practica1.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Date;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerNameContainingAndOrderDateBetween(
            String customerName,
            Date startDate,
            Date endDate
    );

    @EntityGraph(attributePaths = {"product"}) // Добавить эту аннотацию
    List<Order> findAll();

    @EntityGraph(attributePaths = {"product"})
    Order findById(long id);
}

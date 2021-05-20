package ru.clevertec.check.service;

import ru.clevertec.check.model.product.Order;

import java.util.List;

public interface DataOrderService {

    List<Order> getOrdersByUserId(Integer id);

    Integer removeDataOrdersByUserId(Integer id);
}

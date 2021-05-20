package ru.clevertec.check.service.impl;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.check.dao.OrderRepository;
import ru.clevertec.check.dao.UserRepository;
import ru.clevertec.check.model.order.DataOrder;
import ru.clevertec.check.model.product.Order;
import ru.clevertec.check.service.DataOrderService;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class DataOrderServiceImpl implements DataOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final Gson gson;

    @Override
    public List<Order> getOrdersByUserId(Integer id) {
        List<Order> orders = new ArrayList<>();
        if (userRepository.existsById(id) && !orderRepository.getDataOrdersByUserId(id).isEmpty()) {
            List<DataOrder> data = orderRepository.getDataOrdersByUserId(id);
            for (DataOrder order : data) {
                orders.add(gson.fromJson(order.getJson(), Order.class));
            }
            return orders;
        }
        return null;
    }

    @Transactional
    @Override
    public Integer removeDataOrdersByUserId(Integer id) {
        return orderRepository.removeDataOrdersByUserId(id);
    }
}

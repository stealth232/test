package ru.clevertec.check.service;

import ru.clevertec.check.model.product.ProductWarehouse;

import java.util.List;

public interface WarehouseService {

    Integer updateQuantityById(Integer quantity, Integer id);

    List<ProductWarehouse> findAll();
}

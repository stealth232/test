package ru.clevertec.check.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.check.dao.WarehouseRepository;
import ru.clevertec.check.model.product.ProductWarehouse;
import ru.clevertec.check.service.WarehouseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;

    @Override
    public Integer updateQuantityById(Integer quantity, Integer id) {
        return warehouseRepository.updateQuantityById(quantity, id);
    }

    @Override
    public List<ProductWarehouse> findAll(){
        return warehouseRepository.findAll();
    }
}

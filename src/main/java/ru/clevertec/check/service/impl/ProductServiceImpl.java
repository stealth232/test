package ru.clevertec.check.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.check.dao.ProductRepository;
import ru.clevertec.check.dao.WarehouseRepository;
import ru.clevertec.check.model.product.Product;
import ru.clevertec.check.service.ProductService;

import java.util.List;
import java.util.Objects;

import static ru.clevertec.check.service.CheckConstants.ZERO_INT;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Integer deleteProductByName(String name) {
        return productRepository.deleteProductByName(name);
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.getProductById(id);
    }

    @Override
    public Integer deleteProductById(Integer id) {
        warehouseRepository.deleteProductWarehouseById(id);
        return productRepository.deleteProductById(id);
    }

    @Override
    public Integer changeStockById(Integer id) {
        Product product = productRepository.getProductById(id);
        if(Objects.nonNull(product)){
            Boolean stock = null;
            if (product.isStock()) {
                stock = Boolean.FALSE;
            } else if (!product.isStock()) {
                stock = Boolean.TRUE;
            }
            return productRepository.updateStock(stock, id);
        }
        return ZERO_INT;
    }

    @Override
    public Product save(Product product) {
        if(Objects.isNull(getProductByName(product.getName()))){
            Product savedProduct = productRepository.save(product);
            warehouseRepository.saveToWarehouse(savedProduct.getId(),savedProduct.getName(),1000);
            return savedProduct;
        }
        return null;
    }

    @Override
    public Integer updateCost(Double cost, Integer id){
      return   productRepository.updateCost(cost, id);
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.getProductByName(name);
    }
}

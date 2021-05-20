package ru.clevertec.check.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.check.model.product.ProductWarehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<ProductWarehouse, Integer> {
    @Modifying
    @Query(value = "insert into warehouse (id, name, quantity) values (?, ?, ?)", nativeQuery = true)
    Integer saveToWarehouse(Integer id, String name, Integer quantity);

    Integer deleteProductWarehouseById(Integer id);

    @Query(value = "select w.quantity from warehouse w where name = ?", nativeQuery = true)
    Integer getQuantity(String name);

    @Modifying
    @Transactional
    @Query(value = "update warehouse set quantity = ? where name = ?", nativeQuery = true)
    Integer updateQuantity(Integer quantity, String name);

    @Modifying
    @Transactional
    @Query(value = "update warehouse set quantity = ? where id = ?", nativeQuery = true)
    Integer updateQuantityById(Integer quantity, Integer id);
}

package ru.clevertec.check.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.check.model.order.DataOrder;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<DataOrder, Integer> {

    List<DataOrder> getDataOrdersByUserId(Integer id);

    Integer removeDataOrdersByUserId(Integer id);
}

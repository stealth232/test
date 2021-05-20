package ru.clevertec.check.service;

import ru.clevertec.check.exception.ServiceException;
import ru.clevertec.check.model.product.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface CheckService {

    StringBuilder getTXT(Map<String, Integer> map, Integer id);

    StringBuilder getHTML(Map<String, Integer> map);

    StringBuilder getPDF(Map<String, Integer> map);

    void getPDFFromOrder(Order order);

    Order getOrder(Map<String, Integer> map);

    Map<String, Integer> selectProducts(HttpServletRequest request);

    Order sendCheckToEmail(Integer id);
}

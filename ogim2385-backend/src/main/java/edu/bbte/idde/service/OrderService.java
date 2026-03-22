package edu.bbte.idde.service;


import edu.bbte.idde.data.exception.EntityNotFoundException;
import edu.bbte.idde.data.model.Order;

import java.util.List;

public interface OrderService {
    Long addOrder(Order order);

    Order getOrder(Long id) throws EntityNotFoundException;

    List<Order> getAllOrders();

    void updateOrderStatus(Long id, String newStatus) throws EntityNotFoundException;

    void removeOrder(Long id) throws EntityNotFoundException;
}
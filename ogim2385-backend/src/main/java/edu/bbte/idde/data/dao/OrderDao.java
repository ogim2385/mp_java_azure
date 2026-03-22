package edu.bbte.idde.data.dao;

import edu.bbte.idde.data.exception.EntityNotFoundException;
import edu.bbte.idde.data.model.Order;

import java.util.List;

public interface OrderDao {
    Long create(Order order);

    Order findById(Long id) throws EntityNotFoundException;

    List<Order> findAll();

    void update(Order order) throws EntityNotFoundException;

    void delete(Long id) throws EntityNotFoundException;
}

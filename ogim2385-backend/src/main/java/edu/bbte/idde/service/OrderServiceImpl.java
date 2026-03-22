package edu.bbte.idde.service;

import edu.bbte.idde.data.dao.OrderDao;
import edu.bbte.idde.data.dao.factory.AbstractDaoFactory;
import edu.bbte.idde.data.exception.EntityNotFoundException;
import edu.bbte.idde.data.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OrderServiceImpl implements OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderDao orderDao;

    public OrderServiceImpl() {
        orderDao = AbstractDaoFactory.getFactory().getOrderDao();
    }

    @Override
    public Long addOrder(Order order) {
        LOG.debug("Attempting to add new order: {}", order);
        if (order.getPrice() < 0) {
            LOG.warn("Rejected order with negative price: {}", order.getPrice());
            throw new IllegalArgumentException("The price must be positive");
        }
        Long id = orderDao.create(order);
        LOG.info("Order added successfully: id={}", order.getId());
        return id;
    }

    @Override
    public Order getOrder(Long id) throws EntityNotFoundException {
        LOG.debug("Fetching order by id {}", id);
        return orderDao.findById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        LOG.debug("Retrieving all orders");
        return orderDao.findAll();
    }

    @Override
    public void updateOrderStatus(Long id, String newStatus) throws EntityNotFoundException {
        LOG.info("Updating order id {} with new status '{}'", id, newStatus);
        Order order = orderDao.findById(id);
        order.setStatus(newStatus);
        orderDao.update(order);
        LOG.debug("Order id {} status updated", id);
    }

    @Override
    public void removeOrder(Long id) throws EntityNotFoundException {
        LOG.info("Deleting order id {}", id);
        orderDao.delete(id);
        LOG.debug("Order id {} deleted", id);
    }
}
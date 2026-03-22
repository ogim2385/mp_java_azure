package edu.bbte.idde.data.dao;

import edu.bbte.idde.data.exception.EntityNotFoundException;
import edu.bbte.idde.data.exception.WrongArgumentException;
import edu.bbte.idde.data.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryOrderDao implements OrderDao {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryOrderDao.class);
    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private static final AtomicLong COUNTER = new AtomicLong(0);

    @Override
    public Long create(Order order) {

        order.setId(COUNTER.incrementAndGet());

        if (storage.containsKey(order.getId())) {
            LOG.warn("Attempt to create duplicate order with id {}", order.getId());
            throw new WrongArgumentException(
                    "Order with id = " + order.getId() + " already exists");
        }
        storage.put(order.getId(), order);
        LOG.info("Order created in memory storage: id={}", order.getId());
        return order.getId();
    }

    @Override
    public Order findById(Long id) throws EntityNotFoundException {
        LOG.debug("Looking up order by id: {}", id);
        Order res = storage.get(id);
        if (res == null) {
            LOG.error("Order with id {} not found in memory storage", id);
            throw new EntityNotFoundException("Order with id = " + id + " not found");
        }
        return res;
    }

    @Override
    public List<Order> findAll() {
        LOG.debug("Retrieving all orders from memory storage");
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Order order) throws EntityNotFoundException {
        if (!storage.containsKey(order.getId())) {
            LOG.error("Cannot update, order with id {} does not exist", order.getId());
            throw new EntityNotFoundException("Can't update, order with id = " + order.getId() + " does not exist");
        }
        storage.put(order.getId(), order);
        LOG.info("Order updated in memory storage: id={}", order.getId());
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        if (!storage.containsKey(id)) {
            LOG.error("Cannot delete, order with id {} does not exist", id);
            throw new EntityNotFoundException("Can't delete, order with id = " + id + " does not exist");
        }
        storage.remove(id);
        LOG.info("Order deleted from memory storage: id={}", id);
    }
}
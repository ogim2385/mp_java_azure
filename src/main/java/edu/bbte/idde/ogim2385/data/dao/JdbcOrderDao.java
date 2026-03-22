package edu.bbte.idde.ogim2385.data.dao;

import edu.bbte.idde.ogim2385.data.exception.DataAccessException;
import edu.bbte.idde.ogim2385.data.exception.EntityNotFoundException;
import edu.bbte.idde.ogim2385.data.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderDao implements OrderDao {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcOrderDao.class);
    private final DataSource dataSource;

    public JdbcOrderDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(Order order) {
        String query = "INSERT INTO orders(order_date, price, status, shipping_address) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(order.getOrderDate()));
            ps.setDouble(2, order.getPrice());
            ps.setString(3, order.getStatus());
            ps.setString(4, order.getShippingAddress());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                    LOG.info("Inserted order id {}", order.getId());
                }
            }
        } catch (SQLException e) {
            LOG.error("Insert failed", e);
            throw new DataAccessException("Database insert error", e);
        }
    }

    @Override
    public Order findById(Long id) throws EntityNotFoundException {
        String query = "SELECT * FROM orders WHERE id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOG.debug("Found order id {}", id);
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOG.error("Find by id failed", e);
            throw new DataAccessException("Database find error", e);
        }
        throw new EntityNotFoundException("Order with id = " + id + " not found");
    }

    @Override
    public List<Order> findAll() {
        String query = "SELECT * FROM orders";
        List<Order> result = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            LOG.debug("Retrieved {} orders", result.size());
        } catch (SQLException e) {
            LOG.error("Find all failed", e);
            throw new DataAccessException("Database read error", e);
        }
        return result;
    }

    @Override
    public void update(Order order) throws EntityNotFoundException {
        String query = "UPDATE orders SET order_date=?, price=?, status=?, shipping_address=? WHERE id=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(order.getOrderDate()));
            ps.setDouble(2, order.getPrice());
            ps.setString(3, order.getStatus());
            ps.setString(4, order.getShippingAddress());
            ps.setLong(5, order.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                LOG.warn("Update failed, order id {} not found", order.getId());
                throw new EntityNotFoundException("Order not found for update, id=" + order.getId());
            }
            LOG.info("Updated order id {}", order.getId());
        } catch (SQLException e) {
            LOG.error("Update failed", e);
            throw new DataAccessException("Database update error", e);
        }
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        String query = "DELETE FROM orders WHERE id=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                LOG.warn("Delete failed, order {} not found", id);
                throw new EntityNotFoundException("Order with id = " + id + " not found");
            }
            LOG.info("Deleted order id {}", id);
        } catch (SQLException e) {
            LOG.error("Delete failed", e);
            throw new DataAccessException("Database delete error", e);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order(
                rs.getDate("order_date").toLocalDate(),
                rs.getDouble("price"),
                rs.getString("status"),
                rs.getString("shipping_address")
        );
        order.setId(rs.getLong("id"));
        return order;
    }
}
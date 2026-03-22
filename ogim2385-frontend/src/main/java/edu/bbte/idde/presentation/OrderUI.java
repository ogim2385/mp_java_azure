package edu.bbte.idde.presentation;

import edu.bbte.idde.data.exception.DataAccessException;
import edu.bbte.idde.data.exception.EntityNotFoundException;
import edu.bbte.idde.data.exception.WrongArgumentException;
import edu.bbte.idde.data.model.Order;
import edu.bbte.idde.service.OrderService;
import edu.bbte.idde.service.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class OrderUI extends JFrame {
    private final transient OrderService service;
    private final DefaultListModel<Order> orderListModel = new DefaultListModel<>();
    private final JList<Order> orderList = new JList<>(orderListModel);
    private final JTextField addressField = new JTextField(20);
    private final JTextField priceField = new JTextField(10);
    private final JTextField statusField = new JTextField(10);
    private final JTextField updateStatusField = new JTextField(10);
    private static final Logger LOG = LoggerFactory.getLogger(OrderUI.class);

    public OrderUI() {
        super("Webshop Order Manager");

        service = new OrderServiceImpl();

        setLayout(new BorderLayout(10, 10));
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(orderList), BorderLayout.CENTER);

        JPanel createPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        createPanel.setBorder(BorderFactory.createTitledBorder("Create new order"));
        createPanel.add(new JLabel("Shipping address:"));
        createPanel.add(addressField);
        createPanel.add(new JLabel("Price (RON):"));
        createPanel.add(priceField);
        createPanel.add(new JLabel("Status:"));
        createPanel.add(statusField);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addOrder());
        createPanel.add(addButton);

        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update order status"));
        updatePanel.add(new JLabel("New status:"));
        updatePanel.add(updateStatusField);
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateStatus());
        updatePanel.add(updateButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelected());
        updatePanel.add(deleteButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(createPanel, BorderLayout.NORTH);
        bottomPanel.add(updatePanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 800);
        setVisible(true);
        refresh();
    }

    private void addOrder() {
        try {
            LOG.info("User triggered Add order");
            String address = addressField.getText();
            double price = Double.parseDouble(priceField.getText());
            String status = statusField.getText().isBlank() ? "Paid" : statusField.getText();

            if (address.isBlank()) {
                LOG.warn("Add order failed: empty address");
                JOptionPane.showMessageDialog(this, "Address cannot be empty!");
                return;
            }

            Order order = new Order(LocalDate.now(), price, status, address);
            order.setId(service.addOrder(order));
            LOG.info("Order successfully added, id={}", order.getId());
            clearCreateFields();
            refresh();

        } catch (NumberFormatException ex) {
            LOG.warn("Invalid price format entered: {}", priceField.getText());
            JOptionPane.showMessageDialog(this, "Price must be a valid number!");
        } catch (WrongArgumentException ex) {
            LOG.error("Order creation error: {}", ex.getMessage());
            JOptionPane.showMessageDialog(this, "The id can't be the same for 2 orders!");
        } catch (DataAccessException ex) {
            LOG.error("Database error while adding order", ex);
            JOptionPane.showMessageDialog(this,
                    "A database error occurred while adding the order. Please try again later.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearCreateFields() {
        addressField.setText("");
        priceField.setText("");
        statusField.setText("");
    }

    private void updateStatus() {
        int index = orderList.getSelectedIndex();
        if (index >= 0) {
            try {
                Long id = orderListModel.get(index).getId();
                String newStatus = updateStatusField.getText();
                LOG.info("User updates status of order id {} to '{}'", id, newStatus);
                if (newStatus.isBlank()) {
                    LOG.warn("Update failed: new status is blank");
                    JOptionPane.showMessageDialog(this, "Please provide a new status!");
                    return;
                }
                service.updateOrderStatus(id, newStatus);
                updateStatusField.setText("");
                refresh();
            } catch (EntityNotFoundException ex) {
                LOG.error("Update failed - {}", ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (DataAccessException ex) {
                LOG.error("Database error while updating order", ex);
                JOptionPane.showMessageDialog(this,
                        "A database error occurred while updating the order.",
                        "Database error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            LOG.warn("Update attempted with no selected order");
            JOptionPane.showMessageDialog(this, "Select an order before updating!");
        }
    }

    private void deleteSelected() {
        int index = orderList.getSelectedIndex();
        if (index >= 0) {
            try {
                Long id = orderListModel.get(index).getId();
                LOG.info("User deletes order id {}", id);
                service.removeOrder(id);
                refresh();
            } catch (EntityNotFoundException ex) {
                LOG.error("Delete failed: {}", ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (DataAccessException ex) {
                LOG.error("Database error while deleting order", ex);
                JOptionPane.showMessageDialog(this,
                        "A database error occurred while deleting the order.",
                        "Database error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            LOG.warn("Delete attempted with no selected order");
            JOptionPane.showMessageDialog(this, "Select an order before deleting!");
        }
    }

    private void refresh() {
        try {
            orderListModel.clear();
            List<Order> orders = service.getAllOrders();
            orders.forEach(orderListModel::addElement);
        } catch (DataAccessException ex) {
            LOG.error("Database error while refreshing order list", ex);
            JOptionPane.showMessageDialog(this,
                    "Unable to load orders from the database.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OrderUI::new);
    }
}
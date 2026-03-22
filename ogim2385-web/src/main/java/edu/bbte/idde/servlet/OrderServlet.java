package edu.bbte.idde.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.bbte.idde.adapter.LocalDateAdapter;
import edu.bbte.idde.data.exception.DataAccessException;
import edu.bbte.idde.data.exception.EntityNotFoundException;
import edu.bbte.idde.data.model.Order;
import edu.bbte.idde.service.OrderService;
import edu.bbte.idde.service.OrderServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    private transient OrderService orderService;
    private final Logger log = LoggerFactory.getLogger(OrderServlet.class);
    private final transient Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Override
    public void init() {
        orderService = new OrderServiceImpl();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                log.info("Retrieving order with id " + idParam);
                long id = Long.parseLong(idParam);
                Order order = orderService.getOrder(id);
                gson.toJson(order, out);
            } else {
                log.info("Retrieving list of orders");
                List<Order> orders = orderService.getAllOrders();
                gson.toJson(orders, out);
            }
        } catch (EntityNotFoundException e) {
            log.warn("Order not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(Map.of("error", "Order not found"), out);
        } catch (NumberFormatException e) {
            log.warn("Invalid id received");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(Map.of("error", "Invalid id"), out);
        } catch (DataAccessException e) {
            log.error("Error retrieving orders", e.getCause());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(Map.of("error", "Error retrieving data"), out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        try (BufferedReader reader = request.getReader()) {
            Order order = gson.fromJson(reader, Order.class);

            if (order == null || order.getOrderDate() == null || order.getShippingAddress() == null) {
                log.warn("Missing fields for order creation");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(Map.of("error", "Missing fields"), out);
                return;
            }

            order.setId(orderService.addOrder(order));

            response.setStatus(HttpServletResponse.SC_CREATED);
            gson.toJson(order, out);
        } catch (com.google.gson.JsonParseException e) {
            log.warn("Invalid JSON format received");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(Map.of("error", "Invalid JSON format"), out);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order data");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(Map.of("error", "Invalid order data"), out);
        } catch (DataAccessException e) {
            log.error("Error retrieving orders", e.getCause());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(Map.of("error", "Error creating order"), out);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        PrintWriter out = resp.getWriter();
        if (idParam == null) {
            log.warn("Missing id parameter");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(Map.of("error", "Missing id"), out);
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            Order updated = gson.fromJson(req.getReader(), Order.class);

            if (updated == null || updated.getOrderDate() == null || updated.getShippingAddress() == null) {
                log.warn("Missing fields");
                resp.setStatus(HttpServletResponse.SC_OK);
                gson.toJson(Map.of("error", "Missing fields"), out);
                return;
            }

            orderService.updateOrderStatus(id, updated.getStatus());

            resp.setStatus(HttpServletResponse.SC_OK);
            gson.toJson(updated, out);
        } catch (EntityNotFoundException e) {
            log.warn("Order not found");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(Map.of("error", "Order not found"), out);
        } catch (NumberFormatException e) {
            log.warn("Invalid id received");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(Map.of("error", "Invalid id"), out);
        } catch (DataAccessException e) {
            log.error("Error retrieving orders", e.getCause());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(Map.of("error", "Error while updating order status"), out);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        PrintWriter out = resp.getWriter();
        if (idParam == null) {
            log.warn("Missing id parameter");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(Map.of("error", "Missing id"), out);
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            orderService.removeOrder(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            gson.toJson("Order deleted succesfully", out);
        } catch (EntityNotFoundException e) {
            log.warn("Order not found");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(Map.of("error", "Order not found"), out);
        } catch (NumberFormatException e) {
            log.warn("Invalid id received");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(Map.of("error", "Invalid id"), out);
        } catch (DataAccessException e) {
            log.error("Error retrieving orders", e.getCause());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(Map.of("error", "Error while deleting order"), out);
        }
    }
}


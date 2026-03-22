package edu.bbte.idde.data.model;

import java.time.LocalDate;

public class Order extends BaseEntity {
    private final LocalDate orderDate;
    private final double price;
    private String status;
    private final String shippingAddress; // a gradle check keri a finalt

    public Order(LocalDate orderDate, double price, String status, String shippingAddress) {
        super();

        this.orderDate = orderDate;
        this.price = price;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID = " + id
                + ", DATE =" + orderDate
                + ", PRICE =" + price
                + ", ADDR='" + shippingAddress + '\''
                + ", STATUS='" + status + '\'';
    }
}

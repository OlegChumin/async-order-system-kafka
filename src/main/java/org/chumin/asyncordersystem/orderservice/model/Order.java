package org.chumin.asyncordersystem.orderservice.model;

public class Order {
    private String id;
    private String product;
    private int quantity;

    public Order() {}

    public Order(String id, String product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    // геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

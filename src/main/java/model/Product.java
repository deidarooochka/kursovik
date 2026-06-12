package model;

import java.math.BigDecimal;

public abstract class Product {
    protected int id;
    protected String title;
    protected BigDecimal price;
    protected int quantity;

    public Product() {}

    public Product(String title, BigDecimal price, int quantity) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public abstract String getType();
    public abstract String getDisplayName();

    @Override
    public String toString() {
        return getDisplayName();
    }
}
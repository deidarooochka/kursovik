package model;

import java.math.BigDecimal;

public class SaleItem{
    private int id;
    private int saleId;
    private int bookId;
    private int quantity;
    private BigDecimal priceAtSale;
    private String bookTitle;

    public SaleItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtSale() { return priceAtSale; }
    public void setPriceAtSale(BigDecimal priceAtSale) { this.priceAtSale = priceAtSale; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    @Override
    public String toString() {
        return bookTitle + ", кол-во: " + quantity + " (продажа №" + saleId + ")";
    }
}
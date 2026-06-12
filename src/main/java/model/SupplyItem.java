package model;
import java.sql.Timestamp;
import java.math.BigDecimal;

public class SupplyItem{
    private int id;
    private int supplyId;
    private int bookId;
    private int quantity;
    private BigDecimal costPrice;
    private String bookTitle;
    private Timestamp supplyDate;
    private String supplierName;
    private BigDecimal totalCost;

    public SupplyItem() {}

    public SupplyItem(int supplyId, int bookId, int quantity, BigDecimal costPrice) {
        this.supplyId = supplyId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.costPrice = costPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSupplyId() { return supplyId; }
    public void setSupplyId(int supplyId) { this.supplyId = supplyId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public Timestamp getSupplyDate() { return supplyDate; }
    public void setSupplyDate(Timestamp supplyDate) { this.supplyDate = supplyDate; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    @Override
    public String toString() {
        return bookTitle + ", кол-во: " + quantity + " (поставка №" + supplyId + ")";
    }
}
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Supply{
    private int id;
    private Timestamp supplyDate;
    private int supplierId;
    private BigDecimal totalCost;
    private String supplierName;

    public Supply() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Timestamp getSupplyDate() { return supplyDate; }
    public void setSupplyDate(Timestamp supplyDate) { this.supplyDate = supplyDate; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    @Override
    public String toString() {
        return "Поставка №" + id + " от " + supplyDate + " (" + supplierName + ")";
    }
}
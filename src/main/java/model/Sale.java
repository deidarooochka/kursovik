package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Sale{
    private int id;
    private Timestamp saleDate;
    private int customerId;
    private int employeeId;
    private BigDecimal totalAmount;
    private String customerName;
    private String employeeName;

    public Sale() {}
    public Sale(Timestamp saleDate, int customerId, int employeeId, BigDecimal totalAmount) {
        this.saleDate = saleDate;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.totalAmount = totalAmount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    @Override
    public String toString() {
        return "Продажа №" + id + " от " + saleDate + " на сумму " + totalAmount + " руб.";
    }
}
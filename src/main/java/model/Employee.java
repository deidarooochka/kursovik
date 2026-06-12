package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Employee extends Person{
    private String position;
    private Date birthDate;
    private BigDecimal salary;
    private String phone;

    public Employee() {}

    public Employee(String firstName, String lastName, String position, BigDecimal salary, String phone) {
        super(firstName, lastName);
        this.position = position;
        this.salary = salary;
        this.phone = phone;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String getDisplayName() {
        return getFullName() + " (" + position + ")";
    }
}
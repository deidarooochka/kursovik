package model;

import java.sql.Date;

public class Customer extends Person {
    private Date birthDate;
    private String phone;

    public Customer() {}

    public Customer(String firstName, String lastName, String phone, Date birthDate) {
        super(firstName, lastName);
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String getDisplayName() {
        if (phone != null && !phone.trim().isEmpty()) {
            return getFullName() + " (" + phone + ")";
        }
        return getFullName();
    }
}
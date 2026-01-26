package org.emiloanwithbill.dto;

import java.time.LocalDate;

public class BillCustomerDto {

    private long emiId;
    private long customerId;
    private String name;
    private String email;
    private LocalDate dob;

    public long getEmiId() {
        return emiId;
    }

    public void setEmiId(long emiId) {
        this.emiId = emiId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}

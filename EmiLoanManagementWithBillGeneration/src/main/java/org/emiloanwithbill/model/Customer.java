package org.emiloanwithbill.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.emiloanwithbill.enums.Gender;

import java.time.LocalDate;


public class Customer {
    private long customerId;
    private String firstName;
    private String lastName;
    private String email;
    @JsonFormat(shape =  JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String address;
    private Gender gender;

    public Customer() {
    }

    public Customer(long customerId, String firstName, String lastName,
                    String email, LocalDate dob, String address, Gender gender) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
        this.address = address;
        this.gender = gender;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}

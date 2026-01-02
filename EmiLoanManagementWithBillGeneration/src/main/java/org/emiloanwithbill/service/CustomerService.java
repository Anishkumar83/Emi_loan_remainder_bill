package org.emiloanwithbill.service;

import org.emiloanwithbill.model.Customer;

import java.util.List;

public interface CustomerService {
    void addCustomer(Customer customer);
    List<Customer> getAll();
    Customer getCustomerById(long id);
    void updateCustomer(Customer customer);
    void deleteCustomer(long id);
}

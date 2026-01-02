package org.emiloanwithbill.service.serviceimplementation;

import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.service.CustomerService;

import java.util.List;

public class CustomerServiceImplementation implements CustomerService {

    CustomerDao customerDao;

    public CustomerServiceImplementation(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public void addCustomer(Customer customer) {
        customerDao.insert(customer);
    }

    @Override
    public List<Customer> getAll() {
        return customerDao.getAll();
    }

    @Override
    public Customer getCustomerById(long id) {
        return customerDao.getByCustomerId(id);
    }
    @Override
    public void updateCustomer(Customer customer) {
        customerDao.update(customer);
    }

    @Override
    public void deleteCustomer(long id) {
        customerDao.delete(id);
    }
}

package org.emiloanwithbilltest.service;


import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.enums.Gender;
import org.emiloanwithbill.service.serviceimplementation.CustomerServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplementationTest {

    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerServiceImplementation customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setFirstName("Anish");
        customer.setLastName("Kumar");
        customer.setEmail("ak@gmail.com");
        customer.setDob(Date.valueOf("2001-01-01"));
        customer.setAddress("Chennai");
        customer.setGender(Gender.MALE);
    }

    @Test
    void testAddCustomer_callsDaoInsert() {

        customerService.addCustomer(customer);

        verify(customerDao, times(1)).insert(customer);
    }


}


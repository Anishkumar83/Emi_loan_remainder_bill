package org.emiloanwithbilltest.service;

import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.enums.Gender;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.service.serviceimplementation.CustomerServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        customer.setCustomerId(1L);
        customer.setFirstName("Anish");
        customer.setLastName("Kumar");
        customer.setEmail("ak@gmail.com");
        customer.setDob(LocalDate.of(1980, 1, 1));
        customer.setAddress("Chennai");
        customer.setGender(Gender.MALE);
    }


    @Test
    void addCustomer_shouldCallDaoInsert() {
        customerService.addCustomer(customer);

        verify(customerDao, times(1)).insert(customer);
        verifyNoMoreInteractions(customerDao);
    }



    @Test
    void getAll_shouldReturnCustomersFromDao() {
        List<Customer> customers = List.of(customer);
        when(customerDao.getAll()).thenReturn(customers);

        List<Customer> result = customerService.getAll();

        assertEquals(1, result.size());
        assertEquals("Anish", result.get(0).getFirstName());
        verify(customerDao, times(1)).getAll();
        verifyNoMoreInteractions(customerDao);
    }


    @Test
    void getCustomerById_shouldReturnCustomer() {
        when(customerDao.getByCustomerId(1L)).thenReturn(customer);

        Customer result = customerService.getCustomerById(1L);

        assertEquals(1L, result.getCustomerId());
        verify(customerDao, times(1)).getByCustomerId(1L);
        verifyNoMoreInteractions(customerDao);
    }



    @Test
    void updateCustomer_shouldCallDaoUpdate() {
        customerService.updateCustomer(customer);

        verify(customerDao, times(1)).update(customer);
        verifyNoMoreInteractions(customerDao);
    }


    @Test
    void deleteCustomer_shouldCallDaoDelete() {
        customerService.deleteCustomer(1L);

        verify(customerDao, times(1)).delete(1L);
        verifyNoMoreInteractions(customerDao);
    }
}

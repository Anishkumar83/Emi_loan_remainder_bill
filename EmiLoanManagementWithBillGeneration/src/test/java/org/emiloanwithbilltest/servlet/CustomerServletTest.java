package org.emiloanwithbilltest.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.service.CustomerService;
import org.emiloanwithbill.servlet.CustomerServlet;
import org.emiloanwithbill.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServletTest {

    private CustomerServlet servlet;

    @Mock
    private CustomerService customerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        servlet = new CustomerServlet(customerService);
    }

    @Test
    void doPost_success() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        ObjectMapper mapper = JsonUtil.getMapper();
        String json = mapper.writeValueAsString(customer);

        when(request.getReader()).thenReturn(new java.io.BufferedReader(new StringReader(json)));

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(request, response);

        verify(customerService).addCustomer(any(Customer.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void doPost_invalidJson_shouldReturnBadRequest() throws Exception {
        when(request.getReader())
                .thenReturn(new java.io.BufferedReader(new StringReader("invalid-json")));

        servlet.doPost(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid request body");
    }


    @Test
    void doGet_allCustomers_success() throws Exception {
        when(request.getParameter("id")).thenReturn(null);

        when(customerService.getAll())
                .thenReturn(List.of(new Customer()));

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doGet(request, response);

        verify(customerService).getAll();
        assertFalse(writer.toString().isEmpty());
    }

    @Test
    void doGet_customerById_success() throws Exception {
        when(request.getParameter("id")).thenReturn("1");

        Customer customer = new Customer();
        customer.setCustomerId(1L);

        when(customerService.getCustomerById(1L))
                .thenReturn(customer);

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doGet(request, response);

        assertTrue(writer.toString().contains("customerId"));
    }

    @Test
    void doGet_customerNotFound_shouldReturn404() throws Exception {
        when(request.getParameter("id")).thenReturn("99");
        when(customerService.getCustomerById(99L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Customer not found");
    }

    @Test
    void doGet_invalidId_shouldReturnBadRequest() throws Exception {
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid request");
    }


    @Test
    void doPut_success() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        String json = JsonUtil.getMapper().writeValueAsString(customer);
        when(request.getReader())
                .thenReturn(new java.io.BufferedReader(new StringReader(json)));

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPut(request, response);

        verify(customerService).updateCustomer(any(Customer.class));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPut_invalidBody_shouldReturnBadRequest() throws Exception {
        when(request.getReader())
                .thenReturn(new java.io.BufferedReader(new StringReader("bad")));

        servlet.doPut(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid request body");
    }


    @Test
    void doDelete_success() throws Exception {
        when(request.getParameter("id")).thenReturn("1");

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doDelete(request, response);

        verify(customerService).deleteCustomer(1L);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDelete_invalidId_shouldReturnBadRequest() throws Exception {
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doDelete(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid request");
    }
}

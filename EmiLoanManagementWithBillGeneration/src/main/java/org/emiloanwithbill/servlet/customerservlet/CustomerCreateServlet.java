package org.emiloanwithbill.servlet.customerservlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.service.CustomerService;
import org.emiloanwithbill.service.serviceimplementation.CustomerServiceImplementation;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

@WebServlet("/customer")
public class CustomerCreateServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private final ObjectMapper mapper = new ObjectMapper();
    final CustomerService customerService = new CustomerServiceImplementation(new CustomerDao());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {

        String action=req.getParameter("action");

        if(action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid action");
            return;
        }
        switch (action){
            case "create":
                createCustomer(req,resp);
                break;

            case "getAll":
                getAllCustomers(resp);
                break;

            case "getById":
                getById(req,resp);
                break;

            case "update":
                updateCustomer(req,resp);
                break;

            case "delete":
                deleteCustomer(req,resp);
                break;

            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Invalid action");
        }

    }

    private void createCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Customer customer= mapper.readValue(req.getReader(), Customer.class);
        customerService.addCustomer(customer);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    private void getAllCustomers(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(),customerService.getAll());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    private void getById(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        long customerId = Long.parseLong(req.getParameter("customerId"));
        Customer customer=customerService.getCustomerById(customerId);
        if(customer == null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("Customer not found");
            return;
        }
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(),customer);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    private void updateCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Customer customer= mapper.readValue(req.getReader(), Customer.class);
        customerService.updateCustomer(customer);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Customer updated");
    }
    private void deleteCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {}

}

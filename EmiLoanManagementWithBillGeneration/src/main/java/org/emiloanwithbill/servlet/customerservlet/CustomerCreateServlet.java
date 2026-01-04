package org.emiloanwithbill.servlet.customerservlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.service.CustomerService;
import org.emiloanwithbill.service.serviceimplementation.CustomerServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreateServlet.class);
    private static final String INVALID_ACTION ="invalid action";
    private final ObjectMapper mapper = new ObjectMapper();
    private final CustomerService customerService = new CustomerServiceImplementation(new CustomerDao());



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {

        LOGGER.info("Inside CustomerCreateServlet");
        String action=req.getParameter("action");

        if(action == null){
            LOGGER.error("action is null");
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Action parameter is missing");
                resp.getWriter().println(INVALID_ACTION);
            }catch (IOException e){
                LOGGER.error("Failed to send error",e);
            }
            return;
        }

        try {
            switch (action) {

                case "create":
                    LOGGER.info("action=create");
                    createCustomer(req, resp);
                    break;

                case "getAll":
                    LOGGER.info("action=getAll");
                    getAllCustomers(resp);
                    break;

                case "getById":
                    LOGGER.info("action=getById");
                    getById(req, resp);
                    break;

                case "update":
                    LOGGER.info("action=update");
                    updateCustomer(req, resp);
                    break;

                case "delete":
                    LOGGER.info("action=delete");
                    deleteCustomer(req, resp);
                    break;

                default:
                    LOGGER.error(INVALID_ACTION);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(INVALID_ACTION);
            }
        }catch (IOException e){
            LOGGER.error("IO error while processing action= {}",action,e);
        }

    }

    private void createCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.info("Inside createCustomer");
        Customer customer= mapper.readValue(req.getReader(), Customer.class);
        customerService.addCustomer(customer);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    private void getAllCustomers(HttpServletResponse resp) throws IOException {
        LOGGER.info("Inside getAllCustomers");
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(),customerService.getAll());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    private void getById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.info("Inside getById");
        long customerId = Long.parseLong(req.getParameter("customerId"));
        Customer customer=customerService.getCustomerById(customerId);
        if(customer == null){
            LOGGER.error("customer is null there is no such customer");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("Customer not found");
            return;
        }
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(),customer);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    private void updateCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.info("Inside updateCustomer");
        Customer customer= mapper.readValue(req.getReader(), Customer.class);
        customerService.updateCustomer(customer);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Customer updated");
    }
    private void deleteCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.info("Inside deleteCustomer");
        long customerId = Long.parseLong(req.getParameter("id"));
        customerService.deleteCustomer(customerId);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Customer deleted successfully");
    }

}

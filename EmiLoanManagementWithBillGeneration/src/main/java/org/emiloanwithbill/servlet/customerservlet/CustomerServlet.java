package org.emiloanwithbill.servlet.customerservlet;

import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.service.CustomerService;
import org.emiloanwithbill.service.serviceimplementation.CustomerServiceImplementation;
import org.emiloanwithbill.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CustomerServlet.class);

    private final CustomerService customerService;

    public CustomerServlet() {
        this.customerService =
                new CustomerServiceImplementation(new CustomerDao());
    }

    public CustomerServlet(CustomerService customerService) {
        this.customerService = customerService;
    }


    @Override
    public void doPost(HttpServletRequest req,
                       HttpServletResponse resp) {
        LOGGER.info("POST /customer");

        try {
            Customer customer =
                    JsonUtil.getMapper().readValue(req.getReader(), Customer.class);

            customerService.addCustomer(customer);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("Customer created successfully");
        } catch (IOException e) {
            LOGGER.error("Error processing POST /customer", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
            } catch (IOException ex) {
                LOGGER.error("Error processing POST /customer", ex);
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req,
                      HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                List<Customer> customers = customerService.getAll();
                JsonUtil.getMapper().writeValue(resp.getWriter(), customers);
                return;
            }

            long id = Long.parseLong(idParam);
            Customer customer = customerService.getCustomerById(id);

            if (customer == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                return;
            }

            JsonUtil.getMapper().writeValue(resp.getWriter(), customer);
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("Error processing GET /customer", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            } catch (IOException ex) {
                LOGGER.error("Error processing GET /customer", ex);
            }
        }
    }

    @Override
    public void doPut(HttpServletRequest req,
                      HttpServletResponse resp) {
        LOGGER.info("PUT /customer");

        try {
            Customer customer =
                    JsonUtil.getMapper().readValue(req.getReader(), Customer.class);

            customerService.updateCustomer(customer);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Customer updated successfully");
        } catch (IOException e) {
            LOGGER.error("Error processing PUT /customer", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
            } catch (IOException ex) {
                LOGGER.error("Error processing PUT /customer", ex);
            }
        }
    }

    @Override
    public void doDelete(HttpServletRequest req,
                         HttpServletResponse resp) {
        LOGGER.info("DELETE /customer");

        try {
            long id = Long.parseLong(req.getParameter("id"));
            customerService.deleteCustomer(id);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Customer deleted successfully");
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("Error processing DELETE /customer", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            } catch (IOException ex) {
                LOGGER.error("Error processing DELETE /customer", ex);
            }
        }
    }
}

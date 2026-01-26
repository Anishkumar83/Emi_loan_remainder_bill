package org.emiloanwithbill.servlet;

import org.emiloanwithbill.dao.CustomerDao;
import org.emiloanwithbill.model.Customer;
import org.emiloanwithbill.service.CustomerService;
import org.emiloanwithbill.service.serviceimplementation.CustomerServiceImplementation;
import org.emiloanwithbill.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

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
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("POST /customer");

        try {
            long userId = (long) req.getAttribute("userId");

            Customer customer =
                    JsonUtil.getMapper().readValue(req.getReader(), Customer.class);

            customer.setUser_id(userId);

            customerService.addCustomer(customer);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("Customer created successfully");

        } catch (Exception e) {
            LOGGER.error("Error processing POST /customer", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
            } catch (IOException ignored) {}
        }
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            long userId = (long) req.getAttribute("userId");

            String idParam = req.getParameter("id");
            if (idParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Missing required parameter: id");
                return;
            }

            long customerId = Long.parseLong(idParam);

            Customer customer = customerService.getCustomerById(customerId);

            if (customer == null || customer.getUser_id() != userId) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                return;
            }

            JsonUtil.getMapper().writeValue(resp.getWriter(), customer);

        } catch (Exception e) {
            LOGGER.error("Error processing GET /customer", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            } catch (IOException ignored) {}
        }
    }


    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("PUT /customer");

        try {
            long userId = (long) req.getAttribute("userId");
            LOGGER.info("Extracted userId from token = {}", userId);

            String body = req.getReader().lines().reduce("", (a,b) -> a + b);
            LOGGER.info("Raw request body = {}", body);

            Customer customer = JsonUtil.getMapper().readValue(body, Customer.class);
            LOGGER.info("Parsed customerId = {}", customer.getCustomerId());

            Customer existing = customerService.getCustomerById(customer.getCustomerId());
            LOGGER.info("Existing customer from DB = {}", existing != null ? "FOUND" : "NOT FOUND");

            if (existing == null || existing.getUser_id() != userId) {
                LOGGER.warn("Ownership mismatch! userId={}, DB userId={}", userId, existing != null ? existing.getUser_id() : null);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized update");
                return;
            }

            customer.setUser_id(userId);
            customerService.updateCustomer(customer);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Customer updated successfully");

        } catch (Exception e) {
            LOGGER.error("UPDATE ERROR", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("DELETE /customer");

        try {
            long userId = (long) req.getAttribute("userId");

            long id = Long.parseLong(req.getParameter("id"));

            Customer customer = customerService.getCustomerById(id);

            if (customer == null || customer.getUser_id() != userId) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized delete");
                return;
            }

            customerService.deleteCustomer(id);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Customer deleted successfully");

        } catch (Exception e) {
            LOGGER.error("Error processing DELETE /customer", e);
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            } catch (IOException ignored) {}
        }
    }
}

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

    private final CustomerService customerService =
            new CustomerServiceImplementation(new CustomerDao());


    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        LOGGER.info("POST /customer");

        Customer customer =
                JsonUtil.getMapper().readValue(req.getReader(), Customer.class);

        customerService.addCustomer(customer);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("Customer created successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        String idParam = req.getParameter("id");
        resp.setContentType("application/json");

        if (idParam == null) {
           
            List<Customer> customers = customerService.getAll();
            JsonUtil.getMapper().writeValue(resp.getWriter(), customers);
            return;
        }


        long id = Long.parseLong(idParam);
        Customer customer = customerService.getCustomerById(id);

        if (customer == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Customer not found");
            return;
        }

        JsonUtil.getMapper().writeValue(resp.getWriter(), customer);
    }


    @Override
    protected void doPut(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        LOGGER.info("PUT /customer");

        Customer customer =
                JsonUtil.getMapper().readValue(req.getReader(), Customer.class);

        customerService.updateCustomer(customer);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Customer updated successfully");
    }


    @Override
    protected void doDelete(HttpServletRequest req,
                            HttpServletResponse resp)
            throws IOException {

        LOGGER.info("DELETE /customer");

        long id = Long.parseLong(req.getParameter("id"));
        customerService.deleteCustomer(id);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Customer deleted successfully");
    }
}

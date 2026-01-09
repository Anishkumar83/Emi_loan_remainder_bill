package org.emiloanwithbill.servlet;


import org.emiloanwithbill.service.LoanService;
import org.emiloanwithbill.service.serviceimplementation.LoanServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;

@WebServlet("/loan/create")
public class LoanCreateServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoanCreateServlet.class);

    private static final String MISSING_PARAMETER="Missing parameter: ";

    private final LoanService loanService ;

    public LoanCreateServlet() {
        this.loanService = new LoanServiceImplementation();
    }

    public LoanCreateServlet(LoanService loanService) {
        this.loanService = loanService;
    }


    @Override
    public void doPost(HttpServletRequest req,
                       HttpServletResponse resp)
            throws ServletException, IOException {

        LOGGER.info("Loan create request received");

        try {
            long customerId = parseLong(req, "cus_id");
            BigDecimal principal = parseBigDecimal(req, "principal");
            BigDecimal rate = parseBigDecimal(req, "rate");
            int months = parseInt(req, "months");

            long loanId =
                    loanService.createLoan(
                            customerId, principal, rate, months);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter()
                    .write("Loan created successfully. LoanId=" + loanId);

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid loan request: {}", e.getMessage());
            try {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        e.getMessage());
            }catch (IOException ex){
                LOGGER.error("Error sending loan request: {}", ex.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error("Loan creation failed", e);
            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to create loan");
            }catch (IOException ex){
                LOGGER.error("Error sending loan failed: {}", ex.getMessage());
            }

        }
    }




    private static long parseLong(HttpServletRequest req, String param) {
        String value = req.getParameter(param);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    MISSING_PARAMETER + param);
        }
        return Long.parseLong(value);
    }

    private static int parseInt(HttpServletRequest req, String param) {
        String value = req.getParameter(param);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    MISSING_PARAMETER + param);
        }
        return Integer.parseInt(value);
    }

    private static BigDecimal parseBigDecimal(HttpServletRequest req, String param) {
        String value = req.getParameter(param);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    MISSING_PARAMETER+ param);
        }
        return new BigDecimal(value);
    }
}

package org.emiloanwithbill.servlet.customerservlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.emiloanwithbill.dto.LoanResponseDto;
import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.model.Loan;
import org.emiloanwithbill.service.LoanService;
import org.emiloanwithbill.service.serviceimplementation.LoanServiceImplementation;
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

@WebServlet("/loan/details/json")
public class LoanDetailsJsonServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoanDetailsJsonServlet.class);

    private final LoanService loanService;

    public LoanDetailsJsonServlet() {
        this.loanService = new LoanServiceImplementation();
    }

    public LoanDetailsJsonServlet(LoanService loanService) {
        this.loanService = loanService;
    }


    public static final ObjectMapper mapper = JsonUtil.getMapper();

    @Override
    public void doGet(HttpServletRequest req,
                      HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String loanIdParam = req.getParameter("loanId");
            if (loanIdParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Missing loanId");
                return;
            }

            long loanId = Long.parseLong(loanIdParam);

            Loan loan = loanService.getLoanById(loanId);
            if (loan == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Loan not found");
                return;
            }

            List<Emi> emis = loanService.getEmiSchedule(loanId);

            LoanResponseDto response =
                    new LoanResponseDto(loan, emis);

            mapper.writeValue(resp.getWriter(), response);

        } catch (Exception e) {
            LOGGER.error("Failed to fetch loans", e);
            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to fetch loan details");
            }catch (IOException ioe){
                LOGGER.error("Failed to fetch loan details", ioe);
            }

        }
    }
}


package org.emiloanwithbilltest.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.emiloanwithbill.dto.LoanResponseDto;
import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.model.Loan;
import org.emiloanwithbill.service.LoanService;
import org.emiloanwithbill.servlet.customerservlet.LoanDetailsJsonServlet;
import org.emiloanwithbill.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanDetailsJsonServletTest {

    private LoanDetailsJsonServlet servlet;

    @Mock
    private LoanService loanService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;


    @BeforeEach
    void setUp() throws Exception {
        servlet = new LoanDetailsJsonServlet(loanService);

    }

    @Test
    void doGet_success() throws Exception {
        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        when(request.getParameter("loanId")).thenReturn("1");

        Loan loan = new Loan();
        loan.setLoanId(1L);
        loan.setCustomerId(10L);
        loan.setPrincipal(500000);
        loan.setInterestRate(8.5);
        loan.setTenureMonths(240);

        Emi emi = new Emi();
        emi.setEmiId(1L);
        emi.setLoanId(1L);
        emi.setEmiAmount(5000);
        emi.setDueDate(LocalDate.now());
        emi.setStatus("PENDING");

        when(loanService.getLoanById(1L)).thenReturn(loan);
        when(loanService.getEmiSchedule(1L)).thenReturn(List.of(emi));

        servlet.doGet(request, response);

        ObjectMapper mapper = JsonUtil.getMapper();
        LoanResponseDto dto =
                mapper.readValue(responseWriter.toString(), LoanResponseDto.class);

        assertEquals(1L, dto.getLoan().getLoanId());
        assertEquals(1, dto.getEmis().size());
    }


    @Test
    void doGet_missingLoanId_shouldReturnBadRequest() throws Exception {
        when(request.getParameter("loanId")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Missing loanId");
    }

    @Test
    void doGet_loanNotFound_shouldReturnNotFound() throws Exception {
        when(request.getParameter("loanId")).thenReturn("99");
        when(loanService.getLoanById(99L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Loan not found");
    }

    @Test
    void doGet_invalidLoanId_shouldReturnServerError() throws Exception {
        when(request.getParameter("loanId")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Failed to fetch loan details");
    }

    @Test
    void doGet_serviceThrowsException_shouldReturnServerError() throws Exception {
        when(request.getParameter("loanId")).thenReturn("1");
        when(loanService.getLoanById(1L))
                .thenThrow(new RuntimeException("DB down"));

        servlet.doGet(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Failed to fetch loan details");
    }
}

package org.emiloanwithbilltest.servlet;


import org.emiloanwithbill.service.LoanService;
import org.emiloanwithbill.servlet.LoanCreateServlet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanCreateServletTest {

    @InjectMocks
    private LoanCreateServlet servlet;

    @Mock
    private LoanService loanService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;


    @Test
    void doPost_success() throws Exception {
        StringWriter responseWriter = new StringWriter();
        when(response.getWriter())
                .thenReturn(new PrintWriter(responseWriter));

        when(request.getParameter("cus_id")).thenReturn("10");
        when(request.getParameter("principal")).thenReturn("500000");
        when(request.getParameter("rate")).thenReturn("8.5");
        when(request.getParameter("months")).thenReturn("240");

        when(loanService.createLoan(
                10L,
                new BigDecimal("500000"),
                new BigDecimal("8.5"),
                240))
                .thenReturn(1L);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }


    @Test
    void doPost_missingCustomerId_shouldReturnBadRequest() throws Exception {
        when(request.getParameter("cus_id")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Missing parameter: cus_id");
    }


    @Test
    void doPost_invalidNumberFormat_shouldReturnBadRequest() throws Exception {
        when(request.getParameter("cus_id")).thenReturn("abc");

        servlet.doPost(request, response);

        verify(response).sendError(
                eq(HttpServletResponse.SC_BAD_REQUEST),
                anyString());
    }


    @Test
    void doPost_serviceThrowsException_shouldReturnServerError()
            throws Exception {

        when(request.getParameter("cus_id")).thenReturn("10");
        when(request.getParameter("principal")).thenReturn("500000");
        when(request.getParameter("rate")).thenReturn("8.5");
        when(request.getParameter("months")).thenReturn("240");

        when(loanService.createLoan(
                anyLong(),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyInt()))
                .thenThrow(new RuntimeException("DB failure"));

        servlet.doPost(request, response);

        verify(response).sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Failed to create loan");
    }
}

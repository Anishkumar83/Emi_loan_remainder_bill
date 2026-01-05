package org.emiloanwithbill.service;

import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.model.Loan;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    long createLoan(long customerId,
                    BigDecimal principal,
                    BigDecimal interestRate,
                    int months);

    Loan getLoanById(long loanId);

    List<Emi> getEmiSchedule(long loanId);
}

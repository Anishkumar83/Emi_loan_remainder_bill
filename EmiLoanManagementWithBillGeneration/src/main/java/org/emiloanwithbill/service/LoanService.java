package org.emiloanwithbill.service;

import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.model.Loan;

import java.util.List;

public interface LoanService {

    long createLoan(long customerId,
                    java.math.BigDecimal principal,
                    java.math.BigDecimal rate,
                    int months);

    Loan getLoanById(long loanId);

    List<Emi> getEmiSchedule(long loanId, int page, int size);

    boolean isCustomerOwnedByUser(long customerId, long userId);

    boolean isLoanOwnedByUser(long loanId, long userId);
}

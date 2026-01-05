package org.emiloanwithbill.dto;

import org.emiloanwithbill.model.Emi;
import org.emiloanwithbill.model.Loan;

import java.util.List;

public class LoanResponseDto {
    private Loan loan;
    private List<Emi>  emis;

    public LoanResponseDto() {
    }

    public LoanResponseDto(Loan loan, List<Emi> emis) {
        this.loan = loan;
        this.emis = emis;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public List<Emi> getEmis() {
        return emis;
    }

    public void setEmis(List<Emi> emis) {
        this.emis = emis;
    }
}

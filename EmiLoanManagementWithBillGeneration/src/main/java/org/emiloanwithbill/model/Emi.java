package org.emiloanwithbill.model;

import java.time.LocalDate;

public class Emi {

    private Long emiId;
    private Long loanId;
    private double emiAmount;
    private double interestComponent;
    private double principalComponent;
    private double outstandingBalance;
    private LocalDate dueDate;
    private String status;

    public Emi() {
    }

    public Emi(Long emiId,
               Long loanId,
               double emiAmount,
               double interestComponent,
               double principalComponent,
               double outstandingBalance,
               LocalDate dueDate,
               String status) {
        this.emiId = emiId;
        this.loanId = loanId;
        this.emiAmount = emiAmount;
        this.interestComponent = interestComponent;
        this.principalComponent = principalComponent;
        this.outstandingBalance = outstandingBalance;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Long getEmiId() {
        return emiId;
    }

    public void setEmiId(Long emiId) {
        this.emiId = emiId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public double getInterestComponent() {
        return interestComponent;
    }

    public void setInterestComponent(double interestComponent) {
        this.interestComponent = interestComponent;
    }

    public double getPrincipalComponent() {
        return principalComponent;
    }

    public void setPrincipalComponent(double principalComponent) {
        this.principalComponent = principalComponent;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

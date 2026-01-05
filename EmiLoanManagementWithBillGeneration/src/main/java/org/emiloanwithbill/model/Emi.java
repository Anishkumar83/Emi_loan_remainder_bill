package org.emiloanwithbill.model;

import java.time.LocalDate;

public class Emi {

    private Long emiId;
    private Long loanId;
    private double emiAmount;
    private double interest_component;
    private double principal_component;
    private double outstanding_balance;
    private LocalDate dueDate;
    private String status;

    public Emi() {
    }

    public Emi(Long emiId,
               Long loanId,
               double emiAmount,
               double interest_component,
               double principal_component,
               double outstanding_balance,
               LocalDate dueDate,
               String status) {
        this.emiId = emiId;
        this.loanId = loanId;
        this.emiAmount = emiAmount;
        this.interest_component = interest_component;
        this.principal_component = principal_component;
        this.outstanding_balance = outstanding_balance;
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

    public double getInterest_component() {
        return interest_component;
    }

    public void setInterest_component(double interest_component) {
        this.interest_component = interest_component;
    }

    public double getPrincipal_component() {
        return principal_component;
    }

    public void setPrincipal_component(double principal_component) {
        this.principal_component = principal_component;
    }

    public double getOutstanding_balance() {
        return outstanding_balance;
    }

    public void setOutstanding_balance(double outstanding_balance) {
        this.outstanding_balance = outstanding_balance;
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

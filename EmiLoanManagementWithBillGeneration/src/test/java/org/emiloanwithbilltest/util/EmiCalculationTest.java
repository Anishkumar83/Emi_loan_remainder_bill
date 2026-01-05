package org.emiloanwithbilltest.util;

import org.emiloanwithbill.util.EmiCalculation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmiCalculationTest {

    @DisplayName("It should calculate emi when the parameters are correct")
    @Test
    void shouldCalculateEmiForValidInputs(){
        BigDecimal principal = new BigDecimal("100000");
        BigDecimal annualRate = new BigDecimal("12");
        int tenureMonths = 12;

        BigDecimal emi = EmiCalculation.emiCalculation(principal,annualRate,tenureMonths);

        assertNotNull(emi);
        assertEquals(0,emi.compareTo(new BigDecimal("8884.88")));
    }

    @DisplayName("Should throw illegalArgument exception when one field is empty")
    @Test
    void shouldThrowExceptionWhenPrincipalIsZero(){
        BigDecimal principal = BigDecimal.ZERO;
        BigDecimal annualRate = new BigDecimal("12");

        assertThrows(IllegalArgumentException.class,()->EmiCalculation.emiCalculation(principal,annualRate,12));
    }

    @DisplayName("Calculate with correct scale")
    @Test
    void shouldCalculateEmiWithCorrectScale(){
        BigDecimal principal = new BigDecimal("100000");
        BigDecimal annualRate = new BigDecimal("12");
        int tenureMonths = 12;

        BigDecimal emi =  EmiCalculation.emiCalculation(principal,annualRate,tenureMonths);
        assertEquals(2,emi.scale());
    }
}

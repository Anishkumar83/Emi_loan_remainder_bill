package org.emiloanwithbill.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class EmiCalculation {
    private static final int SCALE =2;

    //EMI = principal*monthlyRate*(1+monthlyRate)^tenureMonths /(1+monthlyRate)^tenureMonths

    public static BigDecimal emiCalculation(BigDecimal principal,
                                            BigDecimal annualRate,
                                            int tenureMonths){
        if(principal.compareTo(BigDecimal.ZERO)<=0
                || annualRate.compareTo(BigDecimal.ZERO)<=0
                || tenureMonths<=0){
            throw new IllegalArgumentException("Invalid EMI parameters");
        }

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200),10, RoundingMode.HALF_UP);

        if(monthlyRate.compareTo(BigDecimal.ZERO)==0){
            return principal.divide(BigDecimal.valueOf(tenureMonths),SCALE,RoundingMode.HALF_UP);
        }

        BigDecimal onePlusPowerN = BigDecimal.ONE.add(monthlyRate)
                .pow(tenureMonths, MathContext.DECIMAL64);

        BigDecimal emi = principal.multiply(monthlyRate)
                .multiply(onePlusPowerN)
                .divide(onePlusPowerN.subtract(BigDecimal.ONE),SCALE,RoundingMode.HALF_UP);


        if(emi.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalStateException("Emi calculation failed");
        }
        return emi;
    }
}

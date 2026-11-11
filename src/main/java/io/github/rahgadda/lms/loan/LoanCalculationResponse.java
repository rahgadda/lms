package io.github.rahgadda.lms.loan;

import java.math.BigDecimal;

public record LoanCalculationResponse(
        BigDecimal principal,
        BigDecimal annualInterestRate,
        Integer termInMonths,
        BigDecimal interestAmount,
        BigDecimal totalAmount) {
}

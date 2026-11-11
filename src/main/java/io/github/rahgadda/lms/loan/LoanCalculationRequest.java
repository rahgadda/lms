package io.github.rahgadda.lms.loan;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LoanCalculationRequest(
        @NotNull(message = "principal is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "principal must be greater than 0")
        BigDecimal principal,

        @NotNull(message = "annualInterestRate is required")
        @DecimalMin(value = "0.0", message = "annualInterestRate must be between 0 and 100")
        @DecimalMax(value = "100.0", message = "annualInterestRate must be between 0 and 100")
        BigDecimal annualInterestRate,

        @NotNull(message = "termInMonths is required")
        @Min(value = 1, message = "termInMonths must be at least 1")
        Integer termInMonths) {
}

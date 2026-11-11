package io.github.rahgadda.lms.loan;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LoanCalculatorServiceTest {

    private final LoanCalculatorService service = new LoanCalculatorService();

    @Test
    void calculatesInterestForTypicalLoan() {
        LoanCalculationResponse response = service.calculate(new LoanCalculationRequest(
                new BigDecimal("10000.00"), new BigDecimal("5.5"), 24));

        assertThat(response.principal()).isEqualByComparingTo("10000.00");
        assertThat(response.interestAmount()).isEqualByComparingTo("1100.00");
        assertThat(response.totalAmount()).isEqualByComparingTo("11100.00");
        assertThat(response.termInMonths()).isEqualTo(24);
    }

    @Test
    void returnsZeroInterestWhenRateIsZero() {
        LoanCalculationResponse response = service.calculate(new LoanCalculationRequest(
                new BigDecimal("5000.00"), BigDecimal.ZERO, 12));

        assertThat(response.interestAmount()).isEqualByComparingTo("0.00");
        assertThat(response.totalAmount()).isEqualByComparingTo("5000.00");
    }

    @Test
    void calculatesInterestForSingleMonthTerm() {
        LoanCalculationResponse response = service.calculate(new LoanCalculationRequest(
                new BigDecimal("10000.00"), new BigDecimal("12"), 1));

        assertThat(response.interestAmount()).isEqualByComparingTo("100.00");
        assertThat(response.totalAmount()).isEqualByComparingTo("10100.00");
    }

    @Test
    void roundsInterestToTwoDecimalPlaces() {
        LoanCalculationResponse response = service.calculate(new LoanCalculationRequest(
                new BigDecimal("1234.56"), new BigDecimal("7.89"), 7));

        assertThat(response.interestAmount()).isEqualByComparingTo("56.82");
        assertThat(response.totalAmount()).isEqualByComparingTo("1291.38");
    }
}

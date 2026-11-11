package io.github.rahgadda.lms.loan;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class LoanCalculatorService {

    private static final int CURRENCY_SCALE = 2;
    private static final RoundingMode CURRENCY_ROUNDING = RoundingMode.HALF_UP;
    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public LoanCalculationResponse calculate(LoanCalculationRequest request) {
        BigDecimal principal = request.principal().setScale(CURRENCY_SCALE, CURRENCY_ROUNDING);
        BigDecimal termInYears = BigDecimal.valueOf(request.termInMonths())
                .divide(MONTHS_PER_YEAR, MathContext.DECIMAL128);
        BigDecimal rate = request.annualInterestRate().divide(ONE_HUNDRED, MathContext.DECIMAL128);

        BigDecimal interestAmount = principal.multiply(rate).multiply(termInYears)
                .setScale(CURRENCY_SCALE, CURRENCY_ROUNDING);
        BigDecimal totalAmount = principal.add(interestAmount);

        return new LoanCalculationResponse(
                principal,
                request.annualInterestRate(),
                request.termInMonths(),
                interestAmount,
                totalAmount);
    }
}

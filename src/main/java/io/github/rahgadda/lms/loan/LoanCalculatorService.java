package io.github.rahgadda.lms.loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class LoanCalculatorService {

    private static final int CURRENCY_SCALE = 2;
    private static final RoundingMode CURRENCY_ROUNDING = RoundingMode.HALF_UP;
    private static final BigDecimal DIVISOR = BigDecimal.valueOf(1200);

    public LoanCalculationResponse calculate(LoanCalculationRequest request) {
        BigDecimal principal = request.principal().setScale(CURRENCY_SCALE, CURRENCY_ROUNDING);

        // Multiply exact factors and divide once: dividing months by 12 first
        // truncates non-terminating quotients, and exact ties would round down.
        BigDecimal interestAmount = principal
                .multiply(request.annualInterestRate())
                .multiply(BigDecimal.valueOf(request.termInMonths()))
                .divide(DIVISOR, CURRENCY_SCALE, CURRENCY_ROUNDING);
        BigDecimal totalAmount = principal.add(interestAmount);

        return new LoanCalculationResponse(
                principal,
                request.annualInterestRate(),
                request.termInMonths(),
                interestAmount,
                totalAmount);
    }
}

package io.github.rahgadda.lms.loan;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loan")
public class LoanController {

    private final LoanCalculatorService calculatorService;

    public LoanController(LoanCalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/calculate")
    public LoanCalculationResponse calculate(@Valid @RequestBody LoanCalculationRequest request) {
        return calculatorService.calculate(request);
    }
}

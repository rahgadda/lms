package io.github.rahgadda.lms.loan;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoanController.class)
@Import(LoanCalculatorService.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void calculatesLoanForValidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\": 10000.0, \"annualInterestRate\": 5.5, \"termInMonths\": 24}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal").value(10000.00))
                .andExpect(jsonPath("$.annualInterestRate").value(5.5))
                .andExpect(jsonPath("$.termInMonths").value(24))
                .andExpect(jsonPath("$.interestAmount").value(1100.00))
                .andExpect(jsonPath("$.totalAmount").value(11100.00));
    }

    @Test
    void rejectsOutOfRangeFieldsWithBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\": -100, \"annualInterestRate\": 150, \"termInMonths\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(3)))
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        containsInAnyOrder("principal", "annualInterestRate", "termInMonths")));
    }

    @Test
    void rejectsMissingFieldsWithBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        containsInAnyOrder("principal", "annualInterestRate", "termInMonths")));
    }

    @Test
    void rejectsMalformedJsonWithBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed or missing request body"));
    }

    @Test
    void rejectsFractionalTermWithBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\": 10000, \"annualInterestRate\": 5.5, \"termInMonths\": 1.9}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[*].field", contains("termInMonths")));
    }

    @Test
    void rejectsWrongTypedFieldWithBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\": \"abc\", \"annualInterestRate\": 5.5, \"termInMonths\": 24}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[*].field", contains("principal")));
    }
}

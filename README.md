# lms

Simple interest loan calculator exposed as a Java REST API.

- Java 17, Spring Boot 3, Maven (Maven wrapper included — no local Maven install needed).
- Simple interest: `interest = principal * (annualInterestRate / 100) * (termInMonths / 12)`.
- Total repayment: `total = principal + interest`. Amounts are rounded to 2 decimal places (half-up).

## Build

```sh
./mvnw clean verify
```

## Run

```sh
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`.

## API

### Calculate a loan

`POST /api/v1/loan/calculate`

Request body:

| Field               | Type   | Constraint          |
|---------------------|--------|---------------------|
| `principal`         | number | required, > 0       |
| `annualInterestRate`| number | required, 0–100 (%) |
| `termInMonths`      | integer| required, >= 1      |

Example:

```sh
curl -s -X POST http://localhost:8080/api/v1/loan/calculate \
  -H 'Content-Type: application/json' \
  -d '{"principal": 10000.0, "annualInterestRate": 5.5, "termInMonths": 24}'
```

Response `200 OK`:

```json
{
  "principal": 10000.00,
  "annualInterestRate": 5.5,
  "termInMonths": 24,
  "interestAmount": 1100.00,
  "totalAmount": 11100.00
}
```

Invalid input returns `400 Bad Request` with per-field error messages:

```sh
curl -s -X POST http://localhost:8080/api/v1/loan/calculate \
  -H 'Content-Type: application/json' \
  -d '{"principal": -100, "annualInterestRate": 150, "termInMonths": 0}'
```

```json
{
  "status": 400,
  "error": "Validation failed",
  "fieldErrors": [
    {"field": "principal", "message": "principal must be greater than 0"},
    {"field": "annualInterestRate", "message": "annualInterestRate must be between 0 and 100"},
    {"field": "termInMonths", "message": "termInMonths must be at least 1"}
  ]
}
```

## Tests

```sh
./mvnw test
```

Unit tests cover the interest calculation (typical loan, zero rate, single-month term, rounding); MockMvc tests cover the endpoint's happy path and validation failures.

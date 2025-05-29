package pl.finances.finances_app.dto.requestsAndResponses;

import java.time.LocalDate;

public record ObligationRequest(String title, double amount, LocalDate dateToPay, long categoryId) {
}

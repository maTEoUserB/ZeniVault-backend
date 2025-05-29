package pl.finances.finances_app.dto.requestsAndResponses;

import java.time.LocalDate;

public record SavingsGoalRequest(String title, double currentAmount,
                                 double finalAmount, LocalDate deadline) {
}

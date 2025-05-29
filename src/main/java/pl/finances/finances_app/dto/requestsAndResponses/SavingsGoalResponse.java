package pl.finances.finances_app.dto.requestsAndResponses;

import java.time.LocalDate;

public record SavingsGoalResponse(long id, String title, double currentAmount, double finalAmmount, LocalDate deadline) {
}

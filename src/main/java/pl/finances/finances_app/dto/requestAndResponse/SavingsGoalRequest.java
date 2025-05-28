package pl.finances.finances_app.dto.requestAndResponse;

import java.time.LocalDate;

public record SavingsGoalRequest(String title, double currentAmount,
                                 double finalAmount, LocalDate deadline) {
}

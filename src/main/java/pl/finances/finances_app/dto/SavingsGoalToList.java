package pl.finances.finances_app.dto;

import java.time.LocalDate;

public record SavingsGoalToList(long id, String title, double currentAmount, double finalAmmount, LocalDate deadline) {
}

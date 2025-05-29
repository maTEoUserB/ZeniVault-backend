package pl.finances.finances_app.dto;

import java.util.List;

public record SummaryDTO(List<Double> lastWeekExpenses, Double meanOfWeeklyTransactions, Double meanOfWeeklyIncomes,
                         Double weeklyChange, Integer numberOfWeeklyExpenses, Integer numberOfWeeklyIncomes) {
}

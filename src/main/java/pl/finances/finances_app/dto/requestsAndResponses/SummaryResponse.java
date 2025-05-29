package pl.finances.finances_app.dto.requestsAndResponses;

import java.util.List;

public record SummaryResponse(List<Double> lastWeekExpenses, Double meanOfWeeklyTransactions, Double meanOfWeeklyIncomes,
                              Double weeklyChange, Integer numberOfWeeklyExpenses, Integer numberOfWeeklyIncomes) {
}

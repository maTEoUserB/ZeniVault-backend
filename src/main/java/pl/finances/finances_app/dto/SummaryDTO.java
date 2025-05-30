package pl.finances.finances_app.dto;

import pl.finances.finances_app.dto.projection.TransactionProjection;
import pl.finances.finances_app.dto.requestsAndResponsesDto.TransactionDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record SummaryDTO(List<DailyExpensesDTO> lastWeekExpenses, Double averageThisWeek, Double averageLastWeek, Double meanOfWeeklyTransactions, Double meanOfWeeklyIncomes,
                         Double weeklyChange, Integer numberOfWeeklyExpenses, Integer numberOfWeeklyIncomes, Double totalIncome, Double totalExpense, TransactionProjection biggestExpense) {
}

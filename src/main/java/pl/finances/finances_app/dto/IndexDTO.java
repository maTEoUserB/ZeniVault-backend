package pl.finances.finances_app.dto;

import java.util.List;

public record IndexDTO(double saldo, double euroSaldo, double usdSaldo, double weeklyExpenses, double meanOfWeeklyExpenses, double weeklyChange, List<CategorySummaryDTO> categories,
                       double savingsBalance, double savingsBalanceEuro, List<NearestObligationsDTO> lastObligations, List<LastTransactionsDTO> lastTransactions) {
}

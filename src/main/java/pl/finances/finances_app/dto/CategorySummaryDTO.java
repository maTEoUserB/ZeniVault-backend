package pl.finances.finances_app.dto;

public record CategorySummaryDTO(String categoryName, double totalAmount, Double budgetAmount, Double budgetProcent) {
}

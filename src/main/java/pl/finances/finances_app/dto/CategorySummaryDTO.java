package pl.finances.finances_app.dto;

public record CategorySummaryDTO(Long categoryId, String categoryName, double totalAmount, Double budgetAmount, Double budgetProcent) {
}

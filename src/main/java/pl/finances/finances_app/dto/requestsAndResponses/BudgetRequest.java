package pl.finances.finances_app.dto.requestsAndResponses;


public record BudgetRequest(String categoryName, double amountLimit) {
}

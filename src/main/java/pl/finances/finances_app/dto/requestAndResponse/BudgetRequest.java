package pl.finances.finances_app.dto.requestAndResponse;


public record BudgetRequest(String categoryName, double amountLimit) {
}

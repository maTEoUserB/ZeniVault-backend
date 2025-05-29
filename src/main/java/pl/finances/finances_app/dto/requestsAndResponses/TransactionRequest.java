package pl.finances.finances_app.dto.requestsAndResponses;

import java.time.LocalDateTime;

public record TransactionRequest(String transactionTitle, double transactionAmount, String transactionDescription,
                                 long categoryId, String transactionType, LocalDateTime transactionDate) {
}

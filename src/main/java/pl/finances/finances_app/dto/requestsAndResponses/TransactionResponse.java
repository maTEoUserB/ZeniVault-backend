package pl.finances.finances_app.dto.requestsAndResponses;

import java.time.LocalDateTime;

public record TransactionResponse(double transactionAmount, String transactionType, LocalDateTime transactionDate) {
}

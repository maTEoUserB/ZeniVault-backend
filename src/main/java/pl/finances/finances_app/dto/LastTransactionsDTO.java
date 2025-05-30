package pl.finances.finances_app.dto;

import java.sql.Timestamp;

public record LastTransactionsDTO(long id, String transactionTitle, String transactionDescription, double amount,
                                  String category, String type, Timestamp transactionDate) {
}

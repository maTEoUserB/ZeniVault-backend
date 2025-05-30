package pl.finances.finances_app.dto.projection;

import java.sql.Timestamp;

public interface TransactionProjection {
    String getTransactionTitle();
    Timestamp getTransactionDate();
    double getTransactionAmount();
}

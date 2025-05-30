package pl.finances.finances_app.dto;

import java.sql.Date;

public record DailyExpensesDTO(Date dateLabel, Double totalAmount) {
}

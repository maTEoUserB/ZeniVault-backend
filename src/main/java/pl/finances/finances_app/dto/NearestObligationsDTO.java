package pl.finances.finances_app.dto;

import java.sql.Date;

public record NearestObligationsDTO(Long id, String obligationTitle, Date dateToPay, double obligationAmount, String categoryName) {
}

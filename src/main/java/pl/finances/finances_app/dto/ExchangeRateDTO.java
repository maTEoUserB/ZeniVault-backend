package pl.finances.finances_app.dto;

import java.util.List;

public record ExchangeRateDTO(String table, String currency, String code, List<Rate> rates) {
    public record Rate(String no, String effectiveDate, double mid) {}
}

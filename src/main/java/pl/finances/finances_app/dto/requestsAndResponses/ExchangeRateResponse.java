package pl.finances.finances_app.dto.requestsAndResponses;

import java.util.List;

public record ExchangeRateResponse(String table, String currency, String code, List<Rate> rates) {
    public record Rate(String no, String effectiveDate, double mid) {}
}

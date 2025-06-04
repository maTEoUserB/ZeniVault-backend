package pl.finances.finances_app.dto;

import java.util.List;

public record AllObligationsDTO(List<NearestObligationsDTO> paidObligations, List<NearestObligationsDTO> unpaidObligations) {
}

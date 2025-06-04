package pl.finances.finances_app.dto.requestsAndResponsesDto;

import lombok.*;

/**
 * DTO for transferring saldo amount.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaldoDTO {
    private Double saldoAmount;
}

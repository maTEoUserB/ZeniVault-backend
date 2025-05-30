package pl.finances.finances_app.dto.requestsAndResponsesDto;

import lombok.*;
import java.time.LocalDate;

/**
 * DTO for transferring obligation data to the client.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ObligationDTO {
    private String obligationTitle;
    private Double obligationAmount;
    private LocalDate dateToPay;
    private long categoryId;
}

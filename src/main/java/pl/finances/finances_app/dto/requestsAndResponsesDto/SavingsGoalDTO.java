package pl.finances.finances_app.dto.requestsAndResponsesDto;


import lombok.*;

import java.time.LocalDate;

/**
 * DTO for transferring savings goal data to the client.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalDTO {
    private Long id;
    private String title;
    private Double currentAmount;
    private Double finalAmount;
    private LocalDate deadline;
}

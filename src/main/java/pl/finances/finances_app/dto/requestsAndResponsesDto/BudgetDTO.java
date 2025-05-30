package pl.finances.finances_app.dto.requestsAndResponsesDto;

import lombok.*;

/**
 * DTO for transferring budget data to the client.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
    private String categoryName;
    private Double amountLimit;
}

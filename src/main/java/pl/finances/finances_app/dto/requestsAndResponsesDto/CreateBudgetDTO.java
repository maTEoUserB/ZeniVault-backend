package pl.finances.finances_app.dto.requestsAndResponsesDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for creating a new budget.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBudgetDTO {

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Amount limit is required")
    private Double amountLimit;
}

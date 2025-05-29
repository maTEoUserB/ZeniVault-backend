package pl.finances.finances_app.dto.requestsAndResponsesDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for creating a new savings goal.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSavingsGoalDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @NotNull(message = "Current amount is required")
    private Double currentAmount;

    @NotNull(message = "Final amount is required")
    private Double finalAmount;

    @NotNull(message = "Deadline amount is required")
    private LocalDate deadline;
}

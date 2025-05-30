package pl.finances.finances_app.dto.requestsAndResponsesDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for creating a new obligation.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateObligationDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotNull(message = "Date is required")
    private LocalDate dateToPay;

    @NotNull(message = "Category id is required")
    private long categoryId;
}

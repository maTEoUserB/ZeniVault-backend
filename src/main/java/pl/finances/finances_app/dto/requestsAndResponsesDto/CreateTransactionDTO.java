package pl.finances.finances_app.dto.requestsAndResponsesDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for creating a new transaction.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String transactionTitle;

    @NotNull(message = "Amount is required")
    private double transactionAmount;

    @Size(max = 300, message = "Username must be between 0 and 300 characters")
    private String transactionDescription;

    @NotNull(message = "Category id is required")
    private long categoryId;

    @NotBlank(message = "Type is required")
    private String transactionType;

    @NotNull(message = "Date is required")
    private LocalDateTime transactionDate;
}

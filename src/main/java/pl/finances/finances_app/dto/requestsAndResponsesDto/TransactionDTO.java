package pl.finances.finances_app.dto.requestsAndResponsesDto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for transferring transaction data to the client.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String transactionTitle;
    private double transactionAmount;
    private String transactionDescription;
    private long categoryId;
    private String transactionType;
    private LocalDateTime transactionDate;
}

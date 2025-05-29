package pl.finances.finances_app.repositories.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a user transaction in the system.
 * This class defines the user account model with validation constraints and JPA annotations.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    /**
     * Unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Title of the obligation.
     * Must be between 3-50 characters.
     */
    @NotNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String transactionTitle;

    /**
     * Amount of the income/expense.
     */
    @NotNull
    private double transactionAmount;

    /**
     * Description of the transaction.
     * Must be between 0-300 characters.
     */
    @Size(max = 300, message = "Username must be between 0 and 300 characters")
    private String transactionDescription;

    /**
     * The account of the user who set the transaction.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AccountEntity userAccount;

    /**
     * Category related to the transaction.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    /**
     * Transaction type (income or expense).
     */
    @NotNull
    private String transactionType;

    /**
     * The LocalDateTime the transaction was made.
     */
    @NotNull
    private LocalDateTime transactionDate;

    public TransactionEntity(@NotNull String transactionTitle, @NotNull double transactionAmount, String transactionDescription, @NotNull AccountEntity userAccount, @NotNull CategoryEntity category, @NotNull String transactionType, @NotNull LocalDateTime transactionDate) {
        this.transactionTitle = transactionTitle;
        this.transactionAmount = transactionAmount;
        this.transactionDescription = transactionDescription;
        this.userAccount = userAccount;
        this.category = category;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }
}

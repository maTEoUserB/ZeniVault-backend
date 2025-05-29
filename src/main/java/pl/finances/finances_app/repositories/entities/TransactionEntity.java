package pl.finances.finances_app.repositories.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String transactionTitle;
    @NotNull
    private double transactionAmount;
    private String transactionDescription;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AccountEntity userAccount;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
    @NotNull
    private String transactionType; // income/expense
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

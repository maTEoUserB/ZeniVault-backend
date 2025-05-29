package pl.finances.finances_app.repositories.entities;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * Entity representing a budget in the system.
 * This class defines the budget model with validation constraints and JPA annotations.
 */
@Entity(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetEntity {
    /**
     * Unique identifier for the budget.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The account of the user who set the budget.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AccountEntity userAccount;

    /**
     * Category covered by the budget.
     */
    @OneToOne
    @JoinColumn(name = "category_id", nullable = false, unique = true)
    private CategoryEntity category;

    /**
     * Spending limit for a given category.
     */
    @NotNull
    private double amountLimit;

    public BudgetEntity(@NotNull AccountEntity userAccount, @NotNull CategoryEntity category, @NotNull double amountLimit) {
        this.userAccount = userAccount;
        this.category = category;
        this.amountLimit = amountLimit;
    }
}

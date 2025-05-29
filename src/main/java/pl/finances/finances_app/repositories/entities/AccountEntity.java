package pl.finances.finances_app.repositories.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

/**
 * Entity representing a user account in the system.
 * This class defines the user account model with validation constraints and JPA annotations.
 */
@Entity(name = "accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    /**
     * Unique identifier for the account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Username of owner of the account.
     * Must be between 8-30 characters.
     */
    @Column(unique = true, nullable = false)
    @Size(min = 8, max = 30, message = "Username must be between 8 and 30 characters")
    private String username;

    /**
     * LocalDate when the account was created in the system.
     * Automatically set during entity creation.
     */
    @Column(nullable = false)
    private LocalDate createdAt;

    /**
     * Type of user (e.g., USER, ADMIN).
     */
    @Column(nullable = false)
    private String role;

    /**
     * Account balance.
     * Indicates the total value of the user account.
     */
    @Setter
    @Column(nullable = false)
    private double saldo;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userAccount")
    private Set<TransactionEntity> transactions;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userAccount")
    private Set<ObligationEntity> obligations;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userAccount")
    private Set<SavingsGoalEntity> savingsGoals;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userAccount")
    private Set<BudgetEntity> budgets;

    public AccountEntity(@NotNull String username,@NotNull double saldo, @NotNull String role) {
        this.username = username;
        this.saldo = saldo;
        this.role = role;
        createdAt = LocalDate.now();
    }

    /**
     * Lifecycle callback executed before persisting a new user.
     * Sets the creation timestamps.
     */
    @PrePersist
    void onCreate(){createdAt = LocalDate.now();}

}

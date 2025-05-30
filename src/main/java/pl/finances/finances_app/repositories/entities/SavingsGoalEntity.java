package pl.finances.finances_app.repositories.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Entity representing a savings goal in the system.
 * This class defines the user account model with validation constraints and JPA annotations.
 */
@Entity(name = "savings_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalEntity {
    /**
     * Unique identifier for the savings goal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Title of the savings goal.
     * Must be between 3-50 characters.
     */
    @NotNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String goalTitle;

    /**
     * The account of the user who set the savings goal.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AccountEntity userAccount;

    /**
     * Amount already saved.
     */
    @NotNull
    private double currentAmount;

    /**
     * Target value for the goal.
     */
    @NotNull
    private double finalAmmount;

    /**
     * Information about achieving the goal.
     */
    @NotNull
    private boolean isDone;

    /**
     * LocalDate date by which the user wants to save finalAmount.
     */
    @NotNull
    private LocalDate goalDeadline;

    public SavingsGoalEntity(@NotNull String goalTitle, @NotNull AccountEntity userAccount, @NotNull double currentAmount, @NotNull double finalAmount, @NotNull LocalDate goalDeadline) {
        this.goalTitle = goalTitle;
        this.userAccount = userAccount;
        this.currentAmount = currentAmount;
        this.finalAmmount = finalAmount;
        this.isDone = false;
        this.goalDeadline = goalDeadline;
    }
}

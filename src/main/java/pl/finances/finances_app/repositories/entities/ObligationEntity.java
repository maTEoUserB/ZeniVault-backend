package pl.finances.finances_app.repositories.entities;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a user obligation in the system.
 * This class defines the user account model with validation constraints and JPA annotations.
 */
@Entity(name = "obligations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ObligationEntity {
    /**
     * Unique identifier for the obligation.
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
    private String obligationTitle;

    /**
     * Amount to be paid.
     */
    @NotNull
    private double obligationAmount;

    /**
     * Information whether the obligation has been paid out.
     */
    @NotNull
    private boolean isDone;

    /**
     * The account of the user who have to pay the obligation.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AccountEntity userAccount;

    /**
     * LocalDate date by which the user must pay.
     */
    @NotNull
    private LocalDate dateToPay;

    /**
     * Category related to the obligation.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;


    public ObligationEntity(@NotNull AccountEntity userAccount, @NotNull String obligationTitle, @NotNull double obligationAmount,
                            @NotNull LocalDate dateToPay, @NotNull CategoryEntity category) {
        this.userAccount = userAccount;
        this.obligationTitle = obligationTitle;
        this.obligationAmount = obligationAmount;
        this.dateToPay = dateToPay;
        this.category = category;
        this.isDone = false;
    }
}

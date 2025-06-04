package pl.finances.finances_app.repositories.entities;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Entity representing a category in the system.
 * This class defines the budget model with validation constraints and JPA annotations.
 * Categories are added automatically.
 */
@Entity(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {

    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Username of owner of the account.
     * Must be between 8-100 characters.
     */
    @NotNull
    private String categoryName;

    /**
     * Category type (income or expense).
     */
    @NotNull
    private String typeForCategory;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private Set<TransactionEntity> transactions;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private Set<ObligationEntity> obligations;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
//    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<BudgetEntity> budget;

    public CategoryEntity(@NotNull String categoryName, @NotNull String typeForCategory) {
        this.categoryName = categoryName;
        this.typeForCategory = typeForCategory;
    }
}

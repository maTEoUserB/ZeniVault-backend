package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.requestsAndResponsesDto.BudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateBudgetDTO;
import pl.finances.finances_app.repositories.BudgetRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.BudgetEntity;
import pl.finances.finances_app.repositories.entities.CategoryEntity;

import java.net.URI;

/**
 * Provides business logic for managing budgets in the system.
 */
@Service
@Transactional
public class BudgetService {
    private final UserService userService;
    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;

    /**
     * Constructs a new BudgetService with the required repository and services.
     *
     * @param userService the service for user data managing
     * @param budgetRepository the repository for budget data access
     * @param categoryService the service for category data managing
     */
    @Autowired
    public BudgetService(UserService userService, BudgetRepository budgetRepository, CategoryService categoryService) {
        this.userService = userService;
        this.budgetRepository = budgetRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public ResponseEntity<BudgetDTO> addNewBudget(Jwt jwt, @Valid CreateBudgetDTO createDto) {
        long id = userService.getUserAccountId(jwt);
        AccountEntity userAccount = userService.findUserById(id).get();
        BudgetEntity budgetEntity = budgetRepository.findBudgetEntitiesByCategory_IdAndUserAccount(createDto.getCategoryId(), userAccount);

        if (budgetEntity == null) {
            throw new EntityNotFoundException("Budget entity not found");
        }
        if (budgetEntity.getUserAccount().getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this budget.");
        }

        budgetEntity.setAmountLimit(createDto.getAmountLimit());
        budgetRepository.save(budgetEntity);

        BudgetDTO dto = new BudgetDTO(budgetEntity.getCategory().getCategoryName(), budgetEntity.getAmountLimit());

        return ResponseEntity.created(URI.create("/set/budget/" + budgetEntity.getCategory().getId())).body(dto);
    }

    @Transactional
    public void createDefaultBudgets(AccountEntity userAccount) {
        for (int i = 8; i <= 15; i++) {
            CategoryEntity category = categoryService.findCategoryById(i).orElseThrow(() -> new EntityNotFoundException("Category not found"));
            BudgetEntity budget = new BudgetEntity(userAccount, category, 0.0);
            budgetRepository.save(budget);
        }
    }
}

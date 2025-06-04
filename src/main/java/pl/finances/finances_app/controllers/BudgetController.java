package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.finances.finances_app.dto.requestsAndResponsesDto.BudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateBudgetDTO;
import pl.finances.finances_app.services.BudgetService;

/**
 * REST controller for managing budgets.
 * Provides endpoint for updating user's budgets.
 */
@Controller
public class BudgetController {
    private final BudgetService budgetService;

    /**
     * Constructs a new BudgetController with the required dependencies.
     *
     * @param budgetService the service for budget operations
     */
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Create new budget for category.
     *
     * @param jwt the authenticated user's JWT token
     * @param createDto with id of category and limit amount
     * @return a BudgetDTO object
     */
    @PostMapping("/new/budget")
    ResponseEntity<BudgetDTO> addBudget(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateBudgetDTO createDto) {
        return budgetService.addNewBudget(jwt, createDto);
    }
}

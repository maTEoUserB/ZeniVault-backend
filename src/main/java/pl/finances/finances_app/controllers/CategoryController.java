package pl.finances.finances_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.CategorySummaryDTO;
import pl.finances.finances_app.dto.CategoryToListDTO;
import pl.finances.finances_app.services.CategoryService;
import pl.finances.finances_app.services.TransactionService;

import java.util.List;
import java.util.Set;

/**
 * REST controller for managing categories.
 * Provides endpoints for searching information of categories.
 */
@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final TransactionService transactionService;

    /**
     * Constructs a new CategoryController with the required dependencies.
     *
     * @param categoryService the service for category operations
     * @param transactionService the service for transaction operations
     */
    public CategoryController(CategoryService categoryService, TransactionService transactionService) {
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    /**
     * Get categories of income.
     *
     * @return the set of categories.
     */
    @GetMapping("/incomes/categories")
    ResponseEntity<Set<CategoryToListDTO>> getIncomeCategories(){
        return categoryService.findAllCategories("income");
    }

    /**
     * Get categories of expenses.
     *
     * @return the set of categories.
     */
    @GetMapping("/expenses/categories")
    ResponseEntity<Set<CategoryToListDTO>> getExpenseCategories(){
        return categoryService.findAllCategories("expense");
    }

    /**
     * Get summary information about expenditure information by category.
     *
     * @param jwt the authenticated user's JWT token
     * @return the list of CategorySummaryDTO.
     */
    @GetMapping("/expense/categories/summary")
    ResponseEntity<List<CategorySummaryDTO>> getExpenseCategoriesSummary(@AuthenticationPrincipal Jwt jwt){
        return transactionService.findExpenseCategoriesSummary(jwt);
    }
}

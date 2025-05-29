package pl.finances.finances_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.CategorySummaryDTO;
import pl.finances.finances_app.services.CategoryService;
import pl.finances.finances_app.services.TransactionService;

import java.util.List;
import java.util.Set;

@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final TransactionService transactionService;

    public CategoryController(CategoryService categoryService, TransactionService transactionService) {
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    @GetMapping("/incomes/categories")
    ResponseEntity<Set<String>> getIncomeCategories(){
        return categoryService.findAllCategories("income");
    }

    @GetMapping("/expenses/categories")
    ResponseEntity<Set<String>> getExpenseCategories(){
        return categoryService.findAllCategories("expense");
    }

    @GetMapping("/expense/categories/summary")
    ResponseEntity<List<CategorySummaryDTO>> getExpenseCategoriesSummary(@AuthenticationPrincipal Jwt jwt){
        return transactionService.findExpenseCategoriesSummary(jwt);
    }
}

package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.LastTransactionsDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateTransactionDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.TransactionDTO;
import pl.finances.finances_app.services.TransactionService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing transactions.
 * Provides endpoints for CRUD operations and filtering transactions.
 */
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    /**
     * Constructs a new TransactionController with the required dependencies.
     *
     * @param transactionService the service for transaction operations
     */
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Create new transaction.
     *
     * @param jwt the authenticated user's JWT token
     * @param createDto with id of category and limit amount
     * @return a TransactionDTO object
     */
    @PostMapping("/new/transaction")
    ResponseEntity<TransactionDTO> createTransaction(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateTransactionDTO createDto) {
        return transactionService.createNewTransaction(jwt, createDto);
    }

    /**
     * Get all transactions.
     *
     * @param jwt the authenticated user's JWT token
     * @return a list of the LastTransactionsDTO
     */
    @GetMapping("/transactions")
    ResponseEntity<List<LastTransactionsDTO>> getTransactions(@AuthenticationPrincipal Jwt jwt) {
        return transactionService.getAllTransactions(jwt);
    }

    /**
     * Retrieves a filtered list of transactions based on optional query parameters.
     * Filters can include transaction type, category list, amount range, and date range.
     *
     * @param jwt the authenticated user's JWT token
     * @param type the type of transaction (e.g., "INCOME", "EXPENSE") [optional]
     * @param categories a list of category names to filter by [optional]
     * @param startAmount the minimum transaction amount to include [optional]
     * @param endAmount the maximum transaction amount to include [optional]
     * @param startDate the earliest transaction date to include (ISO format: yyyy-MM-dd) [optional]
     * @param endDate the latest transaction date to include (ISO format: yyyy-MM-dd) [optional]
     * @return a list of transactions matching the provided filters wrapped in a ResponseEntity
     */
    @GetMapping("/transactions/filter")
    ResponseEntity<List<LastTransactionsDTO>> filterAndGetTransactions(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Double startAmount,
            @RequestParam(required = false) Double endAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return transactionService.filterAndGetTransactions(jwt, type, categories, startAmount, endAmount, startDate, endDate);
    }

    /**
     * Delete an transaction.
     *
     * @param jwt the authenticated user's JWT token
     * @param id the ID of the transaction to delete
     * @return no content ResponseEntity
     */
    @DeleteMapping("/transaction/delete/{id}")
    ResponseEntity<?> deleteTransactions(@AuthenticationPrincipal Jwt jwt,  @PathVariable long id) {
        return transactionService.deleteTransaction(jwt, id);
    }
}

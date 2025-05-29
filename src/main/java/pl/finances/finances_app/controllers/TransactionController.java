package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.LastTransactionsDTO;
import pl.finances.finances_app.dto.requestsAndResponses.TransactionRequest;
import pl.finances.finances_app.dto.requestsAndResponses.TransactionResponse;
import pl.finances.finances_app.services.TransactionService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/new/transaction")
    ResponseEntity<TransactionResponse> createTransaction(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid TransactionRequest transaction) {
        return transactionService.createNewTransaction(jwt, transaction);
    }

    @GetMapping("/transactions")
    ResponseEntity<List<LastTransactionsDTO>> getTransactions(@AuthenticationPrincipal Jwt jwt) {
        return transactionService.getAllTransactions(jwt);
    }

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

    @DeleteMapping("/transaction/delete/{id}")
    ResponseEntity<?> getTransactions(@AuthenticationPrincipal Jwt jwt, @RequestParam long id) {
        return transactionService.deleteTransaction(jwt, id);
    }
}

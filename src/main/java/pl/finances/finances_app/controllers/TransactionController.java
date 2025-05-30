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

@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/new/transaction")
    ResponseEntity<TransactionDTO> createTransaction(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateTransactionDTO createDto) {
        return transactionService.createNewTransaction(jwt, createDto);
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
    ResponseEntity<?> deleteTransactions(@AuthenticationPrincipal Jwt jwt,  @PathVariable long id) {
        return transactionService.deleteTransaction(jwt, id);
    }
}

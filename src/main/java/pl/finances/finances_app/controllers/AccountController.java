package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.IndexDTO;
import pl.finances.finances_app.dto.SummaryDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.SaldoDTO;
import pl.finances.finances_app.services.AccountService;

/**
 * REST controller for managing users accounts.
 * Provides endpoints for operations with main and analysis information about accounts.
 */
@RestController
public class AccountController {
    private final AccountService accountService;

    /**
     * Constructs a new AccountController with the required dependencies.
     *
     * @param accountService the service for account operations
     */
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Get main information about logged-in user account.
     *
     * @param jwt the authenticated user's JWT token
     * @return the IndexDTO with information like balance, weekly expenses or savings balance.
     */
    @GetMapping("/index")
    ResponseEntity<IndexDTO> index(@AuthenticationPrincipal Jwt jwt) {
        return accountService.getMainAccountInformation(jwt);
    }

    /**
     * Get a summary of expenses and income.
     *
     * @param jwt the authenticated user's JWT token
     * @return the SummaryDTO with information like average income, total expenses or biggest expense.
     */
    @GetMapping("/summary")
    ResponseEntity<SummaryDTO> getAccountSummary(@AuthenticationPrincipal Jwt jwt){
        return accountService.getAccountSummary(jwt);
    }

    /**
     * Set saldo of user account.
     *
     * @param jwt the authenticated user's JWT token
     * @return the SaldoDTO with amount of saldo.
     */
    @PostMapping("/saldo")
    ResponseEntity<SaldoDTO> setFirsSaldo(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid SaldoDTO saldo){
        return accountService.setFirstSaldo(jwt, saldo);
    }
}

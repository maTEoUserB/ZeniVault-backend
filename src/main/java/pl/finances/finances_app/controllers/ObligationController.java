package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.AllObligationsDTO;
import pl.finances.finances_app.dto.NearestObligationsDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.BudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateBudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateObligationDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.ObligationDTO;
import pl.finances.finances_app.services.ObligationService;

import java.util.List;

/**
 * REST controller for managing obligations.
 * Provides endpoints for CRUD operations (without delete) and searching obligations.
 */
@Controller
public class ObligationController {
    private final ObligationService obligationService;

    /**
     * Constructs a new ObligationController with the required dependencies.
     *
     * @param obligationService the service for obligation operations
     */
    public ObligationController(ObligationService obligationService) {
        this.obligationService = obligationService;
    }

    /**
     * Create new obligation.
     *
     * @param jwt the authenticated user's JWT token
     * @param createDto with id of category and limit amount
     * @return a ObligationDTO object
     */
    @PostMapping("/new/obligation")
    ResponseEntity<ObligationDTO> createObligation(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateObligationDTO createDto){
        return obligationService.createNewObligation(jwt, createDto);
    }

    /**
     * Update an obligation.
     *
     * @param jwt the authenticated user's JWT token
     * @param id the ID of the obligation to update
     * @return the AllObligationsDTO
     */
    @PostMapping("/update/obligation/{id}")
    ResponseEntity<ObligationDTO> updateObligation(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return obligationService.updateObligation(jwt, id);
    }

    /**
     * Get all obligations.
     *
     * @param jwt the authenticated user's JWT token
     * @return the AllObligationsDTO with lists of paid and unpaid obligations
     */
    @GetMapping("/obligations")
    ResponseEntity<AllObligationsDTO> getObligations(@AuthenticationPrincipal Jwt jwt){
        return obligationService.getObligations(jwt);
    }

    /**
     * Delete an obligation.
     *
     * @param id the ID of the obligation to delete
     * @return no content ResponseEntity
     */
    @DeleteMapping("/obligation/delete/{id}")
    ResponseEntity<?> deleteObligation(@PathVariable long id) {
        return obligationService.deleteObligation(id);
    }
}

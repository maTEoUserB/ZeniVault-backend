package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateSavingsGoalDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.SavingsGoalDTO;
import pl.finances.finances_app.dto.SavingsGoalToList;
import pl.finances.finances_app.services.SavingsGoalService;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing savings goals.
 * Provides endpoints for CRUD operations.
 */
@RestController
public class SavingsGoalController {
    private final SavingsGoalService savingsGoalService;

    /**
     * Constructs a new SavingsGoalController with the required dependencies.
     *
     * @param savingsGoalService the service for savings goal operations
     */
    @Autowired
    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    /**
     * Create new savings goal.
     *
     * @param jwt the authenticated user's JWT token
     * @param createDto with id of category and limit amount
     * @return a SavingsGoalDTO object
     */
    @PostMapping("/new/savings_goal")
    ResponseEntity<SavingsGoalDTO> createSavingsGoal(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateSavingsGoalDTO createDto) {
        return savingsGoalService.createNewSavingsGoal(jwt, createDto);
    }

    /**
     * Get all savings goals.
     *
     * @param jwt the authenticated user's JWT token
     * @return a list of the SavingsGoalToList
     */
    @GetMapping("/savings/goals")
    ResponseEntity<List<SavingsGoalToList>> getAllSavingsGoal(@AuthenticationPrincipal Jwt jwt) {
        return savingsGoalService.getAllSavingsGoal(jwt);
    }

    /**
     * Delete an savings goal.
     *
     * @param jwt the authenticated user's JWT token
     * @param id the ID of the savings goal to delete
     * @return no content ResponseEntity
     */
    @DeleteMapping("/saving_goal/delete/{id}")
    ResponseEntity<?> deleteSavingsGoal(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        return savingsGoalService.deleteSavingGoalById(jwt, id);
    }

    /**
     * Update an savings goal.
     *
     * @param jwt the authenticated user's JWT token
     * @param id the ID of the savings goal to update
     * @param updates with objects to update
     * @return the SavingsGoalDTO
     */
    @PatchMapping("/saving_goal/update/{id}")
    ResponseEntity<SavingsGoalDTO> updateSavingGoal(@AuthenticationPrincipal Jwt jwt, @PathVariable long id, @RequestBody Map<String, Object> updates){
        return savingsGoalService.updateSavingGoal(jwt, id, updates);
    }
}

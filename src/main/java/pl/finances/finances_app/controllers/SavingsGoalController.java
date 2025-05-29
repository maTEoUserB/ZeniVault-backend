package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.requestsAndResponses.SavingsGoalRequest;
import pl.finances.finances_app.dto.requestsAndResponses.SavingsGoalResponse;
import pl.finances.finances_app.services.SavingsGoalService;

import java.util.List;
import java.util.Map;

@RestController
public class SavingsGoalController {
    private final SavingsGoalService savingsGoalService;

    @Autowired
    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @PostMapping("/new/savings_goal")
    ResponseEntity<SavingsGoalResponse> createSavingsGoal(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid SavingsGoalRequest savingsGoal) {
        return savingsGoalService.createNewSavingsGoal(jwt, savingsGoal);
    }

    @GetMapping("/savings/goals")
    ResponseEntity<List<SavingsGoalResponse>> getAllSavingsGoal(@AuthenticationPrincipal Jwt jwt) {
        return savingsGoalService.getAllSavingsGoal(jwt);
    }

    @DeleteMapping("/saving_goal/delete/{id}")
    ResponseEntity<?> deleteSavingsGoal(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        return savingsGoalService.deleteSavingGoalById(jwt, id);
    }

    @PatchMapping("/saving_goal/update/{id}")
    ResponseEntity<SavingsGoalResponse> updateSavingGoal(@AuthenticationPrincipal Jwt jwt, @PathVariable long id, @RequestBody Map<String, Object> updates){
        return savingsGoalService.updateSavingGoal(jwt, id, updates);
    }
}

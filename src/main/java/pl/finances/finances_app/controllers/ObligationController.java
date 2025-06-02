package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.NearestObligationsDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.BudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateBudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateObligationDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.ObligationDTO;
import pl.finances.finances_app.services.ObligationService;

import java.util.List;

@Controller
public class ObligationController {
    private final ObligationService obligationService;

    public ObligationController(ObligationService obligationService) {
        this.obligationService = obligationService;
    }

    @PostMapping("/new/obligation")
    ResponseEntity<ObligationDTO> createObligation(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateObligationDTO createDto){
        return obligationService.createNewObligation(jwt, createDto);
    }

    @PostMapping("/update/obligation/{id}")
    ResponseEntity<ObligationDTO> updateObligation(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return obligationService.updateObligation(jwt, id);
    }

    @GetMapping("/obligations/isdone/{done}")
    ResponseEntity<List<NearestObligationsDTO>> getObligationsDone(@AuthenticationPrincipal Jwt jwt, @PathVariable boolean done){
        return obligationService.getObligations(jwt, done);
    }

    @DeleteMapping("/obligation/delete/{id}")
    ResponseEntity<?> deleteObligation(@RequestParam long id) {
        return obligationService.deleteObligation(id);
    }
}

package pl.finances.finances_app.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.finances.finances_app.dto.NearestObligationsDTO;
import pl.finances.finances_app.dto.requestAndResponse.ObligationRequest;
import pl.finances.finances_app.dto.requestAndResponse.ObligationResponse;
import pl.finances.finances_app.services.ObligationService;

import java.util.List;

@Controller
public class ObligationController {
    private final ObligationService obligationService;

    public ObligationController(ObligationService obligationService) {
        this.obligationService = obligationService;
    }

    @PostMapping("/new/obligation")
    ResponseEntity<ObligationResponse> createObligation(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ObligationRequest obligation){
        return obligationService.createNewObligation(jwt, obligation);
    }

    @GetMapping("/obligations/isdone/{done}")
    ResponseEntity<List<NearestObligationsDTO>> getObligationsDone(@AuthenticationPrincipal Jwt jwt, @RequestParam boolean done){
        return obligationService.getObligations(jwt, done);
    }

    @DeleteMapping("/obligation/delete/{id}")
    ResponseEntity<?> deleteObligation(@RequestParam long id) {
        return obligationService.deleteObligation(id);
    }
}

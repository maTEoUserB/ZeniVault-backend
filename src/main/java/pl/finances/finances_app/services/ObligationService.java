package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.NearestObligationsDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.BudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateObligationDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.ObligationDTO;
import pl.finances.finances_app.repositories.ObligationRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.CategoryEntity;
import pl.finances.finances_app.repositories.entities.ObligationEntity;
import java.net.URI;
import java.util.List;

@Service
@Transactional
public class ObligationService {
    private final UserService userService;
    private final ObligationRepository obligationRepository;
    private final CategoryService categoryService;

    @Autowired
    public ObligationService(UserService userService, ObligationRepository obligationRepository, CategoryService categoryService) {
        this.userService = userService;
        this.obligationRepository = obligationRepository;
        this.categoryService = categoryService;
    }

    public List<NearestObligationsDTO> getNearestObligations(long id) {
        return obligationRepository.getNearest2Obligations(id);
    }

    public ResponseEntity<ObligationDTO> createNewObligation(Jwt jwt, CreateObligationDTO createDto) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        CategoryEntity category = categoryService.findCategoryById(createDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found."));

        ObligationEntity newObligation = new ObligationEntity(userAccount, createDto.getTitle(), createDto.getAmount(),
                createDto.getDateToPay(), category);
        obligationRepository.save(newObligation);

        ObligationDTO dto = new ObligationDTO(newObligation.getObligationTitle(), newObligation.getObligationAmount(),
                newObligation.getDateToPay(), category.getId());


        return ResponseEntity.created(URI.create("/new/obligation/" + newObligation.getId())).body(dto);
    }

    public ResponseEntity<List<NearestObligationsDTO>> getObligations(Jwt jwt, boolean done) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        List<NearestObligationsDTO> obligations = obligationRepository.findObligations(userAccount.getId(), done);

        return ResponseEntity.ok(obligations);
    }

    public ResponseEntity<?> deleteObligation(long id) {
        ObligationEntity obligation = obligationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Obligation not found."));

        if(obligation.isDone()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Obligation is already done");
        }

        obligationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<ObligationDTO> updateObligation(Jwt jwt, Long id) {
        ObligationEntity obligation = obligationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Obligation not found."));

        if(obligation.isDone()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Obligation is already done");
        }

        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        if(obligation.getUserAccount().getId() != userAccount.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this obligation.");
        }

        obligation.setDone(true);
        obligationRepository.save(obligation);

        ObligationDTO response = new ObligationDTO(obligation.getObligationTitle(), obligation.getObligationAmount(), obligation.getDateToPay(), obligation.getCategory().getId());

        return ResponseEntity.ok(response);
    }
}

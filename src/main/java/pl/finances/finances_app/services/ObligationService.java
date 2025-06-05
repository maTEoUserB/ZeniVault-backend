package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.AllObligationsDTO;
import pl.finances.finances_app.dto.NearestObligationsDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateObligationDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateTransactionDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.ObligationDTO;
import pl.finances.finances_app.repositories.ObligationRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.CategoryEntity;
import pl.finances.finances_app.repositories.entities.ObligationEntity;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides business logic for managing obligations in the system.
 */
@Service
@Transactional
public class ObligationService {
    private final UserService userService;
    private final ObligationRepository obligationRepository;
    private final CategoryService categoryService;
    private final TransactionService transactionService;

    /**
     * Constructs a new ObligationService with the required repositories and services.
     *
     * @param userService the service for user data managing
     * @param obligationRepository the repository for obligation data access
     * @param categoryService the service for category data managing
     * @param transactionService the service for transaction data managing
     */
    @Autowired
    public ObligationService(UserService userService, ObligationRepository obligationRepository, CategoryService categoryService, TransactionService transactionService) {
        this.userService = userService;
        this.obligationRepository = obligationRepository;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    @Transactional
    public List<NearestObligationsDTO> getNearestObligations(long id) {
        return obligationRepository.getNearest2Obligations(id);
    }

    @Transactional
    public ResponseEntity<ObligationDTO> createNewObligation(Jwt jwt, CreateObligationDTO createDto) {

        long id = userService.getUserAccountId(jwt);
        AccountEntity userAccount = userService.findUserById(id).get();
        CategoryEntity category = categoryService.findCategoryById(createDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found."));

        ObligationEntity newObligation = new ObligationEntity(userAccount, createDto.getTitle(), createDto.getAmount(),
                createDto.getDateToPay(), category);
        obligationRepository.save(newObligation);

        ObligationDTO dto = new ObligationDTO(newObligation.getObligationTitle(), newObligation.getObligationAmount(),
                newObligation.getDateToPay(), category.getId());

        return ResponseEntity.created(URI.create("/new/obligation/" + newObligation.getId())).body(dto);
    }

    @Transactional
    public ResponseEntity<AllObligationsDTO> getObligations(Jwt jwt) {

        long id = userService.getUserAccountId(jwt);

        List<NearestObligationsDTO> paidObligations = obligationRepository.findObligations(id, true);
        List<NearestObligationsDTO> unpaidObligations = obligationRepository.findObligations(id, false);

        AllObligationsDTO obligations = new AllObligationsDTO(paidObligations, unpaidObligations);

        return ResponseEntity.ok(obligations);
    }

    @Transactional
    public ResponseEntity<?> deleteObligation(long id) {
        ObligationEntity obligation = obligationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Obligation not found."));

        if(obligation.isDone()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Obligation is already done");
        }

        obligationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<ObligationDTO> updateObligation(Jwt jwt, Long id) {
        ObligationEntity obligation = obligationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Obligation not found."));

        if(obligation.isDone()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Obligation is already done");
        }
        if(obligation.getUserAccount().getId() != userService.getUserAccountId(jwt)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this obligation.");
        }

        obligation.setDone(true);
        obligationRepository.save(obligation);
        createTransactionFromObligation(jwt, obligation);

        ObligationDTO response = new ObligationDTO(obligation.getObligationTitle(), obligation.getObligationAmount(), obligation.getDateToPay(), obligation.getCategory().getId());

        return ResponseEntity.ok(response);
    }

    @Transactional
    protected void createTransactionFromObligation(Jwt jwt, ObligationEntity obligation) {
        LocalDateTime now = LocalDateTime.now();
        CreateTransactionDTO createDto = new CreateTransactionDTO(obligation.getObligationTitle(), obligation.getObligationAmount(), "", obligation.getCategory().getId(), "expense", now);
        transactionService.createNewTransaction(jwt, createDto);
    }
}

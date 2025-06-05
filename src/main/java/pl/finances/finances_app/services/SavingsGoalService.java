package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateSavingsGoalDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.SavingsGoalDTO;
import pl.finances.finances_app.dto.SavingsGoalToList;
import pl.finances.finances_app.repositories.SavingsGoalRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.SavingsGoalEntity;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides business logic for managing savings goals in the system.
 */
@Service
@Transactional
public class SavingsGoalService {
    private final UserService userService;
    private final SavingsGoalRepository savingsGoalRepository;

    /**
     * Constructs a new SavingsGoalService with the required repository and service.
     *
     * @param userService the service for user data managing
     * @param savingsGoalRepository the repository for savings goal data access
     */
    @Autowired
    public SavingsGoalService(UserService userService, SavingsGoalRepository savingsGoalRepository) {
        this.userService = userService;
        this.savingsGoalRepository = savingsGoalRepository;
    }

    @Transactional
    public ResponseEntity<SavingsGoalDTO> createNewSavingsGoal(Jwt jwt, CreateSavingsGoalDTO createDto){
        long id = userService.getUserAccountId(jwt);
        AccountEntity userAccount = userService.findUserById(id).get();

        SavingsGoalEntity newGoal = new SavingsGoalEntity(createDto.getTitle(), userAccount, createDto.getCurrentAmount(), createDto.getFinalAmount(), createDto.getDeadline());
        savingsGoalRepository.save(newGoal);

        SavingsGoalDTO dto = new SavingsGoalDTO(newGoal.getId(), newGoal.getGoalTitle(), newGoal.getCurrentAmount(),
                newGoal.getFinalAmmount(), newGoal.getGoalDeadline());

        return ResponseEntity.created(URI.create("/new/savings_goal/" + newGoal.getId())).body(dto);
    }


    @Transactional
    public double getCurrentSavingsBalance(long id) {
        AccountEntity userAccount = userService.findUserById(id).orElseThrow(() -> new EntityNotFoundException("User entity not found"));

        if(userAccount == null || userAccount.getSavingsGoals() == null || userAccount.getSavingsGoals().isEmpty()) {
            return 0.0;
        }

        return userAccount.getSavingsGoals()
                .stream()
                .mapToDouble(SavingsGoalEntity::getCurrentAmount)
                .sum();
    }

    @Transactional
    public ResponseEntity<List<SavingsGoalToList>> getAllSavingsGoal(Jwt jwt) {
        long id = userService.getUserAccountId(jwt);

        List<SavingsGoalToList> savingsGoals = new ArrayList<>();
        savingsGoalRepository.findAllByUserAccount_Id(id).forEach(sg -> savingsGoals.add(new SavingsGoalToList(sg.getId(), sg.getGoalTitle(),
                sg.getCurrentAmount(), sg.getFinalAmmount(), sg.getGoalDeadline())));

        return ResponseEntity.ok(savingsGoals);
    }

    @Transactional
    public ResponseEntity<?> deleteSavingGoalById(Jwt jwt, long id) {
        if(!savingsGoalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        long userId = userService.getUserAccountId(jwt);

        if(savingsGoalRepository.findById(id).get().getUserAccount().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this savings goal.");
        }

        savingsGoalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<SavingsGoalDTO> updateSavingGoal(Jwt jwt, long id, Map<String, Object> updates) {
        SavingsGoalEntity savingsGoal = savingsGoalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Savings goal not found."));

        long userId = userService.getUserAccountId(jwt);
        if(savingsGoal.getUserAccount().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this savings goal.");
        }

        setUpdates(savingsGoal, updates);
        savingsGoalRepository.save(savingsGoal);

        SavingsGoalDTO dto = new SavingsGoalDTO(savingsGoal.getId(), savingsGoal.getGoalTitle(), savingsGoal.getCurrentAmount(), savingsGoal.getFinalAmmount(), savingsGoal.getGoalDeadline());
        return ResponseEntity.ok(dto);
    }

    @Transactional
    protected void setUpdates(SavingsGoalEntity savingsGoal, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch(key) {
                case "goalTitle" -> savingsGoal.setGoalTitle((String) value);
                case "currentAmount" -> savingsGoal.setCurrentAmount(Double.parseDouble(value.toString()));
                case "finalAmount" -> savingsGoal.setFinalAmmount(Double.parseDouble(value.toString()));
                case "goalDeadline" -> savingsGoal.setGoalDeadline(LocalDate.parse((String) value));
                default -> throw new IllegalArgumentException("Unknown key " + key);
            }
        });
    }
}

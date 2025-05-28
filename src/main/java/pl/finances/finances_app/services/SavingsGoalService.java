package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.SavingsGoalDTO;
import pl.finances.finances_app.dto.requestAndResponse.SavingsGoalRequest;
import pl.finances.finances_app.dto.requestAndResponse.SavingsGoalResponse;
import pl.finances.finances_app.repositories.SavingsGoalRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.SavingsGoalEntity;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SavingsGoalService {
    private final UserService userService;
    private final SavingsGoalRepository savingsGoalRepository;

    @Autowired
    public SavingsGoalService(UserService userService, SavingsGoalRepository savingsGoalRepository) {
        this.userService = userService;
        this.savingsGoalRepository = savingsGoalRepository;
    }


    public ResponseEntity<SavingsGoalResponse> createNewSavingsGoal(Jwt jwt, SavingsGoalRequest savingsGoal){
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        SavingsGoalEntity newSavingsGoal = new SavingsGoalEntity(savingsGoal.title(), userAccount, savingsGoal.currentAmount(), savingsGoal.finalAmount(), savingsGoal.deadline());
        savingsGoalRepository.save(newSavingsGoal);

        SavingsGoalResponse response = new SavingsGoalResponse(newSavingsGoal.getId(), newSavingsGoal.getGoalTitle(), newSavingsGoal.getCurrentAmount(),
                newSavingsGoal.getFinalAmmount(), newSavingsGoal.getGoalDeadline());

        return ResponseEntity.created(URI.create("/new/savings_goal/" + newSavingsGoal.getId())).body(response);
    }

    public SavingsGoalDTO findLastSavingsGoal(long id) {
        return savingsGoalRepository.findFirstByUserAccount_IdOrderByGoalDeadlineAsc(id)
                .orElseThrow(() -> new EntityNotFoundException("Savings goal not found."));
    }


    public double getCurrentSavingsBalance(AccountEntity userAccount) {
        if(userAccount == null || userAccount.getSavingsGoals() == null || userAccount.getSavingsGoals().isEmpty()) {
            return 0.0;
        }

        return userAccount.getSavingsGoals()
                .stream()
                .mapToDouble(SavingsGoalEntity::getCurrentAmount)
                .sum();
    }

    public ResponseEntity<List<SavingsGoalResponse>> getAllSavingsGoal(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        List<SavingsGoalResponse> savingsGoals = new ArrayList<>();
        savingsGoalRepository.findAllByUserAccount_Id(userAccount.getId()).forEach(sg -> savingsGoals.add(new SavingsGoalResponse(sg.getId(), sg.getGoalTitle(),
                sg.getCurrentAmount(), sg.getFinalAmmount(), sg.getGoalDeadline())));

        return ResponseEntity.ok(savingsGoals);
    }

    public ResponseEntity<?> deleteSavingGoalById(Jwt jwt, long id) {
        if(!savingsGoalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        if(savingsGoalRepository.findById(id).get().getUserAccount().getId() != userAccount.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this savings goal.");
        }

        savingsGoalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<SavingsGoalResponse> updateSavingGoal(Jwt jwt, long id, Map<String, Object> updates) {
        SavingsGoalEntity savingsGoal = savingsGoalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Savings goal not found."));

        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        if(savingsGoal.getUserAccount().getId() != userAccount.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this savings goal.");
        }

        updates.forEach((key, value) -> {
            switch(key) {
                case "goalTitle" -> savingsGoal.setGoalTitle((String) value);
                case "currentAmount" -> savingsGoal.setCurrentAmount(Double.parseDouble(value.toString()));
                case "finalAmount" -> savingsGoal.setFinalAmmount(Double.parseDouble(value.toString()));
                case "goalDeadline" -> savingsGoal.setGoalDeadline(LocalDate.parse((String) value));
                default -> throw new IllegalArgumentException("Unknown key " + key);
            }
        });

        savingsGoalRepository.save(savingsGoal);
        SavingsGoalResponse response = new SavingsGoalResponse(savingsGoal.getId(), savingsGoal.getGoalTitle(), savingsGoal.getCurrentAmount(), savingsGoal.getFinalAmmount(), savingsGoal.getGoalDeadline());
        return ResponseEntity.ok(response);
    }
}

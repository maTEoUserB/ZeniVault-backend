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

    public ResponseEntity<SavingsGoalDTO> createNewSavingsGoal(Jwt jwt, CreateSavingsGoalDTO createDto){
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        SavingsGoalEntity newGoal = new SavingsGoalEntity(createDto.getTitle(), userAccount, createDto.getCurrentAmount(), createDto.getFinalAmount(), createDto.getDeadline());
        savingsGoalRepository.save(newGoal);

        SavingsGoalDTO dto = new SavingsGoalDTO(newGoal.getId(), newGoal.getGoalTitle(), newGoal.getCurrentAmount(),
                newGoal.getFinalAmmount(), newGoal.getGoalDeadline());

//        SavingsGoalResponse response = new SavingsGoalResponse(newSavingsGoal.getId(), newSavingsGoal.getGoalTitle(), newSavingsGoal.getCurrentAmount(),
//                newSavingsGoal.getFinalAmmount(), newSavingsGoal.getGoalDeadline());

        return ResponseEntity.created(URI.create("/new/savings_goal/" + newGoal.getId())).body(dto);
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

    public ResponseEntity<List<SavingsGoalToList>> getAllSavingsGoal(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        List<SavingsGoalToList> savingsGoals = new ArrayList<>();
        savingsGoalRepository.findAllByUserAccount_Id(userAccount.getId()).forEach(sg -> savingsGoals.add(new SavingsGoalToList(sg.getId(), sg.getGoalTitle(),
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

    public ResponseEntity<SavingsGoalDTO> updateSavingGoal(Jwt jwt, long id, Map<String, Object> updates) {
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
        SavingsGoalDTO dto = new SavingsGoalDTO(savingsGoal.getId(), savingsGoal.getGoalTitle(), savingsGoal.getCurrentAmount(), savingsGoal.getFinalAmmount(), savingsGoal.getGoalDeadline());
        return ResponseEntity.ok(dto);
    }
}

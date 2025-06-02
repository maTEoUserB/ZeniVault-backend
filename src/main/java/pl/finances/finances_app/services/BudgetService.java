package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.requestsAndResponsesDto.BudgetDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateBudgetDTO;
import pl.finances.finances_app.repositories.BudgetRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.BudgetEntity;

import java.net.URI;

@Service
@Transactional
public class BudgetService {
    private final UserService userService;
    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;

    @Autowired
    public BudgetService(UserService userService, BudgetRepository budgetRepository, CategoryService categoryService) {
        this.userService = userService;
        this.budgetRepository = budgetRepository;
        this.categoryService = categoryService;
    }


    public ResponseEntity<BudgetDTO> addNewBudget(Jwt jwt, @Valid CreateBudgetDTO createDto) {
        BudgetEntity budgetEntity = budgetRepository.findBudgetEntitiesByCategory_Id(createDto.getCategoryId());

        if(budgetEntity == null) {
            throw new EntityNotFoundException("Budget entity not found");
        }

        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        if(budgetEntity.getUserAccount().getId() != userAccount.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this budget.");
        }

        budgetEntity.setAmountLimit(createDto.getAmountLimit());
        budgetRepository.save(budgetEntity);

        BudgetDTO dto = new BudgetDTO(budgetEntity.getCategory().getCategoryName(), budgetEntity.getAmountLimit());

        return ResponseEntity.created(URI.create("/set/budget/" + budgetEntity.getCategory())).body(dto);
    }
}

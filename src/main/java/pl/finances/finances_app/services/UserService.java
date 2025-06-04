package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.finances.finances_app.repositories.AccountRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import java.util.Optional;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Provides business logic for managing users in the system.
 */
@Service
@Transactional
public class UserService {
    private final AccountRepository userRepository;
    private BudgetService budgetService;

    /**
     * Constructs a new UserService with the required repository and service.
     *
     * @param userRepository the repository for user data access
     */
    @Autowired
    public UserService(AccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Setter to inject the budget service object when needed.
     *
     * @param budgetService the service for budget data managing
     */
    @Autowired
    public void setBudgetService(@Lazy BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    private AccountEntity getOrCreateUserAccount(String username){
        return userRepository.findByUsername(username).orElseGet(()-> {
            AccountEntity userAccount = new AccountEntity(username, 0.0, "USER");
            AccountEntity newUserAccount = userRepository.save(userAccount);
            userRepository.flush();

            budgetService.createDefaultBudgets(userAccount);

            return newUserAccount;
        });
    }

    public long getUserAccountId(Jwt jwt){
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = getOrCreateUserAccount(username);

        if(userAccount == null){
            throw new EntityNotFoundException("User entity not found.");
        }

        return userAccount.getId();
    }

    public Double getUserSaldo(long id){
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User entity not found")).getSaldo();
    }

    public Optional<AccountEntity> findUserById(long id) {
        return userRepository.findById(id);
    }

    public boolean existsUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}

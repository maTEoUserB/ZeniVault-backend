package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.*;
import pl.finances.finances_app.dto.requestAndResponse.TransactionRequest;
import pl.finances.finances_app.dto.requestAndResponse.TransactionResponse;
import pl.finances.finances_app.repositories.TransactionRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.CategoryEntity;
import pl.finances.finances_app.repositories.entities.TransactionEntity;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Autowired
    public TransactionService(UserService userService, TransactionRepository transactionRepository, CategoryService categoryService) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    public ResponseEntity<TransactionResponse> createNewTransaction(Jwt jwt, TransactionRequest transaction) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        CategoryEntity category = categoryService.findCategoryById(transaction.categoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found."));
        TransactionEntity newTransaction = new TransactionEntity(transaction.transactionTitle(), transaction.transactionAmount(), transaction.transactionDescription(),
                userAccount, category, transaction.transactionType(), transaction.transactionDate());
        transactionRepository.save(newTransaction);

        if(newTransaction.getTransactionType().equals("expense")) {
            userAccount.setSaldo(userAccount.getSaldo() - newTransaction.getTransactionAmount());
        }

        TransactionResponse response = new TransactionResponse(newTransaction.getTransactionAmount(),
                newTransaction.getTransactionType(), newTransaction.getTransactionDate());

        return ResponseEntity.created(URI.create("/new/transaction/" + newTransaction.getTransactionType())).body(response);
    }

    public ResponseEntity<List<LastTransactionsDTO>> getAllTransactions(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        List<LastTransactionsDTO> transactions = transactionRepository.getAllTransactions(userAccount.getId());

        return ResponseEntity.ok(transactions);
    }

    public ResponseEntity<List<LastTransactionsDTO>> filterAndGetTransactions(Jwt jwt, String type, String category,
                                                                              Double amount, LocalDate startDate, LocalDate endDate) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        LocalDateTime startTime = (startDate != null) ? startDate.atStartOfDay() : LocalDate.of(1900, 1, 1).atStartOfDay();
        LocalDateTime endTime = (endDate != null) ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now().plusDays(1);

        List<LastTransactionsDTO> transactions = transactionRepository.findFilteredTransactions(
                userAccount.getId(), type, category, amount, startTime, endTime
        );

        return ResponseEntity.ok(transactions);
    }

    public ResponseEntity<?> deleteTransaction(Jwt jwt, long id) {
        if(!transactionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        if(transactionRepository.findById(id).get().getUserAccount().getId() != userAccount.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this transaction.");
        }

        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<TopCategoryDTO>> findExpenseCategoriesSummary(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        List<TopCategoryDTO> categories = transactionRepository.findExpenseCategoriesSummary(userAccount.getId());

        return ResponseEntity.ok(categories);
    }

    public List<TopCategoryDTO> findTopExpenseCategories(long id) {
        return transactionRepository.findTop3ExpenseCategories(id);
    }

    public List<LastTransactionsDTO> findLatestTransactions(long id) {
        return transactionRepository.findLast3Transactions(id);
    }

    public double getWeeklyExpenses(long id){
        return transactionRepository.getLastWeekExpenses(id);
    }

    public double getMeanOfWeeklyExpenses(long id){
        return transactionRepository.getLastWeekAverageExpenses(id);
    }

    public double getBeforeWeekExpenses(long id) {
        return transactionRepository.getBeforeLastWeekExpenses(id);
    }


}

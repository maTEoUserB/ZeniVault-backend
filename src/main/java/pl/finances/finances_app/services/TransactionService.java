package pl.finances.finances_app.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.finances.finances_app.dto.*;
import pl.finances.finances_app.dto.projection.TransactionProjection;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CreateTransactionDTO;
import pl.finances.finances_app.dto.requestsAndResponsesDto.TransactionDTO;
import pl.finances.finances_app.repositories.TransactionRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import pl.finances.finances_app.repositories.entities.CategoryEntity;
import pl.finances.finances_app.repositories.entities.TransactionEntity;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides business logic for managing transactions in the system.
 */
@Service
@Transactional
public class TransactionService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    /**
     * Constructs a new TransactionService with the required repository and services.
     *
     * @param userService the service for user data managing
     * @param transactionRepository the repository for transaction data access
     * @param categoryService the service for category data managing
     */
    @Autowired
    public TransactionService(UserService userService, TransactionRepository transactionRepository, CategoryService categoryService) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public ResponseEntity<TransactionDTO> createNewTransaction(Jwt jwt, CreateTransactionDTO transaction) {
        long id = userService.getUserAccountId(jwt);
        AccountEntity userAccount = userService.findUserById(id).get();

        CategoryEntity category = categoryService.findCategoryById(transaction.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found."));
        TransactionEntity newTransaction = new TransactionEntity(transaction.getTransactionTitle(), transaction.getTransactionAmount(), transaction.getTransactionDescription(),
                userAccount, category, transaction.getTransactionType(), transaction.getTransactionDate());
        transactionRepository.save(newTransaction);

        changeSaldo(newTransaction, userAccount);

        TransactionDTO dto = new TransactionDTO(newTransaction.getId(), newTransaction.getTransactionTitle(), newTransaction.getTransactionAmount(),
                newTransaction.getTransactionDescription(), newTransaction.getCategory().getId(), newTransaction.getTransactionType(), newTransaction.getTransactionDate());


        return ResponseEntity.created(URI.create("/new/transaction/" + newTransaction.getTransactionType())).body(dto);
    }

    @Transactional
    public ResponseEntity<List<LastTransactionsDTO>> getAllTransactions(Jwt jwt) {
        long id = userService.getUserAccountId(jwt);
        List<LastTransactionsDTO> transactions = transactionRepository.getAllTransactions(id);

        return ResponseEntity.ok(transactions);
    }

    @Transactional
    public ResponseEntity<List<LastTransactionsDTO>> filterAndGetTransactions(Jwt jwt, String type, List<String> categories,
                                                                              Double startAmount, Double endAmount, LocalDate startDate, LocalDate endDate) {
        long id = userService.getUserAccountId(jwt);

        Double startAmountVal = (startAmount != null) ? startAmount : Double.MIN_VALUE;
        Double endAmountVal = (endAmount != null) ? endAmount : Double.MAX_VALUE;
        if(type.isEmpty()) type = null;
        LocalDateTime startTime = (startDate != null) ? startDate.atStartOfDay() : LocalDate.of(1900, 1, 1).atStartOfDay();
        LocalDateTime endTime = (endDate != null) ? endDate.plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();
        if(categories != null && categories.isEmpty()) {
            categories = null;
        }

        List<LastTransactionsDTO> transactions = transactionRepository.findFilteredTransactions(
                id, type, categories, startAmountVal, endAmountVal, startTime, endTime
        );

        return ResponseEntity.ok(transactions);
    }

    @Transactional
    public ResponseEntity<?> deleteTransaction(Jwt jwt, long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found");
        }

        long userId = userService.getUserAccountId(jwt);
        if (transactionRepository.findById(id).get().getUserAccount().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this transaction.");
        }

        TransactionEntity transaction = transactionRepository.findById(id).get();
        changeSaldoCauseDelete(transaction.getTransactionType(), transaction.getTransactionAmount(), userId);
        transactionRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<List<CategorySummaryDTO>> findExpenseCategoriesSummary(Jwt jwt) {
        long id = userService.getUserAccountId(jwt);
        List<CategorySummaryDTO> categories = transactionRepository.findExpenseCategoriesSummary(id);

        return ResponseEntity.ok(categories);
    }

    @Transactional
    protected void changeSaldo(TransactionEntity newTransaction, AccountEntity userAccount) {
        if (newTransaction.getTransactionType().equals("expense")) {
            userAccount.setSaldo(userAccount.getSaldo() - newTransaction.getTransactionAmount());
        } else {
            userAccount.setSaldo(userAccount.getSaldo() + newTransaction.getTransactionAmount());
        }
    }

    @Transactional
    protected void changeSaldoCauseDelete(@NotNull String transactionType, @NotNull double transactionAmount, long id) {
        AccountEntity userAccount = userService.findUserById(id).get();
        if (transactionType.equals("expense")) {
            userAccount.setSaldo(userAccount.getSaldo() + transactionAmount);
        } else {
            userAccount.setSaldo(userAccount.getSaldo() - transactionAmount);
        }
    }

    @Transactional
    public List<CategorySummaryDTO> findTopExpenseCategories(long id) {
        return transactionRepository.findTop3ExpenseCategories(id);
    }

    @Transactional
    public List<LastTransactionsDTO> findLatestTransactions(long id) {
        return transactionRepository.findLast3Transactions(id);
    }

    @Transactional
    public double getWeeklyTransactions(long id, String type) {
        return transactionRepository.getLastWeekTransactions(id, type);
    }

    @Transactional
    public double getMeanOfWeeklyExpenses(long id) {
        return transactionRepository.getLastWeekAverageExpenses(id);
    }

    @Transactional
    public double getMeanOfWeeklyIncomes(long id) {
        return transactionRepository.getLastWeekAverageIncomes(id);
    }

    @Transactional
    public double getMeanOfWeeklyTransactions(long id) {
        return transactionRepository.getLastWeekAverageTransactions(id);
    }

    @Transactional
    public double getBeforeWeekExpenses(long id) {
        return transactionRepository.getBeforeLastWeekExpenses(id);
    }

    @Transactional
    public double getMeanOfBeforeWeeklyExpenses(long id) {
        return transactionRepository.getBeforeLastWeekAverageExpenses(id);
    }

    @Transactional
    public List<DailyExpensesDTO> getLast7DaysExpenses(long id) {
        return transactionRepository.getLast7DaysExpenses(id);
    }

    @Transactional
    public TransactionProjection findMaxWeeklyExpense(long id) {
        return transactionRepository.findMaxWeeklyExpense(id);
    }
}

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
import java.util.Map;

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

    public ResponseEntity<TransactionDTO> createNewTransaction(Jwt jwt, CreateTransactionDTO transaction) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        CategoryEntity category = categoryService.findCategoryById(transaction.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found."));
        TransactionEntity newTransaction = new TransactionEntity(transaction.getTransactionTitle(), transaction.getTransactionAmount(), transaction.getTransactionDescription(),
                userAccount, category, transaction.getTransactionType(), transaction.getTransactionDate());
        transactionRepository.save(newTransaction);

        if (newTransaction.getTransactionType().equals("expense")) {
            userAccount.setSaldo(userAccount.getSaldo() - newTransaction.getTransactionAmount());
        } else {
            userAccount.setSaldo(userAccount.getSaldo() + newTransaction.getTransactionAmount());
        }

        TransactionDTO dto = new TransactionDTO(newTransaction.getId(), newTransaction.getTransactionTitle(), newTransaction.getTransactionAmount(),
                newTransaction.getTransactionDescription(), newTransaction.getCategory().getId(), newTransaction.getTransactionType(), newTransaction.getTransactionDate());


        return ResponseEntity.created(URI.create("/new/transaction/" + newTransaction.getTransactionType())).body(dto);
    }

    public ResponseEntity<List<LastTransactionsDTO>> getAllTransactions(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        List<LastTransactionsDTO> transactions = transactionRepository.getAllTransactions(userAccount.getId());

        return ResponseEntity.ok(transactions);
    }

    public ResponseEntity<List<LastTransactionsDTO>> filterAndGetTransactions(Jwt jwt, String type, List<String> categories,
                                                                              Double startAmount, Double endAmount, LocalDate startDate, LocalDate endDate) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        Double startAmountVal = (startAmount != null) ? startAmount : Double.MIN_VALUE;
        Double endAmountVal = (endAmount != null) ? endAmount : Double.MAX_VALUE;

        if(type.isEmpty()) type = null;
        System.out.println("===================================================================");
        System.out.println("Type: " + type);
        System.out.println("===================================================================");
        System.out.println("StartAmount: " + startAmountVal);
        System.out.println("EndAmount: " + endAmountVal);
        System.out.println("===================================================================");


        LocalDateTime startTime = (startDate != null) ? startDate.atStartOfDay() : LocalDate.of(1900, 1, 1).atStartOfDay();
        LocalDateTime endTime = (endDate != null) ? endDate.plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();
        System.out.println("===================================================================");
        System.out.println("StratTime: " + startTime);
        System.out.println("EndTime: " + endTime);
        System.out.println("===================================================================");


        if(categories != null && categories.isEmpty()) {
            categories = null;
        }
        System.out.println("===================================================================");
        System.out.println("Categories: " + categories);
        System.out.println("===================================================================");

        List<LastTransactionsDTO> transactions = transactionRepository.findFilteredTransactions(
                userAccount.getId(), type, categories, startAmountVal, endAmountVal, startTime, endTime
        );

        return ResponseEntity.ok(transactions);
    }

    public ResponseEntity<?> deleteTransaction(Jwt jwt, long id) {
        if (!transactionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        if (transactionRepository.findById(id).get().getUserAccount().getId() != userAccount.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this transaction.");
        }

        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<CategorySummaryDTO>> findExpenseCategoriesSummary(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);

        List<CategorySummaryDTO> categories = transactionRepository.findExpenseCategoriesSummary(userAccount.getId());

        return ResponseEntity.ok(categories);
    }

    public List<CategorySummaryDTO> findTopExpenseCategories(long id) {
        return transactionRepository.findTop3ExpenseCategories(id);
    }

    public List<LastTransactionsDTO> findLatestTransactions(long id) {
        return transactionRepository.findLast3Transactions(id);
    }

    public double getWeeklyTransactions(long id, String type) {
        return transactionRepository.getLastWeekTransactions(id, type);
    }

    public double getMeanOfWeeklyExpenses(long id) {
        return transactionRepository.getLastWeekAverageExpenses(id);
    }

    public double getMeanOfWeeklyIncomes(long id) {
        return transactionRepository.getLastWeekAverageIncomes(id);
    }

    public double getMeanOfWeeklyTransactions(long id) {
        return transactionRepository.getLastWeekAverageTransactions(id);
    }

    public double getBeforeWeekExpenses(long id) {
        return transactionRepository.getBeforeLastWeekExpenses(id);
    }

    public double getMeanOfBeforeWeeklyExpenses(long id) {
        return transactionRepository.getBeforeLastWeekAverageExpenses(id);
    }

    public List<DailyExpensesDTO> getLast7DaysExpenses(long id) {
        return transactionRepository.getLast7DaysExpenses(id);
    }

    public TransactionProjection findMaxWeeklyExpense(long id) {
        return transactionRepository.findMaxWeeklyExpense(id);
    }
}

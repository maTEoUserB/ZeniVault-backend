package pl.finances.finances_app.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.finances.finances_app.dto.*;
import pl.finances.finances_app.dto.projection.TransactionProjection;
import pl.finances.finances_app.dto.requestsAndResponsesDto.SaldoDTO;
import pl.finances.finances_app.repositories.AccountRepository;
import pl.finances.finances_app.repositories.TransactionRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Provides business logic for managing accounts in the system.
 */
@Service
@Transactional
public class AccountService {

    private final UserService userService;
    private final TransactionService transactionService;
    private final SavingsGoalService savingsGoalService;
    private final ExchangeRateService exchangeRateService;
    private final ObligationService obligationService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /**
     * Constructs a new AccountService with the required repositories and services.
     *
     * @param userService the service for user data managing
     * @param transactionService the service for transaction data managing
     * @param savingsGoalService the service for savings goal data managing
     * @param exchangeRateService the service for get exchange rates from API
     * @param obligationService the service for obligation data managing
     * @param transactionRepository the repository for transaction data access
     * @param accountRepository the repository for account data access
     */
    @Autowired
    public AccountService(UserService userService, TransactionService transactionService, SavingsGoalService savingsGoalService, ExchangeRateService exchangeRateService, ObligationService obligationService, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.savingsGoalService = savingsGoalService;
        this.exchangeRateService = exchangeRateService;
        this.obligationService = obligationService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public ResponseEntity<IndexDTO> getMainAccountInformation(Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");
        boolean isNew = !userService.existsUserByUsername(username);
        long id = userService.getUserAccountId(jwt);

        double saldo = userService.getUserSaldo(id);
        double savingsBalance = savingsGoalService.getCurrentSavingsBalance(id);
        double euroSaldo = calculateEuroRateSaldo(saldo);
        double usdSaldo = calculateUsdRateSaldo(saldo);
        double savingsBalanceEuro = calculateEuroSavingsBalance(savingsBalance);
        double weeklyExpenses = transactionService.getWeeklyTransactions(id, "expense");
        double beforeWeeklyExpenses = transactionService.getBeforeWeekExpenses(id);
        double weeklyChange = calculateWeeklyChange(weeklyExpenses, beforeWeeklyExpenses);
        double meanOfWeeklyExpenses = transactionService.getMeanOfWeeklyExpenses(id);
        List<CategorySummaryDTO> topCategories = transactionService.findTopExpenseCategories(id);
        List<NearestObligationsDTO> nearestObligations = obligationService.getNearestObligations(id);
        List<LastTransactionsDTO> lastTransactions = transactionService.findLatestTransactions(id);

        IndexDTO response = new IndexDTO(saldo, euroSaldo, usdSaldo, weeklyExpenses, meanOfWeeklyExpenses, weeklyChange,
                topCategories, savingsBalance, savingsBalanceEuro, nearestObligations, lastTransactions, isNew);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<SummaryDTO> getAccountSummary(Jwt jwt) {

        long id = userService.getUserAccountId(jwt);

        List<DailyExpensesDTO> lastWeekExpenses = transactionService.getLast7DaysExpenses(id);
        Double averageThisWeek = transactionService.getMeanOfWeeklyExpenses(id);
        Double averageLastWeek = transactionService.getMeanOfBeforeWeeklyExpenses(id);
        Double meanOfWeeklyTransactions = transactionService.getMeanOfWeeklyTransactions(id);
        Double meanOfWeeklyIncomes = transactionService.getMeanOfWeeklyIncomes(id);
        double weeklyExpenses = transactionService.getWeeklyTransactions(id, "expense");
        double beforeWeeklyExpenses = transactionService.getBeforeWeekExpenses(id);
        double weeklyChange = calculateWeeklyChange(weeklyExpenses, beforeWeeklyExpenses);
        int numberOfWeeklyExpenses = transactionRepository.countLastWeekTransactions(id, "expense");
        int numberOfWeeklyIncomes = transactionRepository.countLastWeekTransactions(id, "income");
        Double totalIncome = transactionService.getWeeklyTransactions(id, "income");
        Double totalExpense = transactionService.getWeeklyTransactions(id, "expense");
        TransactionProjection biggestExpense = transactionService.findMaxWeeklyExpense(id);

        SummaryDTO response = new SummaryDTO(lastWeekExpenses, averageThisWeek, averageLastWeek, meanOfWeeklyTransactions, meanOfWeeklyIncomes, weeklyChange,
                numberOfWeeklyExpenses, numberOfWeeklyIncomes, totalIncome, totalExpense, biggestExpense);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<SaldoDTO> setFirstSaldo(Jwt jwt, SaldoDTO saldo) {

        long id = userService.getUserAccountId(jwt);
        AccountEntity userAccount = userService.findUserById(id).get();
        userAccount.setSaldo(saldo.getSaldoAmount());
        accountRepository.save(userAccount);

        return ResponseEntity.ok(saldo);
    }

    protected double calculateEuroRateSaldo(Double saldo){
        double euroRate, euroSaldo;
        try{
            euroRate = exchangeRateService.getEuroExchangeRate();
            euroSaldo = new BigDecimal(saldo / euroRate)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
        }catch (Exception e){
            euroSaldo = 0.0;
        }
        return euroSaldo;
    }


    private double calculateUsdRateSaldo(Double saldo){
        double usdRate, usdSaldo;
        try{
            usdRate = exchangeRateService.getUSDExchangeRate();
            usdSaldo = new BigDecimal(saldo/usdRate)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
        }catch (Exception e){
            usdSaldo = 0.0;
        }
        return usdSaldo;
    }

    private double calculateEuroSavingsBalance(Double savingsBalance){
        double euroRate, savingsBalanceEuro;
        try{
            euroRate = exchangeRateService.getEuroExchangeRate();
            savingsBalanceEuro = new BigDecimal(savingsBalance / euroRate)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
        }catch (Exception e){
            savingsBalanceEuro = 0.0;
        }
        return savingsBalanceEuro;
    }

    private double calculateWeeklyChange(double weeklyExpenses, double beforeWeeklyExpenses){
        double weeklyChange;
        if(weeklyExpenses == 0.0 && beforeWeeklyExpenses == 0.0){
            weeklyChange = 0.0;
        }else if(beforeWeeklyExpenses == 0.0){
            weeklyChange = 100.0;
        }else{
            weeklyChange = (weeklyExpenses/beforeWeeklyExpenses * 100.0) - 100.0;
        }

        return weeklyChange;
    }
}

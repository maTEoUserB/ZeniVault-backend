package pl.finances.finances_app.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.finances.finances_app.dto.*;
import pl.finances.finances_app.dto.projection.TransactionProjection;
import pl.finances.finances_app.repositories.TransactionRepository;
import pl.finances.finances_app.repositories.entities.AccountEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class AccountService {
    private final UserService userService;
    private final TransactionService transactionService;
    private final SavingsGoalService savingsGoalService;
    private final ExchangeRateService exchangeRateService;
    private final ObligationService obligationService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(UserService userService, TransactionService transactionService, SavingsGoalService savingsGoalService, ExchangeRateService exchangeRateService, ObligationService obligationService, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.savingsGoalService = savingsGoalService;
        this.exchangeRateService = exchangeRateService;
        this.obligationService = obligationService;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<IndexDTO> getMainAccountInformations(Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        long id = userAccount.getId();

        double saldo = userAccount.getSaldo();
        double savingsBalance = savingsGoalService.getCurrentSavingsBalance(userAccount);
        double euroRate, usdRate, euroSaldo, usdSaldo, savingsBalanceEuro;
        try {
            euroRate = exchangeRateService.getEuroExchangeRate();
            usdRate = exchangeRateService.getUSDExchangeRate();
            euroSaldo = new BigDecimal(saldo / euroRate)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            usdSaldo = new BigDecimal(saldo/usdRate)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            savingsBalanceEuro = new BigDecimal(savingsBalance / euroRate)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
        }catch (Exception e) {
            euroSaldo = 0.0;
            usdSaldo = 0.0;
            savingsBalanceEuro = 0.0;
        }

        double weeklyExpenses = transactionService.getWeeklyTransactions(id, "expense");
        double beforeWeeklyExpenses = transactionService.getBeforeWeekExpenses(id);
        double weeklyChange;
        if(weeklyExpenses == 0.0 && beforeWeeklyExpenses == 0.0){
            weeklyChange = 0.0;
        }else if(beforeWeeklyExpenses == 0.0){
            weeklyChange = 100.0;
        }else{
            double denominatorOfWeeklyChange = beforeWeeklyExpenses == 0.0 ? weeklyExpenses : beforeWeeklyExpenses;
            weeklyChange = (weeklyExpenses/denominatorOfWeeklyChange * 100.0) - 100.0;
        }
        double meanOfWeeklyExpenses = transactionService.getMeanOfWeeklyExpenses(id);
        List<CategorySummaryDTO> topCategories = transactionService.findTopExpenseCategories(id);
        List<NearestObligationsDTO> nearestObligations = obligationService.getNearestObligations(id);
        List<LastTransactionsDTO> lastTransactions = transactionService.findLatestTransactions(id);

        IndexDTO response = new IndexDTO(saldo, euroSaldo, usdSaldo, weeklyExpenses, meanOfWeeklyExpenses, weeklyChange,
                topCategories, savingsBalance, savingsBalanceEuro, nearestObligations, lastTransactions);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<SummaryDTO> getAccountSummary(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        AccountEntity userAccount = userService.getOrCreateUserAccount(username);
        long id = userAccount.getId();

        List<DailyExpensesDTO> lastWeekExpenses = transactionService.getLast7DaysExpenses(id);

        Double averageThisWeek = transactionService.getMeanOfWeeklyExpenses(id);
        Double averageLastWeek = transactionService.getMeanOfBeforeWeeklyExpenses(id);

        Double meanOfWeeklyTransactions = transactionService.getMeanOfWeeklyTransactions(id);
        Double meanOfWeeklyIncomes = transactionService.getMeanOfWeeklyIncomes(id);

        double weeklyExpenses = transactionService.getWeeklyTransactions(id, "expense");
        double beforeWeeklyExpenses = transactionService.getBeforeWeekExpenses(id);
        double weeklyChange;
        if(weeklyExpenses == 0.0 && beforeWeeklyExpenses == 0.0){
            weeklyChange = 0.0;
        }else if(beforeWeeklyExpenses == 0.0){
            weeklyChange = 100.0;
        }else{
            double denominatorOfWeeklyChange = beforeWeeklyExpenses;
            weeklyChange = (weeklyExpenses/denominatorOfWeeklyChange * 100.0) - 100.0;
        }

        int numberOfWeeklyExpenses = transactionRepository.countLastWeekTransactions(id, "expense");
        int numberOfWeeklyIncomes = transactionRepository.countLastWeekTransactions(id, "income");

        Double totalIncome = transactionService.getWeeklyTransactions(id, "income");
        Double totalExpense = transactionService.getWeeklyTransactions(id, "expense");

        TransactionProjection biggestExpense = transactionService.findMaxWeeklyExpense(id);

        SummaryDTO response = new SummaryDTO(lastWeekExpenses, averageThisWeek, averageLastWeek, meanOfWeeklyTransactions, meanOfWeeklyIncomes, weeklyChange,
                numberOfWeeklyExpenses, numberOfWeeklyIncomes, totalIncome, totalExpense, biggestExpense);
        return ResponseEntity.ok(response);
    }
}

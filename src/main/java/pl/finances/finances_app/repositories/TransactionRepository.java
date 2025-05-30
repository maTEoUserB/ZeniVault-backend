package pl.finances.finances_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.finances.finances_app.dto.DailyExpensesDTO;
import pl.finances.finances_app.dto.LastTransactionsDTO;
import pl.finances.finances_app.dto.CategorySummaryDTO;
import pl.finances.finances_app.dto.projection.TransactionProjection;
import pl.finances.finances_app.dto.requestsAndResponsesDto.TransactionDTO;
import pl.finances.finances_app.repositories.entities.TransactionEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    TransactionEntity save(TransactionEntity transaction);
    boolean existsById(Long id);
    Optional<TransactionEntity> findById(Long id);
    void deleteById(Long id);


    @Query(value = """
    SELECT c.id AS categoryId, c.category_name AS categoryName, SUM(t.transaction_amount) AS totalAmount, b.amount_limit AS budgetAmount, SUM(t.transaction_amount)/NULLIF(b.amount_limit, 0) AS budgetProcent
    FROM transactions t
    JOIN categories c ON t.category_id = c.id
    LEFT JOIN budgets b ON b.category_id = t.category_id AND b.user_id = t.user_id
    WHERE t.user_id = :id AND c.type_for_category = 'expense'
    GROUP BY c.id, c.category_name, b.amount_limit
    ORDER BY totalAmount DESC
    LIMIT 3
""", nativeQuery = true)
    List<CategorySummaryDTO> findTop3ExpenseCategories(@Param("id") long id);

    @Query(value = """
    SELECT c.id AS categoryId, c.category_name AS categoryName, SUM(t.transaction_amount) AS totalAmount, b.amount_limit AS budgetAmount, SUM(t.transaction_amount)/NULLIF(b.amount_limit, 0) AS budgetProcent
    FROM transactions t
    JOIN categories c ON t.category_id = c.id
    LEFT JOIN budgets b ON b.category_id = t.category_id AND b.user_id = t.user_id
    WHERE t.user_id = :id AND c.type_for_category = 'expense'
    GROUP BY c.id, c.category_name, b.amount_limit
    ORDER BY totalAmount DESC
""", nativeQuery = true)
    List<CategorySummaryDTO> findExpenseCategoriesSummary(@Param("id") long id);

    @Query(value = """
    SELECT t.id AS is, t.transaction_title AS transactionTitle, t.transaction_description AS transactionDescription, t.transaction_amount AS amount, c.category_name AS category, t.transaction_type AS type, t.transaction_date AS transactionDate
    FROM transactions t
    JOIN categories c ON t.category_id = c.id
    WHERE t.user_id = :id
    ORDER BY t.transaction_date DESC
    LIMIT 3
""", nativeQuery = true)
    List<LastTransactionsDTO> findLast3Transactions(@Param("id") long id);

    @Query(value = """
    SELECT DATE(t.transaction_date) AS dateLabel, COALESCE(SUM(t.transaction_amount), 0) AS totalAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = 'expense'
      AND t.transaction_date >= NOW() - INTERVAL '1 week'
      AND t.transaction_date <= NOW()
    GROUP BY DATE(t.transaction_date)
    ORDER BY DATE(t.transaction_date) DESC
""", nativeQuery = true)
    List<DailyExpensesDTO> getLast7DaysExpenses(@Param("id") long id);


    @Query(value = """
    SELECT COALESCE(SUM(t.transaction_amount), 0) AS totalAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = :type
      AND t.transaction_date >= DATE_TRUNC('week', NOW())
      AND t.transaction_date <= NOW()
""", nativeQuery = true)
    double getLastWeekTransactions(@Param("id") long id, @Param("type") String type);

    @Query(value = """
    SELECT COALESCE(AVG(t.transaction_amount), 0) AS averageAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = 'expense'
      AND t.transaction_date >= DATE_TRUNC('week', NOW())
      AND t.transaction_date <= NOW()
""", nativeQuery = true)
    double getLastWeekAverageExpenses(@Param("id") long id);

    @Query(value = """
    SELECT COALESCE(SUM(t.transaction_amount), 0) AS beforeTotalAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = 'expense'
      AND t.transaction_date >= DATE_TRUNC('week', NOW()) - INTERVAL '1 week'
      AND t.transaction_date < DATE_TRUNC('week', NOW())
""", nativeQuery = true)
    double getBeforeLastWeekExpenses(@Param("id") long id);

    @Query(value = """
    SELECT COALESCE(AVG(t.transaction_amount), 0) AS averageAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = 'expense'
      AND t.transaction_date >= DATE_TRUNC('week', NOW()) - INTERVAL '1 week'
      AND t.transaction_date < DATE_TRUNC('week', NOW())
""", nativeQuery = true)
    double getBeforeLastWeekAverageExpenses(@Param("id") long id);

    @Query(value = """
    SELECT COALESCE(AVG(t.transaction_amount), 0) AS averageAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = 'income'
      AND t.transaction_date >= DATE_TRUNC('week', NOW())
      AND t.transaction_date <= NOW()
""", nativeQuery = true)
    double getLastWeekAverageIncomes(@Param("id") long id);

    @Query(value = """
    SELECT COALESCE(AVG(t.transaction_amount), 0) AS averageAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_date >= DATE_TRUNC('week', NOW())
      AND t.transaction_date <= NOW()
""", nativeQuery = true)
    double getLastWeekAverageTransactions(@Param("id") long id);

    @Query(value = """
    SELECT t.id AS id, t.transaction_title AS transactionTitle, t.transaction_description AS transactionDescription, t.transaction_amount AS amount, c.category_name AS category, t.transaction_type AS type, t.transaction_date AS transactionDate
    FROM transactions t
    JOIN categories c ON t.category_id = c.id
    WHERE t.user_id = :id
    ORDER BY t.transaction_date DESC
""", nativeQuery = true)
    List<LastTransactionsDTO> getAllTransactions(@Param("id") long id);


    @Query("""
    SELECT new pl.finances.finances_app.dto.LastTransactionsDTO(
        t.id,
        t.transactionTitle,
        t.transactionDescription,
        t.transactionAmount,
        c.categoryName,
        t.transactionType,
        CAST(t.transactionDate AS timestamp)
    )
    FROM TransactionEntity t
    JOIN t.category c
    WHERE t.userAccount.id = :id
    AND (:type IS NULL OR t.transactionType = :type)
    AND (:categories IS NULL OR c.categoryName IN :categories)
    AND (:startAmount IS NULL OR t.transactionAmount >= :startAmount)
    AND (:endAmount IS NULL OR t.transactionAmount <= :endAmount)
    AND t.transactionDate >= :startTime
    AND t.transactionDate < :endTime
""")
    List<LastTransactionsDTO> findFilteredTransactions(
            @Param("id") long id,
            @Param("type") String type,
            @Param("categories") List<String> categories,
            @Param("startAmount") Double startAmount,
            @Param("endAmount") Double endAmount,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query(value = """
    SELECT COUNT(*) AS totalTransactions
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = :type
      AND t.transaction_date >= DATE_TRUNC('week', NOW())
      AND t.transaction_date <= NOW()
""", nativeQuery = true)
    int countLastWeekTransactions(@Param("id") long id, @Param("type") String type);

    @Query(value = """
    SELECT t.transaction_title AS transactionTitle, t.transaction_date AS transactionDate, t.transaction_amount AS transactionAmount
    FROM transactions t
    WHERE t.user_id = :id
      AND t.transaction_type = 'expense'
      AND t.transaction_date >= DATE_TRUNC('week', NOW())
      AND t.transaction_date <= NOW()
    ORDER BY t.transaction_amount DESC
    LIMIT 1
""", nativeQuery = true)
    TransactionProjection findMaxWeeklyExpense(@Param("id") long id);
}

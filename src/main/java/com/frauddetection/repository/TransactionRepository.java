package com.frauddetection.repository;

import com.frauddetection.model.Transaction;
import com.frauddetection.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccountIdOrderByTransactionTimeDesc(String accountId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByFlaggedTrue();

    List<Transaction> findByFlaggedTrueAndReviewedFalse();

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.transactionTime >= :since")
    List<Transaction> findRecentByAccount(@Param("accountId") String accountId,
                                          @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.accountId = :accountId AND t.transactionTime >= :since")
    long countRecentTransactions(@Param("accountId") String accountId,
                                 @Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT t.locationCountry FROM Transaction t WHERE t.accountId = :accountId AND t.status = 'COMPLETED'")
    List<String> findKnownCountriesForAccount(@Param("accountId") String accountId);

    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE t.accountId = :accountId AND t.status = 'COMPLETED'")
    BigDecimal findAverageAmountForAccount(@Param("accountId") String accountId);

    @Query("SELECT t FROM Transaction t WHERE t.flagged = true AND t.reviewed = false ORDER BY t.riskScore DESC")
    List<Transaction> findPendingReviewOrderByRiskDesc();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.flagged = true AND t.reviewed = false AND t.accountId = :accountId")
    long countPendingFlagsForAccount(@Param("accountId") String accountId);
}
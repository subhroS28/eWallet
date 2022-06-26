package com.subhro.eWallet.repository;

import com.subhro.eWallet.models.Transaction;
import com.subhro.eWallet.models.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Modifying
    @Transactional
    @Query("update Transaction t set t.transactionStatus = ?2 where transactionId = ?1")
    void updateTrasactionStatus(String transactionId, TransactionStatus transactionStatus);

    Transaction findByTransactionId(String transactionId);
}

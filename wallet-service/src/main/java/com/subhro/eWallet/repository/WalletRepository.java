package com.subhro.eWallet.repository;

import com.subhro.eWallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    @Modifying
    @Transactional
    @Query("update Wallet w set w.balance = w.balance + :amount where w.email = :email")
    void updateWallet(Double amount, String email);

    Wallet findByEmail(String email);
}

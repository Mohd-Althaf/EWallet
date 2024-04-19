package com.example.Repository;

import com.example.Model.wallet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface WalletRepository extends JpaRepository<wallet,Integer> {
    wallet findByPhoneNo(String phoneNo);

    @Modifying
    @Query("update wallet w set w.balance=w.balance+?2 where w.phoneNo=?1")
    void updateWallet(String phoneNo,Double amount);
}

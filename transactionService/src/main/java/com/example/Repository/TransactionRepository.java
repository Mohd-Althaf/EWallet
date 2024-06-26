package com.example.Repository;

import com.example.Model.Transaction;
import com.example.transactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Modifying
    @Query("update Transaction t set t.transactionStatus=?2 where t.transactionId=?1")
    void updateTransaction(String transactionId, transactionStatus transactionStatus);

    UserDetails findBytransactionId(String username);
}

package com.example.Controller;



import com.example.Service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/txn")
    public String initiateTransaction(@RequestParam("receiver")String receiver,
                                      @RequestParam("purpose")String purpose,
                                      @RequestParam("amount")Double amount,
                                      @RequestParam("sender")String sender) throws JsonProcessingException {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails user = (UserDetails) auth.getPrincipal();
        return transactionService.initiateTransaction(sender,receiver,purpose,amount);
    }
}

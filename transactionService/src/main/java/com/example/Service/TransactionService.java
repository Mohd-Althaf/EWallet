package com.example.Service;


import com.example.CommonConstants;
import com.example.Model.Transaction;
import com.example.Repository.TransactionRepository;
import com.example.transactionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.Uuid;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements UserDetailsService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public String initiateTransaction(String sender, String receiver,
                                      String purpose, Double amount) throws JsonProcessingException {
        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(receiver)
                .amount(amount)
                .purpose(purpose)
                .transactionId(Uuid.randomUuid().toString())
                .transactionStatus(transactionStatus.PROCESSING)
                .build();

        transactionRepository.save(transaction);
        // publish the event post transaction initiation which will be listened by the consumers

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", transaction.getSender());
        jsonObject.put("receiver", transaction.getReceiver());
        jsonObject.put("amount", transaction.getAmount());
        jsonObject.put("txnId", transaction.getTransactionId());

        kafkaTemplate.send(CommonConstants.TRANSACTION_CREATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
        return transaction.getTransactionId();
    }

    @KafkaListener(topics = CommonConstants.WALLET_UPDATED_TOPIC, groupId = "EWallet_Group")
    public void updateTransaction(String msg) throws ParseException, JsonProcessingException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(msg);
        String sender = (String) jsonObject.get("sender");
        String receiver = (String) jsonObject.get("receiver");
        Double amount = (Double) jsonObject.get("amount");
        String txnId = (String) jsonObject.get("txnId");
        String walletUpdateStatus = (String) jsonObject.get("walletUpdateStatus");
        logger.info("validating sender wallet balance :sender-{},receiver-{},amount-{},txnId-{},status-{}",
                sender, receiver, amount, txnId,walletUpdateStatus);

        if(walletUpdateStatus.equals("SUCCESS")){
            transactionRepository.updateTransaction(txnId,transactionStatus.SUCCESSFUL);
        }
        else transactionRepository.updateTransaction(txnId,transactionStatus.FAILED);

        // publish the event after validating and updating wallets which will be listened by the consumers
        String senderMsg = "Hi,Your transaction with Id "+txnId+" got "+walletUpdateStatus;

        jsonObject.put("sender", sender);
        jsonObject.put("receiver", receiver);
        jsonObject.put("amount", amount);
        jsonObject.put("txnId", txnId);
        jsonObject.put("walletUpdateStatus",walletUpdateStatus);

        kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETION_TOPIC, objectMapper.writeValueAsString(jsonObject));

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return transactionRepository.findBytransactionId(username);
    }
}
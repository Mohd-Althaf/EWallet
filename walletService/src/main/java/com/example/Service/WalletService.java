package com.example.Service;

import com.example.CommonConstants;
import com.example.Model.wallet;
import com.example.Repository.WalletRepository;
import com.example.enums.WalletUpdateStatus;
import com.example.userIdentifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;

    private static Logger logger= LoggerFactory.getLogger(WalletService.class);

    @KafkaListener(topics = CommonConstants.USER_CREATION_TOPIC,groupId = "EWallet_Group")
    public void createWallet(String msg) throws ParseException {
        JSONObject jsonObject=(JSONObject) new JSONParser().parse(msg);
        Long userId = (Long) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_USERID);
        String phoneNo = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_PHONE_NUMBER);
        String identifierKey = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_KEY);
        String identifierValue = (String) jsonObject.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_VALUE);

        wallet Wallet = wallet.builder()
                .phoneNo(phoneNo)
                .userId(userId)
                .userIdentifier(userIdentifier.valueOf(identifierKey))
                .identifierValue(identifierValue)
                .balance(100.0)
                .build();
        walletRepository.save(Wallet);
    }
    @KafkaListener(topics = CommonConstants.TRANSACTION_CREATION_TOPIC,groupId = "EWallet_Group")
    public void updateWalletForTransaction(String msg) throws ParseException, JsonProcessingException {
        JSONObject jsonObject=(JSONObject) new JSONParser().parse(msg);
        String sender = (String) jsonObject.get("sender");
        String receiver = (String) jsonObject.get("receiver");
        Double amount = (Double) jsonObject.get("amount");
        String txnId = (String) jsonObject.get("txnId");

        logger.info("validating sender wallet balance :sender-{},receiver-{},amount-{},txnId-{}",
                sender,receiver,amount,txnId);

        wallet walletSender = walletRepository.findByPhoneNo(sender);

        wallet walletReceiver = walletRepository.findByPhoneNo(receiver);

        // publish the event after validating and updating wallets which will be listened by the consumers

      //  JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender",sender);
        jsonObject.put("receiver",receiver);
        jsonObject.put("amount",amount);
        jsonObject.put("txnId",txnId);


        if(walletSender==null || walletReceiver==null || walletSender.getBalance()<amount){
            jsonObject.put("walletUpdateStatus", WalletUpdateStatus.FAILED);
        }
        else {
            walletRepository.updateWallet(sender, -amount);
            walletRepository.updateWallet(receiver, amount);
            jsonObject.put("walletUpdateStatus", WalletUpdateStatus.SUCCESS);

        }
        kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC,objectMapper.writeValueAsString(jsonObject));

//        wallet Wallet = wallet.builder()
//                .phoneNo(phoneNo)
//                .userId(userId)
//                .userIdentifier(userIdentifier.valueOf(identifierKey))
//                .identifierValue(identifierValue)
//                .balance(100.0)
//                .build();
//        walletRepository.save(Wallet);
    }
}

package com.subhro.eWallet.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhro.eWallet.models.Wallet;
import com.subhro.eWallet.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.subhro.eWallet.CommonConstants.TRANSACTION_ID_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.USER_CREATE_KAFKA_TOPIC;
import static com.subhro.eWallet.CommonConstants.GROUP_ID;
import static com.subhro.eWallet.CommonConstants.EMAIL_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.PHONE_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.TRANSACTION_CREATE_KAFKA_TOPIC;
import static com.subhro.eWallet.CommonConstants.SENDER_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.RECEIVER_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.AMOUNT_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.TRANSACTION_STATUS_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.WALLET_UPDATE_SUCCESS_STATUS;
import static com.subhro.eWallet.CommonConstants.WALLET_UPDATE_FAILED_STATUS;
import static com.subhro.eWallet.CommonConstants.WALLET_UPDATE_STATUS_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.WALLET_UPDATE_KAFKA_TOPIC;

@Service
public class WalletService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    WalletRepository repository;

    @Value("${amount.wallet.create}")
    private Double amount;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = {USER_CREATE_KAFKA_TOPIC}, groupId = GROUP_ID)
    public void createWallet(String message) throws JsonProcessingException {

        JSONObject createWalletReq = mapper.readValue(message, JSONObject.class);

        String email = (String) createWalletReq.get(EMAIL_ATTRIBUTE);
        Integer phoneNumber = (Integer) createWalletReq.get(PHONE_ATTRIBUTE);

        Wallet newUserWallet = Wallet.builder()
                        .email(email)
                        .phone(phoneNumber)
                        .balance(amount)
                        .build();

        repository.save(newUserWallet);
    }

    @KafkaListener(topics = {TRANSACTION_CREATE_KAFKA_TOPIC}, groupId = GROUP_ID)
    public void updateWallet(String message) throws Exception {

        JSONObject reader = mapper.readValue(message, JSONObject.class);

        String transactionId = (String) reader.get(TRANSACTION_ID_ATTRIBUTE);
        String sender = (String) reader.get(SENDER_ATTRIBUTE);
        String receiver = (String) reader.get(RECEIVER_ATTRIBUTE);
        Double amount = (Double) reader.get(AMOUNT_ATTRIBUTE);

        Wallet senderWallet = repository.findByEmail(sender);
        Wallet receiverWallet = repository.findByEmail(receiver);

        JSONObject updatedWalletRequest = new JSONObject();
        updatedWalletRequest.put(WALLET_UPDATE_STATUS_ATTRIBUTE, WALLET_UPDATE_FAILED_STATUS);
        updatedWalletRequest.put(TRANSACTION_ID_ATTRIBUTE, transactionId);
        updatedWalletRequest.put(SENDER_ATTRIBUTE, sender);
        updatedWalletRequest.put(RECEIVER_ATTRIBUTE, receiver);
        updatedWalletRequest.put(AMOUNT_ATTRIBUTE, amount);

        //Code from line 76-86 should be handled in frontend only as
        // if user receiver user is not created then sending of money will not be allowed in first place.
        if(!transactionValidity(senderWallet, amount)){
            if(receiverWallet!=null){
                repository.updateWallet(amount, receiver);
            }else{
                //First creating user wallet with the given amount
                receiverWallet = Wallet.builder()
                        .email(receiver)
                        .balance(amount)
                        .build();

                repository.save(receiverWallet);
            }

            repository.updateWallet(0 - amount, sender);
            updatedWalletRequest.put(TRANSACTION_STATUS_ATTRIBUTE, WALLET_UPDATE_SUCCESS_STATUS);
        }

        kafkaTemplate.send(WALLET_UPDATE_KAFKA_TOPIC, mapper.writeValueAsString(updatedWalletRequest));
    }

    private boolean transactionValidity(Wallet wallet, Double amount){
        if(wallet==null){
            return false;
        }
        Double currentBalance = wallet.getBalance();
        return currentBalance>amount;
    }
}

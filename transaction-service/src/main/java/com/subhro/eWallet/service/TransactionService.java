package com.subhro.eWallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhro.eWallet.models.Transaction;
import com.subhro.eWallet.models.TransactionStatus;
import com.subhro.eWallet.repository.TransactionRepository;
import com.subhro.eWallet.request.TransactionRequest;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static com.subhro.eWallet.CommonConstants.*;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository repository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper mapper;

    public String createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException {
        Transaction newTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .sender(transactionRequest.getSender())
                .receiver(transactionRequest.getReceiver())
                .amount(transactionRequest.getAmount())
                .purpose(transactionRequest.getPurpose())
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        repository.save(newTransaction);

        JSONObject transactionCreateObject = new JSONObject();
        transactionCreateObject.put(SENDER_ATTRIBUTE, newTransaction.getSender());
        transactionCreateObject.put(RECEIVER_ATTRIBUTE, newTransaction.getReceiver());
        transactionCreateObject.put(AMOUNT_ATTRIBUTE, newTransaction.getAmount());
        transactionCreateObject.put(TRANSACTION_ID_ATTRIBUTE, newTransaction.getTransactionId());

        kafkaTemplate.send(TRANSACTION_CREATE_KAFKA_TOPIC, mapper.writeValueAsString(transactionCreateObject));
        return newTransaction.getTransactionId();
    }

    @KafkaListener(topics = {WALLET_UPDATE_KAFKA_TOPIC}, groupId = GROUP_ID)
    public void updateTrasaction(String message) throws JsonProcessingException {

        JSONObject updatedTransactionRequest = mapper.readValue(message, JSONObject.class);
        String walletUpdateStatus = (String) updatedTransactionRequest.get(WALLET_UPDATE_STATUS_ATTRIBUTE);
        String transactionId = (String)updatedTransactionRequest.get(TRANSACTION_ID_ATTRIBUTE);
        String sender = (String) updatedTransactionRequest.get(SENDER_ATTRIBUTE);
        String receiver = (String) updatedTransactionRequest.get(RECEIVER_ATTRIBUTE);
        Double amount = (Double) updatedTransactionRequest.get(AMOUNT_ATTRIBUTE);

        /*
        //Note below lines and Line 81 - 85 do the same just that line 81 - 85 makes only one DB call and below one makes 2.

        Transaction transaction = repository.findByTransactionId(transactionId);
        TransactionStatus transactionStatus = WALLET_UPDATE_SUCCESS_STATUS.equals(walletUpdateStatus) ?
                TransactionStatus.SUCCESS : TransactionStatus.FAILED;

        transaction.setTransactionStatus(transactionStatus);
        repository.save(transaction);
        */

        /*
        NOTE: This below condition should not be :- walletUpdateStatus.equals(WALLET_UPDATE_SUCCESS_STATUS)
              bz as walletUpdateStatus is getting from kafka this might be null but as WALLET_UPDATE_SUCCESS_STATUS
              is a constant this would never be null.

        This below line will be equivalent to  (walletUpdateStatus!=null && walletUpdateStatus.equals(WALLET_UPDATE_SUCCESS_STATUS))
         */

        String status;
        if(WALLET_UPDATE_SUCCESS_STATUS.equals(walletUpdateStatus)){
            status = TRANSACTION_SUCCESS_STATUS;
            repository.updateTrasactionStatus(transactionId, TransactionStatus.SUCCESS);
        }else{
            status = TRANSACTION_FAILED_STATUS;
            repository.updateTrasactionStatus(transactionId, TransactionStatus.FAILED);
        }

        /*
            Below code is written in such a way that if status is failed then we will send notification to sender only
            if success then to sender and receiver and thu message will also be different.
         */
        JSONObject senderRequest = new JSONObject();
        senderRequest.put(EMAIL_ATTRIBUTE, sender);
        senderRequest.put(ACTOR_TYPE_ATTRIBUTE, ACTOR_SENDER_ATTRIBUTE);
        senderRequest.put(AMOUNT_ATTRIBUTE, amount);
        senderRequest.put(TRANSACTION_TIME_ATTRIBUTE, new Date());
        senderRequest.put(TRANSACTION_STATUS_ATTRIBUTE, status);
        senderRequest.put(TRANSACTION_ID_ATTRIBUTE, transactionId);

        kafkaTemplate.send(TRANSACTION_COMPLETE_KAFKA_TOPIC, mapper.writeValueAsString(senderRequest));

        if(status.equals(TRANSACTION_SUCCESS_STATUS)){
            //As all the values are same except for 2 keys:- EMAIL_ATTRIBUTE,ACTOR_TYPE_ATTRIBUTE
            JSONObject receiverRequest = senderRequest;
            receiverRequest.put(EMAIL_ATTRIBUTE, receiver);
            receiverRequest.put(ACTOR_TYPE_ATTRIBUTE, ACTOR_RECEIVER_ATTRIBUTE);

            kafkaTemplate.send(TRANSACTION_COMPLETE_KAFKA_TOPIC, mapper.writeValueAsString(receiverRequest));
        }
    }
}

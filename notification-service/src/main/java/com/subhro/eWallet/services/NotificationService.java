package com.subhro.eWallet.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.subhro.eWallet.CommonConstants.TRANSACTION_COMPLETE_KAFKA_TOPIC;
import static com.subhro.eWallet.CommonConstants.GROUP_ID;
import static com.subhro.eWallet.CommonConstants.*;

@Service
public class NotificationService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    JavaMailSender javaMailSender;

    @KafkaListener(topics = {TRANSACTION_COMPLETE_KAFKA_TOPIC}, groupId = GROUP_ID)
    public void publishEmail(String msg) throws Exception {

        JSONObject transactionCompleteRequest = objectMapper.readValue(msg, JSONObject.class);
        String email = (String) transactionCompleteRequest.get(EMAIL_ATTRIBUTE);
        String actor = (String) transactionCompleteRequest.get(ACTOR_TYPE_ATTRIBUTE);
        Double amount = (Double) transactionCompleteRequest.get(AMOUNT_ATTRIBUTE);
        String transactionId = (String) transactionCompleteRequest.get(TRANSACTION_ID_ATTRIBUTE);
        String status = (String) transactionCompleteRequest.get(TRANSACTION_STATUS_ATTRIBUTE);
        Long timeInMillis = (Long) transactionCompleteRequest.get(TRANSACTION_TIME_ATTRIBUTE);

        Date date = new Date(timeInMillis);

        String message;
        if(ACTOR_SENDER_ATTRIBUTE.equals(actor)){
            switch (status){
                case TRANSACTION_SUCCESS_STATUS:
                    message = String.format(SENDER_SUCCESS_MESSAGE, transactionId, date, amount);
                    break;
                case TRANSACTION_FAILED_STATUS:
                    message = String.format(SENDER_FAILED_MESSAGE, transactionId, date);
                    break;
                default:
                    throw new Exception("Invalid Status found!!!");
            }
        }else{
            message = String.format(RECEIVER_MESSAGE, amount, date);
        }

        simpleMailMessage.setText(message);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom("subhra919191@gmail.com");
        simpleMailMessage.setSubject("Payment Notification!!!");

        try {
            javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            // other table // retry logic
        }

    }
}

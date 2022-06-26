package com.subhro.eWallet;

public class CommonConstants {

    //GroupID
    public static final String GROUP_ID = "eWallet";

    // Kafka topics constants
    public static final String USER_CREATE_KAFKA_TOPIC = "userCreate";
    public static final String TRANSACTION_CREATE_KAFKA_TOPIC = "transactionCreate";
    public static final String WALLET_UPDATE_KAFKA_TOPIC = "walletUpdate";
    public static final String TRANSACTION_COMPLETE_KAFKA_TOPIC = "transactionComplete";

    // Kafka attributes constants
    public static final String EMAIL_ATTRIBUTE = "email";
    public static final String PHONE_ATTRIBUTE = "phoneNumber";
    public static final String TRANSACTION_STATUS_ATTRIBUTE = "transactionStatus";
    public static final String TRANSACTION_ID_ATTRIBUTE = "transactionId";
    public static final String SENDER_ATTRIBUTE = "sender";
    public static final String RECEIVER_ATTRIBUTE = "receiver";
    public static final String AMOUNT_ATTRIBUTE = "amount";
    public static final String WALLET_UPDATE_STATUS_ATTRIBUTE = "walletUpdateStatus";
    public static final String WALLET_UPDATE_SUCCESS_STATUS = "SUCCESS";
    public static final String WALLET_UPDATE_FAILED_STATUS = "FAILED";
    public static final String TRANSACTION_SUCCESS_STATUS = "SUCCESS";
    public static final String TRANSACTION_FAILED_STATUS = "FAILED";
    public static final String TRANSACTION_TIME_ATTRIBUTE = "transactionTime";
    public static final String ACTOR_TYPE_ATTRIBUTE = "actor";
    public static final String ACTOR_SENDER_ATTRIBUTE = "sender";
    public static final String ACTOR_RECEIVER_ATTRIBUTE = "receiver";

    //Notification Mail constants
    public static final String SENDER_SUCCESS_MESSAGE = "Transaction with id %d has been completed at %s, your account has been debited by amount $%f";
    //"Transaction with id " + transactionId + " has been completed at " + date + " your account has been debited by amount " + amount;

    public static final String SENDER_FAILED_MESSAGE = "Transaction with id %d has failed at %s, Please retry!!";
    //"Transaction with id " + transactionId + " has failed at " + date + ", Please retry!!";

    public static final String RECEIVER_MESSAGE = "Your account has been credit with amount %d on %s";
    //"Your account has been credit with amount " + amount + " on " + date.toString()"
}

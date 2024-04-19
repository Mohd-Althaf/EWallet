package com.example;

public class CommonConstants {

    // Used when New User is created
    public static final String USER_CREATION_TOPIC="user_created";
    // Used when Transaction is initiated
    public static final String TRANSACTION_CREATION_TOPIC="transaction_initiated";
    // Used when Transaction is completed
    public static final String TRANSACTION_COMPLETION_TOPIC="transaction_completed";
    //Used when Wallet is updated
    public static final String WALLET_UPDATED_TOPIC="wallet_updated";

    public static final String USER_CREATION_TOPIC_USERID="userId";
    public static final String USER_CREATION_TOPIC_PHONE_NUMBER="phoneNo";
    public static final String USER_CREATION_TOPIC_IDENTIFIER_KEY="userIdentifier";
    public static final String USER_CREATION_TOPIC_IDENTIFIER_VALUE="identifierValue";
}

package com.techelevator.tenmo.model;

public class TransferType {
    public static final String Send = "Send";
    public static final String REQUEST = "Request";

    public static boolean isValid(String transferType) {
        return REQUEST.equals(transferType) || Send.equals(transferType);
    }
}

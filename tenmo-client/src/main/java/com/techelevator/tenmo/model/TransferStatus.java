package com.techelevator.tenmo.model;

public final class TransferStatus {
    public static final String Pending = "Pending";
    public static final String Rejected = "Rejected";
    public static final String Approved = "Approved";
    public static final String Request = "Request";

    public static boolean valid(String status){
        return Pending.equals(status) || Approved.equals(status) || Rejected.equals(status);
    }

}

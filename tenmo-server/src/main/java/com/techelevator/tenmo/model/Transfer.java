package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

public class Transfer {
    private Long transferId;
    private String transferType;
    private String transferStatus;
    private User accountFrom;
    private User accountTo;
    private BigDecimal amount;

    public static String TRANSFER_TYPE_REQUEST = "Request";
    public static String TRANSFER_TYPE_SEND = "Send";
    public static String TRANSFER_STATUS_PENDING = "Pending";
    public static String TRANSFER_STATUS_APPROVED = "Approved";
    public static String TRANSFER_STATUS_REJECTED = "Rejected";

    public Transfer(String transferType, User accountFrom, User accountTo, BigDecimal amount){
        //transferId initially set to null, so we could add it later on with getTransferID method
        this(null, transferType, initialStatusForTransferType(transferType), accountFrom, accountTo, amount);
    }
    public Transfer(Long transferId, String transferType, String transferStatus, User accountFrom, User accountTo, BigDecimal amount) {
        this.transferId = transferId;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        validateTransferStatus();
        validateTransferType();
    }


    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public User getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(User accountFrom) {
        this.accountFrom = accountFrom;
    }

    public User getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(User accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public boolean isApproved() {
        return TRANSFER_STATUS_APPROVED.equals(this.transferStatus);
    }

    public boolean isRejected() {
        return TRANSFER_STATUS_REJECTED.equals(this.transferStatus);
    }

    public boolean isPending() {
        return TRANSFER_STATUS_PENDING.equals(this.transferStatus);
    }

    public boolean isRequestType() {
        return TRANSFER_TYPE_REQUEST.equals(this.transferType);
    }

    public boolean isSendType() {
        return TRANSFER_TYPE_SEND.equals(this.transferType);
    }

    private static String initialStatusForTransferType(String transferType) {
        String transferStatus = "";
        if(TRANSFER_TYPE_REQUEST.equals(transferType)) {
            transferStatus = TRANSFER_STATUS_PENDING;
        } else if(TRANSFER_TYPE_SEND.equals(transferType)) {
            transferStatus = Transfer.TRANSFER_STATUS_APPROVED;
        }
        return transferStatus;
    }
    //*Hannah
    public void approved(){
        if(isPending()){
            transferStatus = TRANSFER_STATUS_APPROVED;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    //*Caleb
    public void rejected(){
        if(isPending()){
            transferStatus = TRANSFER_STATUS_REJECTED;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void validateTransferType() {
        if(!(TRANSFER_TYPE_REQUEST.equals(transferType) || TRANSFER_TYPE_SEND.equals(transferType))) {
            throw new IllegalArgumentException(transferType + " is not a valid transferType");
        }
    }

    private void validateTransferStatus() {
        if(!(TRANSFER_STATUS_APPROVED.equals(transferStatus) || TRANSFER_STATUS_PENDING.equals(transferStatus) || TRANSFER_STATUS_REJECTED.equals(transferStatus))) {
            throw new IllegalArgumentException(transferStatus + " is not a valid transferStatus");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return transferId == transfer.transferId && Objects.equals(transferType, transfer.transferType) && Objects.equals(transferStatus, transfer.transferStatus) && Objects.equals(accountFrom, transfer.accountFrom) && Objects.equals(accountTo, transfer.accountTo) && Objects.equals(amount, transfer.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId, transferType, transferStatus, accountFrom, accountTo, amount);
    }



    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", transferType='" + transferType + '\'' +
                ", transferStatus='" + transferStatus + '\'' +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                '}';
    }
}

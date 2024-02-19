package com.techelevator.tenmo.model;

public class StatusUpdateDto {
    private String transferStatus;

    public StatusUpdateDto(String status){
        if(TransferStatus.valid(status)){
            this.transferStatus = status;
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
}

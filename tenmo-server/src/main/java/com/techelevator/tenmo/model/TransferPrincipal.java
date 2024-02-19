package com.techelevator.tenmo.model;

import java.security.Principal;

public class TransferPrincipal {
    private Transfer transfer;
    private Principal principal;

    public TransferPrincipal(Transfer transfer, Principal principal) {
        this.transfer = transfer;
        this.principal = principal;
    }

    public boolean isAllowedToView() {
        return principalUsername().equals(fromUsername()) ||
                principalUsername().equals(toUsername());
    }
    public boolean isAllowedToCreate() {
        boolean isAllowed = false;
        if(transfer.isRequestType()) {
            isAllowed = principalUsername().equals(toUsername());
        } else if(transfer.isSendType()) {
            isAllowed = principalUsername().equals(fromUsername());
        }
        return isAllowed;
    }

    public boolean isAllowedToApproveOrReject() {
        return principalUsername().equals(fromUsername());
    }

    private String toUsername() {
        return transfer.getAccountTo().getUsername();
    }

    private String fromUsername() {
        return transfer.getAccountFrom().getUsername();
    }

    private String principalUsername() {
        return principal.getName();
    }


}

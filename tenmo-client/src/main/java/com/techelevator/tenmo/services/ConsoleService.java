package com.techelevator.tenmo.services;


import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public Long promptForLong(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printAvailableUsers(){
        System.out.println("\n-------------------------------------------\n" +
                "Users\n" +
                "ID          Name\n" +
                "-------------------------------------------");
        for(User i: App.currentUsers){// App.currentUsers retrieves static list that excludes current user
            System.out.println(i.getId() + "        " + i.getUsername()); // Switched .getUsername() and .getId()
        }
        System.out.println("-------------------------------------------");
        App.currentUsers.clear(); // Empties the list after each print, eliminating duplicate retrievals
    }

    public void printPastTransfers(AuthenticatedUser currentUser) {
        System.out.println("\n-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To              Amount\n" +
                "-------------------------------------------\n");
        for (Transfer t : App.pastTransfers) {
            String amountFormat = "$" + t.getAmount();
            String correctFormat = buildFormat(t, currentUser.getUser());
            if (t.getTransferType().equals("Send")) {
                //creates the space between what is printing
                //thank you google for this
                //"-" determines what side it adds padding
                //10 specifies a minimum width of 10 characters
                //23 specifies a minimum width of 23 characters
                //if none of these aren't either it gets padded with spaces
                //%s placeholder for arguments %n adds a new line after it's printed
                System.out.format("%-10s%-23s%s%n", t.getTransferId(), correctFormat, amountFormat);
            }
            if (t.getTransferType().equals("Request")) {
                    System.out.format("%-10s%-23s%s%n", t.getTransferId(), correctFormat, amountFormat);
            }
        }
        System.out.println("-------------------------------------------");
        App.pastTransfers.clear(); // Empties the list after each print
    }

    private String buildFormat(Transfer transfer, User currentUser) {
        //Creates the correct format when printing transfers
        String format = null;
        User accountFrom = transfer.getAccountFrom();
        User accountTo = transfer.getAccountTo();
        String accountFromName = accountFrom.getUsername();
        String accountToName = accountTo.getUsername();
        if(currentUser.equals(accountFrom)) {
            format = "  To: "  + accountToName;
        } else {
            format = "From: " + accountFromName;
        }
        return format;
    }

    private String buildRequestFormat(Transfer transfer, User currentUser) {
        //Creates the correct format when printing pending transfers
        String format = null;
        User accountFrom = transfer.getAccountFrom();
        User accountTo = transfer.getAccountTo();
        String accountToName = accountTo.getUsername();
        if(currentUser.equals(accountFrom)) {
            format = "From: "  + accountToName;
        }
        return format;
    }

    public void printPendingTransfers(AuthenticatedUser currentUser){
        System.out.println("\n-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From                 Amount\n" +
                "-------------------------------------------\n");
        for(Transfer t : App.pendingTransfers){
            String amountFormat = "$" + t.getAmount();
            String correctFormat = buildRequestFormat(t, currentUser.getUser());
            System.out.format("%-10s%-23s%s%n", t.getTransferId(), correctFormat, amountFormat);
        } if(App.pendingTransfers.isEmpty()){
            System.out.println("You have no pending transfers");
        }
        System.out.println("\n-------------------------------------------");
        App.pendingTransfers.clear();
    }

    public void printTransferDetails(Transfer detailedTransfer) {
        System.out.println("\n--------------------------------------------\n" +
                "Transfer Details\n" +
                "--------------------------------------------\n" +
                "Id: " + detailedTransfer.getTransferId() +
                "\nFrom: " + detailedTransfer.getAccountFrom().getUsername() +
                "\nTo: " + detailedTransfer.getAccountTo().getUsername() +
                "\nType: " + detailedTransfer.getTransferType() +
                "\nStatus: " + detailedTransfer.getTransferStatus() +
                "\nAmount: $" + detailedTransfer.getAmount());
    }


}

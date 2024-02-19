package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);
    public static AuthenticatedUser currentUser;
    public static List<User> currentUsers = new ArrayList<>();
    public static List<Transfer> pastTransfers = new ArrayList<>();
    public static List<Transfer> pendingTransfers = new ArrayList<>();


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        String token = currentUser.getToken();
        accountService.setAuthToken(token);
        userService.setAuthToken(token);
        transferService.setAuthToken(token);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                removeMyself();
                consoleService.printAvailableUsers();
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalance();
        System.out.println("\nYour current balance is: $" + balance);
	}

	private void viewTransferHistory() {
		// Retrieve past transfers of current user
		Transfer[] transfers = transferService.retrieveAllTransfers();
        pastTransfers.addAll(Arrays.asList(transfers));
        consoleService.printPastTransfers(currentUser);
        // Prompt user for transfer details
        getTransferDetails();
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        viewPendingTransfers();
        long transferId = consoleService.promptForLong("\nPlease enter transfer ID to view details (0 to cancel): ");
        Transfer detailedTransfer = null;
        if (transferId != 0) {
            detailedTransfer = transferService.retrieveTransferDetails(transferId);
            if (detailedTransfer != null) {
                consoleService.printTransferDetails(detailedTransfer);
                int choice = consoleService.promptForInt("\n1) Approve " + "\n2) Deny" + "\nEnter choice: ");
                BigDecimal amount = detailedTransfer.getAmount();
                BigDecimal balance = accountService.getBalance();
                User accountTo = detailedTransfer.getAccountTo();
                String accountToName = accountTo.getUsername();
                int comparisonResult = balance.compareTo(amount);
                if(choice == 1){
                    if(comparisonResult < 0){
                        System.out.println("\nNot enough funds to send $" + amount + " to " + accountToName);
                    }
                    approval(transferId);

                } else{
                    rejection(transferId);

                }
            } else {
                consoleService.printErrorMessage();
            }
        }
	}

	private void sendBucks() {
		// Prompt user for user ID to send to and amount to be sent
        promptUserToSend();
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        removeMyself();
        consoleService.printAvailableUsers();
        promptUserToRequest();
	}

    private void promptUserToSend() {
        int toId = consoleService.promptForInt("\nEnter ID of user you are sending to (0 to cancel): ");
        if(toId != 0) {
            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount you want to send: $");
            long fromId = currentUser.getUser().getId();
            TransferDto dto = new TransferDto(fromId, toId, amount, TransferType.Send);
            Transfer newTransfer = transferService.createTransfer(dto);
            User accountTo = newTransfer.getAccountTo();
            String accountToName = accountTo.getUsername();
            if (newTransfer != null) {
                System.out.println("\nTE Buck amount: $" + amount + " was sent to " + accountToName);
            } else {
                consoleService.printErrorMessage();
            }
        }
    }

    private void getTransferDetails() { // Prompts user to enter transfer ID to retrieve details from a specific transfer.
        long transferId = consoleService.promptForLong("\nPlease enter transfer ID to view details (0 to cancel): ");
        Transfer detailedTransfer = null;
        if (transferId != 0) {
            detailedTransfer = transferService.retrieveTransferDetails(transferId);
            if (detailedTransfer != null) {
                consoleService.printTransferDetails(detailedTransfer);
            } else {
                consoleService.printErrorMessage();
            }
        }
    }

    private void removeMyself() { // Removes currently logged-in user from list of users to send money to.
        User[] users = userService.listUsers();
        for(User i: users){ // Checks if each registered user matches current user, and adds them to static list
            if(!i.getUsername().equalsIgnoreCase(currentUser.getUser().getUsername())) {
                currentUsers.add(i);
            }
        }
        currentUsers.toArray(users);
    }

    private void viewPendingTransfers(){
        removeMyRequest();
        consoleService.printPendingTransfers(currentUser);

    }

    private void promptUserToRequest(){
        int fromId = consoleService.promptForInt("\nEnter ID of user you are requesting from (0 to cancel): ");
        if(fromId != 0) {
            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount you want to request: $");
            long toId = currentUser.getUser().getId();
            TransferDto dto = new TransferDto(fromId, toId, amount, TransferType.REQUEST);
            Transfer newTransfer = transferService.createTransfer(dto);
            User accountFrom = newTransfer.getAccountFrom();
            String accountFromName = accountFrom.getUsername();
            if (newTransfer != null) {
                System.out.println("\nTE Buck amount: $" + amount + " was requested from " + accountFromName);
            } else {
                consoleService.printErrorMessage();
            }
        }
    }

    private void approval(Long id){
        Transfer transfer = transferService.transferStatusById(id, TransferStatus.Approved);
        User accountTo = transfer.getAccountTo();
        String accountToName = accountTo.getUsername();
        BigDecimal transferAmount = transfer.getAmount();
        if(transfer != null){
            System.out.println("\nTransfer of: $" + transferAmount + " TE Bucks to " +
                    accountToName + " was Approved");
        }

    }

    private void rejection(Long id){
        Transfer transfer = transferService.transferStatusById(id, TransferStatus.Rejected);
        User accountTo = transfer.getAccountTo();
        String accountToName = accountTo.getUsername();
        BigDecimal transferAmount = transfer.getAmount();
        if(transfer != null){
            System.out.println("\nTransfer of: $" + transferAmount + " TE Bucks to " +
                    accountToName + " was Rejected");
        }
    }

    private void removeMyRequest(){
        Transfer[] transfers = transferService.retrieveAllTransfers();
        User current = currentUser.getUser();
        String currentUsername = current.getUsername();
        for(Transfer i : transfers){
            if(i.getTransferStatus().equals("Pending")) {
                if (i.getAccountFrom().getUsername().equals(currentUsername)) {
                    pendingTransfers.add(i);
                }
            }
        }
        pendingTransfers.toArray(transfers);
    }

}

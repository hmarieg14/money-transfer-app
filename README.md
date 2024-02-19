# Module 2 Capstone - TEnmo: Money Transfer App

This app is an online payment service for transferring bucks between friends. It consists of the backend, which is a RESTful API server, and a frontend which is a java-based command-line application.

## Features

1. __Registration:__ A user will be able to register themselves by providing a username and password. A new registered user starts with an initial balance of 1,000 Bucks.
2. __Authentication:__ After successful registration, a user will be able to log in using their registered username and password. Logging in returns an Authentication Token. This token needs to be included with all subsequent API calls.
3. __Show Balance:__ After logging in, a user is able to see their Account Balance.
4. __Send Money:__  An authenticated user of the system is able to send a specific amount of Bucks to a registered user.
- The authenticated user will be able to choose from a list of users to send TE Bucks to
- The authenticated user will not be allowed to send money to themselves.
- The receiver’s account balance is increased by the amount of the transfer.
- The sender’s account balance is decreased by the amount of the transfer.
- The authenticated user can’t send more Bucks than they have in their account.
- The authenticated user can’t send a zero or negative amount.
- A Sending Transfer, given that it’s not zero or negative amount, the amount is not more that the current balance, and the sender and receiver are not the same, is automatically approved
5. __Display Transfers History:__ An authenticated user of the system will be able to see transfers they have sent or received.
6. __Display Details of a Specific Transfer:__ An authenticated user of the system will be able to retrieve the details of any transfer based upon the transfer ID.
7. __Request Money:__ An authenticated user of the system will be able to request a transfer of a specific amount of Bucks from another registered user.
- The authenticated user will be able to choose from a list of users to request Bucks from.
- The authenticated user will not be allowed to request money from themselves.
- The authenticated user can’t request a zero or negative amount.
- A Request Transfer has an initial status of Pending.
- Add account balance changes until the request is approved.
- The transfer request should appear in both users’ list of transfers.
8. __Display Pending Transfers:__ An authenticated user of the system will be able to see their Pending transfers.
9. __Approve/Reject a Transfer Request:__ An authenticated user of the system will be able to either approve or reject a Request Transfer.
- An authenticated user of the system will NOT be able to “approve” a given Request Transfer for more Bucks than the current balance.
- The Request Transfer status is Approved if the user approves, or Rejected if the user rejects the request.
- If the transfer is approved, the requester’s account balance is increased by the amount of the request and the requestee’s account balance is decreased by the amount of the request.
- If the transfer is rejected, no account balance changes but the transfer will still appear in the transfer history

### Prerequisite:
PostgreSQL
Database username should be “postgres” and password should be “postgres1”
A database named “tenmo”

To run this project locally, you will need to create a Postgres database and run the following sql script to create the necessary tables.

Refer to this link if you don’t know how to [Create Database in PostgreSQL using PSQL and PgAdmin](https://www.tutorialsteacher.com/postgresql/create-database#:~:text=Create%20Database%20using%20pgAdmin&text=Open%20pgAdmin%20and%20right%2Dclick,Database%E2%80%A6%20%2C%20as%20shown%20below.&text=This%20will%20open%20Create%20%E2%80%93%20Database,be%20the%20owner%20by%20default)

### Database schema (Run this in your PgAdmin or PSQL)

```
BEGIN TRANSACTION;

DROP TABLE IF EXISTS transfer, account, tenmo_user, transfer_type, transfer_status;
DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;


CREATE TABLE transfer_type (
    transfer_type_id serial NOT NULL,
    transfer_type_desc varchar(10) NOT NULL,
    CONSTRAINT PK_transfer_type PRIMARY KEY (transfer_type_id)
);

CREATE TABLE transfer_status (
    transfer_status_id serial NOT NULL,
    transfer_status_desc varchar(10) NOT NULL,
    CONSTRAINT PK_transfer_status PRIMARY KEY (transfer_status_id)
);

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
    user_id int NOT NULL DEFAULT nextval('seq_user_id'),
    username varchar(50) UNIQUE NOT NULL,
    password_hash varchar(200) NOT NULL,
    role varchar(20),
    CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_username UNIQUE (username)
);

CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
    account_id int NOT NULL DEFAULT nextval('seq_account_id'),
    user_id int NOT NULL,
    balance decimal(13, 2) NOT NULL,
    CONSTRAINT PK_account PRIMARY KEY (account_id),
    CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
    transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
    transfer_type_id int NOT NULL,
    transfer_status_id int NOT NULL,
    account_from int NOT NULL,
    account_to int NOT NULL,
    amount decimal(13, 2) NOT NULL,
    CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
    CONSTRAINT FK_transfer_account_from FOREIGN KEY (account_from) REFERENCES account (account_id),
    CONSTRAINT FK_transfer_account_to FOREIGN KEY (account_to) REFERENCES account (account_id),
    CONSTRAINT FK_transfer_transfer_status FOREIGN KEY (transfer_status_id) REFERENCES transfer_status (transfer_status_id),
    CONSTRAINT FK_transfer_transfer_type FOREIGN KEY (transfer_type_id) REFERENCES transfer_type (transfer_type_id),
    CONSTRAINT CK_transfer_not_same_account CHECK (account_from <> account_to),
    CONSTRAINT CK_transfer_amount_gt_0 CHECK (amount > 0)
);

INSERT INTO transfer_status (transfer_status_desc) VALUES ('Pending');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Approved');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Rejected');

INSERT INTO transfer_type (transfer_type_desc) VALUES ('Request');
INSERT INTO transfer_type (transfer_type_desc) VALUES ('Send');

COMMIT;
```

* Once the cloning process is complete, open your PgAdmin (or any other database admin you are using), create a database and name it ‘tenmo’.
* From your PgAdmin, open data.sql file (it's located in the project directory’s data folder) and execute it.

## How to run the project:

* Once your database setup is complete, open the project in your favorite IDE, navigate to tenmo-server directory, `com.techelevator.tenmo` package and run TenmoApplication.java
* The server-side of the app should now be running on your local machine.
* Then, open go to tenmo-client directory, `com.techelevator` package and run App.java
* The client-side of the app should be running in your local machine now.

# Running the App using command line

1. Open your terminal/Gitbash and navigate to the project folder
2. Navigate to the tenmo-server directory and type the following command to run the server side:
mvn clean package
Maven will build the project into a jar file inside the target directory. Navigate to target and run the jar file using the following command:
java -jar m02-capstone-server-1.0.jar
3. Navigate to tenmo-server and type the following command to run the client side:
mvn clean package
Now, change directory to the target folder and run the following command:
Important Note: if you see an error like Error: Unable to access jarfile m02-capstone-server-0.0.1.jar, your jar file name might be different and hence you would want to give the appropriate name to the following command after java -jar. You can find out the name of your jar file by running the ls command in your target directory.
java -jar m02-capstone-client-0.0.1-SNAPSHOT.jar


# java-mysql-bank
A simple banking application with a JavaFX GUI that I made to demonstrate my knowledge of Java, SQL, MySQL, and Amazon RDS. 

Dependencies:

1) MySQL must be installed on the local machine. 

    download MySQL: https://dev.mysql.com/downloads/installer/

2) Java must be installed on the local machine. 


NOTE: One central MySQL database, hosted by Amazon RDS, is used to store all customer and account records.

TO RUN THE PROGRAM: Download all of the files in /src, then run BankMenu.java. 

Objects in System: 
    Account (abstract class), CheckingAccount, RegularAccount, GoldAccount,
    Customer, CustomerAccount

Account: 
* provides the shared framework for the three types of customer accounts
* defines data fields, as well as both abstract and non-abstract methods

CheckingAccount: 
* Deposits specified amounts into the account and increments monthlyTransactions.
* Withdraws specified amounts from the account and increments monthlyTransactions. 
* Does not allow overdrafting.
* Performs the monthly update activities for a checking account, namely, 
deducting transaction fees and then resetting the monthly transactions counter.

RegularAccount:
* Attempts to withdraw specified amounts from the account. If the balance of 
the account is less than specified amount, then the remainder of the money in 
the account will be withdrawn.
* Performs the monthly update activities for a checking account, namely, applies 
interest to the account.

GoldAccount:
* Withdraws specified amounts from the account.
* Performs the monthly update activities for a checking account, namely, applies 
interest to the account.

Customer:
* Allows the program to instantiate Customer objects.
* Customer objects have data fields for ID, name, phone number, and address.
* When a new account is opened, the "owner" field in the Account object holds
  a reference to a Customer object. 
* Customer objects are translated from and into database records (in the Customer
  table) on program load and save actions, respectively. 

CustomerAccount:
* CustomerAccount objects are created for only on reason: to "glue" data from
  Customer and Account objects together for the purpose of displaying data in a
  TableView. 
* You can see the result of this when you choose "Generate a customer
 report" from the Reports menu. 

 
GoldAccount, RegularAccount, and CheckingAccount all extend Account, and all act in a very similar behavior. 
RegularAccount and GoldAccount have the same pattern to them and their main difference 
is interest rate applied. Checking account is similar as well with getters and 
setters and Override, but differs because it holds the transaction fee value, 
and is used more for transactions, while the other two are used to keep track of
 the interest the accounts accrue. This info is sent to Accounts, and the program 
is run through Bank Menu. Bank Menu is connected to BankDB which connects to the 
database.

*Use-Classes*

BankDB: 
* Connects to the AWS database 
* Loads data from database into Customer and Account objects on program start
* Writes data from the current session to the database when use chooses 
  "Save changes to database."
* Provides an interface for accessing and modifying session data, for example
  adding new users and accounts, closing accounts, etc. 

Bank Menu:
* Extends Application class to implement the JavaFX library
* Uses BankDB to load data from the AWS database.  
* Declares GUI components
* Adds panes to the screen, builds menu bar, and builds menu options
* Builds GUI
* Implements functionality for GUI components, e.g. creating new accounts 
 when the "Open Account" button is clicked. 
 

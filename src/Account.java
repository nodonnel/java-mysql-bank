/**
 *
 * @author Nathanael O'Donnell
 * @date 4.29.2019
 */
public abstract class Account {

    /*
    Use a static integer to keep track of the number of accounts. The count
    will be used to generate a unique accountNumber for every account.
    
    The first account created will have accountNumber = 1, the second will have
    accountNumber = 2, and so on. This number will correspond to the account's entry
    in the MySQL database. 
     */
    protected static int accountsCount = 0;

    // instance variables
    protected int accountNumber;
    protected String type;
    protected double balance;
    protected Customer owner; // Customer that owns this account
    protected int ownerID; // set automatically, used for TableView
 

    /**
     * No-arg constructor
     */
    public Account() {
        accountNumber = ++accountsCount;
        balance = 0; // account opens with 0 balance
    }

    /**
     * Constructor with parameters for each data field
     *
     * @param owner The owner, a Customer object, who owns this account
     */
    public Account(Customer owner, double balance) {
        accountNumber = ++accountsCount; 
        this.balance = balance; 
        this.owner = owner;
        this.ownerID = owner.getID();
    }

    /**
     * Each type of account has different rules for withdrawing money, and
     * implements its own makeWithdrawl() method
     */
    public abstract void makeWithdrawl(double amount);

    /**
     * Adds a specified amount to the balance of this account object.
     *
     * @param amount The amount to deposit.
     */
    public void makeDeposit(double amount) {
        balance += amount;
    }

    /**
     * Updates the account according to account-specific rules.
     */
    public abstract void monthlyUpdate();

    /* getters and setters */
    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }


    public String getType() {
        return type;
    }

    public static int getAccountsCount() {
        return accountsCount;
    }

    public static void setAccountsCount(int accountsCount) {
        Account.accountsCount = accountsCount;
    }

    public int getOwnerID() {
        return ownerID;
    }
    
}


/**
 *
 * @author Nathanael O'Donnell
 * @date 4.29.2019
 */
public class CheckingAccount extends Account {

    // declare data fields
    private static final double TRANSACTION_FEE = 3.0;
    int monthlyTransactions;

    /**
     * No-arg constructor
     */
    public CheckingAccount() {
        monthlyTransactions = 0;
    }

    /**
     * Constructor with parameters for each data field
     *
     * @param accountNumber The account number for the account.
     * @param balance The initial balance of the account.
     * @param owner The owner, a Customer object, who owns this account
     */
    public CheckingAccount(Customer owner, double balance) {
        super(owner, balance);
        type = "Checking Account";
    }

    /**
     * Deposits the specified amount into the account and increments
     * monthlyTransactions.
     *
     * @param amount The amount to deposit.
     */
    public void makeDeposit(double amount) {
        balance += amount;
        monthlyTransactions += 1;
    }

    /**
     * Withdraws the specified amount from the account and increments
     * monthlyTransactions. Don't allow overdrafting.
     *
     * @param amount The amount to withdraw.
     */
    @Override
    public void makeWithdrawl(double amount) {
        if (amount > balance) {
            balance = 0;
        } else {
            balance -= amount;
        }
        monthlyTransactions += 1;
    }

    /**
     * Performs the monthly update activities for a checking account, namely,
     * deducting transaction fees and then resetting the monthly transactions
     * counter.
     */
    @Override
    public void monthlyUpdate() {
        // deduct TRANSACTION_FEE for every transaction after the second. 
        if (monthlyTransactions > 2) {
            balance -= (monthlyTransactions - 2) * TRANSACTION_FEE;
            System.out.println(monthlyTransactions);
            System.out.println(balance);
        }
        // reset monthlyTransactions to zero
        monthlyTransactions = 0;
    }

    // getter and setter 
    
    public int getMonthlyTransactions() {
        return monthlyTransactions;
    }

    
    public void setMonthlyTransactions(int monthlyTransactions) {
        this.monthlyTransactions = monthlyTransactions;
    }
    
  

}


/**
 *
 * @author Nathanael O'Donnell
 * @date 4.29.2019
 */
public class RegularAccount extends Account {
    // declare data fields
    private static final double INTEREST_RATE = 6.0; // annual interest rate
    
    /**
     * No-arg constructor. 
     */
    public RegularAccount() {}

    /**
     * Constructor with parameters for each data field
     *
     * @param accountNumber The account number for the account.
     * @param balance The initial balance of the account.
     * @param owner The owner, a Customer object, who owns this account
     */
    public RegularAccount(Customer owner, double balance) {
        super(owner, balance);
        type = "Regular Account";
        }
   
    
    
    /**
     * Attempts to withdraw the specified amount from the account. If the
     * balance of the account is less than specified amount, then the remainder
     * of the money in the account will be withdrawn.
     *
     * @param amount The amount to withdraw
     */
    @Override
    public void makeWithdrawl(double amount) {
        if (amount > balance) {
            balance = 0;
        } else {
            balance -= amount;
        }
    }
    
    /**
     * Performs the monthly update activities for a checking account, namely,
     * apply interest to the account.
     */
    @Override
    public void monthlyUpdate() {
        // calculate monthly interest
        double monthlyInterest = balance * (INTEREST_RATE / 12 / 100);
        // add interest to balance
        balance += monthlyInterest;
    }
}

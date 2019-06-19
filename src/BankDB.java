import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Nathanael O'Donnell
 * @date 4.29.2019
 */
public class BankDB {

    // connection to the bank database
    private static Connection connection;

    /* 
    Declare ArrayLists to store account and customer objects.
    These objects will be generated from data in the database with the
    load() method, and likewise the data from these objects will be written
    to the database with the save() method.
     */
    private static ArrayList<Account> accounts = new ArrayList<>();
    private static ArrayList<Customer> customers = new ArrayList<>();

    // connection info for NathanaelbankDB, hosted on Amazon RDS
    private final static String PUBLIC_DNS = "nathanaeldb.cswhgf7emt6h.us-east-2.rds.amazonaws.com";
    private final static String PORT = "3306";
    private final static String DATABASE = "NathanaelBankDB";
    private final static String USERNAME = "NathanaelBankDB";
    private final static String PASSWORD = "IloveMySQL!";

    /**
     * Connects to the database. Connection is assigned to the instance variable
     * "connection."
     */
    public static void connect() {
        // make sure the MySQL JDBC driver is present
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }
        connection = null;

        // establish connection
        try {
            connection = DriverManager.
                    getConnection("jdbc:mysql://" + PUBLIC_DNS + ":" + PORT + "/" + DATABASE + "?useSSL=false", USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection Failed!:\n" + e.getMessage());
        }

        if (connection == null) {
            System.out.println("FAILURE! Failed to make connection!");
        }
    } // end connect()

    /**
     * Loads the data from the database into the ArrayLists for customers and
     * accounts.
     */
    public static void load() {
        connect();
        Statement statement = null;
        // load the data
        try {
            statement = connection.createStatement();

            // get entire Customer table
            String custQuery;
            custQuery = "SELECT * FROM Customer";
            ResultSet rs = statement.executeQuery(custQuery);

            // populate customer array list 
            while (rs.next()) {
                //Retrieve by column name
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                customers.add(new Customer(name, phone, address));
            }

            // get entire Account table
            String accountQuery;
            accountQuery = "SELECT * FROM Account";
            rs = statement.executeQuery(accountQuery);

            // populate account array list 
            while (rs.next()) {
                //Retrieve by column name
                String type = rs.getString("accountType");
                double balance = rs.getDouble("balance");
                int ownerID = rs.getInt("owner");
                int accountNumber = rs.getInt("accountNumber");

                if (type.equals("Regular Account")) {
                    RegularAccount regularAccount = new RegularAccount(
                            getCustomer(ownerID), balance);
                    regularAccount.setAccountNumber(accountNumber);
                    accounts.add(regularAccount);
                }

                if (type.equals("Checking Account")) {
                    int monthlyTransactions = rs.getInt("monthlyTransactions");
                    CheckingAccount checkingAccount = new CheckingAccount(
                            getCustomer(ownerID), balance);
                    checkingAccount.setAccountNumber(accountNumber);
                    checkingAccount.setMonthlyTransactions(
                            monthlyTransactions);
                    accounts.add(checkingAccount);

                }

                if (type.equals("Gold Account")) {
                    GoldAccount goldAccount = new GoldAccount(
                            getCustomer(ownerID), balance);
                    goldAccount.setAccountNumber(accountNumber);
                    accounts.add(goldAccount);
                }
            }

            // set accountsCount to the appropriate value based on the number of
            // Account objects loaded.
            if (!accounts.isEmpty()) {
                Account.setAccountsCount(accounts.get(accounts.size() - 1).getAccountNumber());
            }

            // Clean up environment
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException se) {
            //Handle JDBC errors
            se.printStackTrace();
        } catch (Exception e) {
            //Handle Class.forName errors
            e.printStackTrace();
        } finally {
            //close resources
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    } // end load()

    /**
     * Saves the data in the ArrayLists (customers, accounts) to the database
     */
    public static void save() {
        connect();
        Statement statement = null;

        // delete data current in database
        try {
            statement = connection.createStatement();

            String deleteAcct = "DELETE FROM Account";
            statement.executeUpdate(deleteAcct);

            String deleteCust = "DELETE FROM Customer";
            statement.executeUpdate(deleteCust);

            // Clean up environment
            statement.close();
            connection.close();
        } catch (SQLException se) {
            //Handle JDBC errors
            se.printStackTrace();
        } catch (Exception e) {
            //Handle Class.forName errors
            e.printStackTrace();
        } finally {
            //close resources
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try

        // insert the new data
        try {
            connect();
            statement = connection.createStatement();

            // add customers
            for (int i = 0; i < customers.size(); i++) {
                String addCust = "INSERT INTO `NathanaelBankDB`.`Customer` "
                        + "(`ID`, `name`, `phone`, `address`)"
                        + " VALUES ("
                        + "'" + customers.get(i).getID() + "', "
                        + "'" + customers.get(i).getName() + "', "
                        + "'" + customers.get(i).getPhone() + "', "
                        + "'" + customers.get(i).getAddress() + "');";

                statement.executeUpdate(addCust);
            }

            // add  accounts
            for (int i = 0; i < accounts.size(); i++) {
                String addAccount = "INSERT INTO `NathanaelBankDB`.`Account` "
                        + "(`accountNumber`, `accountType`, `balance`, "
                        + "`owner`, `monthlyTransactions`)"
                        + " VALUES ("
                        + "'" + accounts.get(i).getAccountNumber() + "', "
                        + "'" + accounts.get(i).getType() + "', "
                        + "'" + accounts.get(i).getBalance() + "', "
                        + "'" + accounts.get(i).getOwner().getID() + "', ";
                if (accounts.get(i) instanceof CheckingAccount) {
                    addAccount += "'"
                            + ((CheckingAccount) accounts.get(i)).getMonthlyTransactions()
                            + "');";

                } else {
                    addAccount += "'0');";
                }

                statement.executeUpdate(addAccount);
            }

            // Clean up environment
            statement.close();
            connection.close();
        } catch (SQLException se) {
            //Handle JDBC errors
            se.printStackTrace();
        } catch (Exception e) {
            //Handle Class.forName errors
            e.printStackTrace();
        } finally {
            //close resources
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }

    /**
     * Adds an Account to the accounts ArrayList
     *
     * @param account the Account to add
     */
    public static void openAccount(Account account) {
        accounts.add(account);
    }

    /**
     * Adds a Customer to the customers ArrayList
     *
     * @param customer the Customer to add
     */
    public static void addCustomer(Customer customer) {
        customers.add(customer);
    }

    /**
     * Removes the Account with the specified accountNumber from the accounts
     * ArrayList.
     *
     * @param accountNumber the accountNumber of the Account to close
     * @return
     */
    public static boolean closeAccount(int accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                accounts.remove(account);
                
                // set accountsCount to the current highest accountNumber,
                // to account for the possibility that the Account with the
                // highest accountNumber was just removed
                Account.setAccountsCount(accounts.get(
                        accounts.size() - 1).getAccountNumber());
                return true;
            }
        }
        return false;
    }

    /**
     * Effectively deletes all the data in this session (NOT from the database)
     * by setting the ArrayLists accounts and customers to empty ArrayLists.
     */
    public static void reset() {

        accounts = new ArrayList<Account>();
        customers = new ArrayList<Customer>();
        Customer.setCustomerCount(0);
        Account.setAccountsCount(0);

    }

    /**
     * Looks up a Customer by their ID and returns the associated Customer.
     *
     * @param ID The Customer ID to look up.
     * @return The Customer associated with the ID.
     */
    public static Customer getCustomer(int ID) {
        for (Customer customer : customers) {
            if (customer.getID() == ID) {
                return customer;
            }
        }
        return null;
    }

    /**
     * Looks up an Account by its accountNumber and returns the associated
     * Account.
     *
     * @param accountNumber The accountNumber to look up.
     * @return The Account associated with the accountNumber.
     */
    public static Account getAccount(int accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }
        return null;
    }

    /**
     * Calls monthlyUpdate() on every Account object in the accounts ArrayList
     */
    public static void monthlyUpdate() {
        for (Account account : accounts) {
            account.monthlyUpdate();
        }
    }

    /**
     * Builds and returns an ObservableList of CustomerAccount objects for 
     * all the Accounts that are owned by the Customer with the specified ID.
     * 
     * @param ID The ID of the Customer whose Accounts are looked up.
     * @return An ObservableList of CustomerAccount objects, one object for each
     * Account owned by the Customer.
     */
    public static ObservableList<CustomerAccount> getCustomerAccount(int ID) {
        ObservableList<CustomerAccount> customerAccounts
                = FXCollections.observableArrayList();

        // find the Customer matching the ID
        Customer customer = null;
        for (Customer candidate : customers) {
            if (candidate.ID == ID) {
                customer = candidate;
            }
        }

        // add all the Accounts owned by the Customer to the ObservableList
        for (Account account : accounts) {
            if (account.getOwner().getID() == ID) {
                CustomerAccount custAcct = new CustomerAccount();
                custAcct.setID(ID);
                custAcct.setName(customer.getName());
                custAcct.setPhone(customer.getPhone());
                custAcct.setAddress(customer.getAddress());
                custAcct.setAccountNumber(account.getAccountNumber());
                custAcct.setType(account.getType());
                custAcct.setBalance(account.getBalance());
                customerAccounts.add(custAcct);
            }
        }
        return customerAccounts;
    }

    /**
     * Finds the Account associated with the specified accountNumber, then adds
     * the Account to an ObservableList and returns the ObservableList. Note:
     * the returned ObservableList will contain at most one Account object.
     * @param accountNumber
     * @return 
     */
    public static ObservableList<Account> getAccountList(int accountNumber) {
        ObservableList<Account> accountList
                = FXCollections.observableArrayList();

        // find the specified Account and add to the ObservableList
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                accountList.add(account);
                // stop looking if the Account is found
                break;
            }
        }
        return accountList;
    }

    /**
     * Builds and returns an ObservableList of all Customer objects in the customers 
     * ArrayList.
     * 
     * @return An ObservableList of all Customer objects in the customers 
     * ArrayList.
     */
    public static ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customerList
                = FXCollections.observableArrayList();

        for (Customer customer : customers) {
            customerList.add(customer);
        }

        return customerList;
    }

    /**
     * Builds and returns an ObservableList of all Account objects in the 
     * accounts ArrayList.
     * @return An ObservableList of all Account objects in the accounts 
     * ArrayList.
     */
    public static ObservableList<Account> getAccountsList() {
        ObservableList<Account> accountList
                = FXCollections.observableArrayList();

        for (Account account : accounts) {
            accountList.add(account);
        }

        return accountList;
    }

    /**
     * Returns an ArrayList of all the Account objects in this session.
     * @return An ArrayList of all the Account objects in this session.
     */
    public static ArrayList<Account> getAccounts() {
        return accounts;
    }

}

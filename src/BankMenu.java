
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Nathanael O'Donnell
 * @date 4.29.2019
 */
public class BankMenu extends Application {

    //declare high-level GUI components 
    BorderPane mainPane;
    VBox homePane;
    VBox addCustPane;
    VBox openPane;
    VBox closePane;
    VBox depositPane;
    VBox withdrawPane;
    VBox customersReport;
    VBox accountsReport;
    TableView<Customer> customersTable;
    TableView<Account> accountsTable;
    TableView<CustomerAccount> custAcctTable;
    TableView<Account> accountTable;
    VBox stats;

    // system stats labels
    Label balanceLabel;
    Label averageBalanceLabel;
    Label emptyAccountsLabel;
    Label largestOwnerLabel;

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {

        // initalize all panes
        mainPaneInit();
        homePaneInit();
        mainPane.setCenter(homePane); // set the homePane as the center on load
        openPaneInit();
        closePaneInit();
        depositPaneInit();
        withdrawPaneInit();
        addCustPaneInit();
        custAcctTableInit();
        customersTableInit();
        accountsTableInit();
        accountTableInit();
        statsInit();

        Scene scene = new Scene(mainPane, 1080, 608);
        scene.getStylesheets().add("style.css");
        // display avaiable font families for design purposes
        //System.out.println(javafx.scene.text.Font.getFamilies());
        primaryStage.setTitle("MySQL Bank"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }

    /**
     * This method initializes the components for mainPane, which is the
     * BorderPane that will be set as the scene for the primaryStage.
     *
     * The main menu is set as the top of mainPane, and then, throughout the
     * execution of the program, the various other panes are set as the center.
     */
    private void mainPaneInit() {

        // build main MenuBar, one Menu at a time
        //file menu
        Menu fileMenu = new Menu("File");
        MenuItem home = new MenuItem("Go to home screen");
        home.setOnAction(e -> mainPane.setCenter(homePane));
        MenuItem save = new MenuItem("Save changes to database");
        save.setOnAction(e -> BankDB.save());
        MenuItem reset = new MenuItem("Delete all records");
        reset.setOnAction(e -> {
            // clear records from memory - DB records won't be deleted 
            // until changes are saved 
            BankDB.reset();
            customersTable.getItems().clear();
            accountsTable.getItems().clear();
            custAcctTable.getItems().clear();
            accountTable.getItems().clear();
        }); // do a "hard" reset
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());
        fileMenu.getItems().addAll(home, save, reset, exit);

        // customers menu
        Menu customerMenu = new Menu("Customers");
        MenuItem add = new MenuItem("New customer");
        add.setOnAction(e -> mainPane.setCenter(addCustPane));
        MenuItem customersReport = new MenuItem("View all customers");
        customersReport.setOnAction(e -> {
            customersTable.getItems().clear();
            customersTable.setItems(BankDB.getCustomers());
            mainPane.setCenter(customersTable);
        });
        customerMenu.getItems().addAll(add, customersReport);

        // account menu
        Menu accountMenu = new Menu("Accounts");
        MenuItem create = new MenuItem("Open an account");
        create.setOnAction(e -> {
            mainPane.setCenter(openPane);
        });
        MenuItem close = new MenuItem("Close an account");
        close.setOnAction(e -> mainPane.setCenter(closePane));
        MenuItem accountsReport = new MenuItem("View all accounts");
        accountsReport.setOnAction(e -> {
            accountsTable.getItems().clear();
            accountsTable.setItems(BankDB.getAccountsList());
            mainPane.setCenter(accountsTable);
        });
        MenuItem endOfMonth = new MenuItem("Perform end of month procedures");
        endOfMonth.setOnAction(e -> {
            // call the end of month method 
            BankDB.monthlyUpdate();
            // refresh the accounts list table
            accountsTable.getItems().clear();
            accountsTable.setItems(BankDB.getAccountsList());

        });
        accountMenu.getItems().addAll(create, close, accountsReport, endOfMonth);

        // transactions menu
        Menu transactionsMenu = new Menu("Transactions");
        MenuItem deposit = new MenuItem("Deposit funds");
        deposit.setOnAction(e -> mainPane.setCenter(depositPane));
        MenuItem withdraw = new MenuItem("Withdraw funds");
        withdraw.setOnAction(e -> mainPane.setCenter(withdrawPane));
        transactionsMenu.getItems().addAll(deposit, withdraw);

        // reports menu
        Menu reportsMenu = new Menu("Reports");
        MenuItem accountReport = new MenuItem("Generate an account report");
        accountReport.setOnAction(e -> {
            try {
                TextInputDialog tid = new TextInputDialog();
                tid.setTitle("Generate account report");
                tid.setHeaderText("Enter the account number:");
                tid.setContentText("Account number:");
                tid.showAndWait();
                int accountNumber = Integer.parseInt(tid.getEditor().getText());
                if (BankDB.getAccount(accountNumber) == null) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Account number not found.");
                    alert.showAndWait();
                } else {
                    accountTable.getItems().clear();
                    accountTable.setItems(BankDB.getAccountList(accountNumber));
                    mainPane.setCenter(accountTable);
                }
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Account number must be an integer");
                alert.showAndWait();
            }
        });
        MenuItem customerReport = new MenuItem("Generate a customer report");
        customerReport.setOnAction(e -> {
            try {
                TextInputDialog tid = new TextInputDialog();
                tid.setTitle("Generate customer report");
                tid.setHeaderText("Enter the customer ID:");
                tid.setContentText("ID:");
                tid.showAndWait();
                int ID = Integer.parseInt(tid.getEditor().getText());
                if (BankDB.getCustomer(ID) == null) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Customer ID not found.");
                    alert.showAndWait();
                } else {
                    custAcctTable.getItems().clear();
                    custAcctTable.setItems(BankDB.getCustomerAccount(ID));
                    mainPane.setCenter(custAcctTable);
                }
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Customer ID must be an integer");
                alert.showAndWait();
            }
        });
        MenuItem systemReport = new MenuItem("Display system-wide bank statistics");
        systemReport.setOnAction(e -> {
            // update system statistics
            calcStats();
            // display the stats pane
            mainPane.setCenter(stats);
        });
        reportsMenu.getItems().addAll(
                accountReport, customerReport, systemReport);

        // add all the menus to the menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, customerMenu, accountMenu,
                transactionsMenu, reportsMenu);

        // add all GUI components to the mainPane 
        mainPane = new BorderPane();
        mainPane.setTop(menuBar);
    }

    /**
     * This method initializes the components for homePane, which is the pane
     * that shows on program load. This pane contains instructions for using the
     * program.
     */
    private void homePaneInit() {
        // use a VBox for the home screen
        homePane = new VBox(5);

        // add Labels for the welcome text and all the instructions
        Label mainTitle = new Label("Welcome to MySQL bank!");
        mainTitle.setId("main-title-label");
        Label instructions = new Label("Please read the overview of the program"
                + " below.");
        instructions.setId("italic-label");
        Label spacer = new Label("");
        Label fileTitle = new Label("File");
        fileTitle.setId("title-label");
        Label fileDescription = new Label(""
                + "The file menu contains functions to delete records,"
                + " save changes to the database, and return to this home screen.");
        Label customersTitle = new Label("Customers");
        customersTitle.setId("title-label");
        Label customersDescription = new Label(""
                + "The customers menu contains functions to add new customers"
                + " and view a list of all customers.");
        Label accountsTitle = new Label("Accounts");
        accountsTitle.setId("title-label");
        Label accountsDescription = new Label(""
                + "The accounts menu contains functions to add/remove accounts, "
                + "view a list of all accounts, and perform end-of-month procedures.");
        Label transactionsTitle = new Label("Transactions");
        transactionsTitle.setId("title-label");
        Label transactionsDescription = new Label("The transactions menu contains"
                + " functions to withdraw and deposit funds.");
        Label reportsTitle = new Label("Reports");
        reportsTitle.setId("title-label");
        Label reportsDescription = new Label("The reports menu contains functions"
                + " for generating reports on customers, accounts, and the whole system.");

        homePane.getChildren().addAll(mainTitle, instructions, spacer, fileTitle, 
                fileDescription, customersTitle, customersDescription, 
                accountsTitle, accountsDescription, transactionsTitle, 
                transactionsDescription, reportsTitle, reportsDescription);
    }

    /**
     * This method initializes the components for the "Open a new account" pane.
     */
    private void openPaneInit() {
        openPane = new VBox(5);
        openPane.setPadding(new Insets(5, 5, 5, 5));

        Label title = new Label("Open a new account");
        title.setId("title-label");

        // get customer ID of owner
        HBox row1 = new HBox(5);
        row1.setPadding(new Insets(5, 5, 5, 5));
        Label IDLabel = new Label("Customer ID of owner:");
        TextField custIDText = new TextField();
        row1.getChildren().addAll(IDLabel, custIDText);

        // get account type from user
        HBox row2 = new HBox(5);
        row2.setPadding(new Insets(5, 5, 5, 5));
        Label typeLabel = new Label("Select account type:");
        ComboBox type = new ComboBox();
        type.getItems().addAll(
                "Regular Account",
                "Checking Account",
                "Gold Account"
        );
        type.getSelectionModel().select(0);
        row2.getChildren().addAll(typeLabel, type);

        // open button
        Button openButton = new Button("Open Account");
        openButton.setOnAction(e -> {
            int custID = 0;
            try {
                custID = Integer.parseInt(custIDText.getText());

                if (BankDB.getCustomer(custID) == null) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Customer ID not found.");
                    alert.showAndWait();
                } else {
                    switch ((String) type.getValue()) {
                        case ("Regular Account"):
                            BankDB.openAccount(
                                    new RegularAccount(
                                            BankDB.getCustomer(custID), 0));
                            break;
                        case ("Checking Account"):
                            BankDB.openAccount(
                                    new CheckingAccount(
                                            BankDB.getCustomer(custID), 0));
                            break;
                        case ("Gold Account"):
                            BankDB.openAccount(
                                    new GoldAccount(
                                            BankDB.getCustomer(custID), 0));
                            break;
                    }
                    // success dialog
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("System Dialog");
                    alert.setHeaderText("Success");
                    alert.setContentText("Account successfully opened!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Customer ID must be an integer");
                alert.showAndWait();
            }
        });

        openPane.getChildren().addAll(title, row1, row2, openButton);
    }

    /**
     * This method initializes the components for the "Close an account" pane.
     */
    private void closePaneInit() {
        closePane = new VBox(5);
        closePane.setPadding(new Insets(5, 5, 5, 5));

        Label title = new Label("Close an account");
        title.setId("title-label");

        // get account number to close
        HBox row1 = new HBox(5);
        row1.setPadding(new Insets(5, 5, 5, 5));
        Label numLabel = new Label("Account number to close:");
        TextField acctNumText = new TextField();
        row1.getChildren().addAll(numLabel, acctNumText);

        // close button
        Button closeButton = new Button("Close account");
        closeButton.setOnAction(e -> {
            try {
                int accountNumber = Integer.parseInt(acctNumText.getText());
                if (BankDB.getAccount(accountNumber) == null) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Account number not found.");
                    alert.showAndWait();
                } else {
                    BankDB.closeAccount(accountNumber);
                    // success dialog
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("System Dialog");
                    alert.setHeaderText("Success");
                    alert.setContentText("Account successfully closed!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException nfre) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Account number must be an integer");
                alert.showAndWait();
            }
        });

        closePane.getChildren().addAll(title, row1, closeButton);
    }

    /**
     * This method initializes the components for the "Deposit funds" pane.
     */
    private void depositPaneInit() {
        depositPane = new VBox(5);
        depositPane.setPadding(new Insets(5, 5, 5, 5));

        Label title = new Label("Make a deposit");
        title.setId("title-label");

        // get account number for deposit
        HBox row1 = new HBox(5);
        row1.setPadding(new Insets(5, 5, 5, 5));
        Label numLabel = new Label("Enter account number:");
        TextField acctNumText = new TextField();
        row1.getChildren().addAll(numLabel, acctNumText);

        // get deposit amount
        HBox row2 = new HBox(5);
        row2.setPadding(new Insets(5, 5, 5, 5));
        Label amountLabel = new Label("Enter amount to deposit:");
        TextField amountText = new TextField();
        row2.getChildren().addAll(amountLabel, amountText);

        // deposit button
        Button depositButton = new Button("Deposit Funds");
        depositButton.setOnAction(e -> {
            // set accountNumber as -1 as a flag for whether the account 
            // exists. 
            int accountNumber = -1;
            try {
                accountNumber = Integer.parseInt(acctNumText.getText());
                if (BankDB.getAccount(accountNumber) == null) {
                    accountNumber = -1;
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Account number not found.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Account number must be an integer.");
                alert.showAndWait();
            }

            // only try to deposit funds if the account exists, i.e., the
            // accountNumber has been set to a value other than -1.
            if (accountNumber != -1) {
                try {
                    double amount = Double.parseDouble(amountText.getText());
                    BankDB.getAccount(accountNumber).makeDeposit(amount);
                    // success dialog
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("System Dialog");
                    alert.setHeaderText("Success");
                    alert.setContentText("Funds deposited successfully!");
                    alert.showAndWait();
                } catch (NumberFormatException nfe) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Amount must be a number.");
                    alert.showAndWait();
                }
            }
        });

        depositPane.getChildren().addAll(title, row1, row2, depositButton);
    }

    /**
     * This method initializes the components for the "Withdraw funds" pane.
     */
    private void withdrawPaneInit() {
        withdrawPane = new VBox(5);
        withdrawPane.setPadding(new Insets(5, 5, 5, 5));

        Label title = new Label("Make a withdrawl");
        title.setId("title-label");

        // get account number for withdraw
        HBox row1 = new HBox(5);
        row1.setPadding(new Insets(5, 5, 5, 5));
        Label numLabel = new Label("Enter account number:");
        TextField acctNumText = new TextField();
        row1.getChildren().addAll(numLabel, acctNumText);

        // get withdraw amount
        HBox row2 = new HBox(5);
        row2.setPadding(new Insets(5, 5, 5, 5));
        Label amountLabel = new Label("Enter amount to withdraw:");
        TextField amountText = new TextField();
        row2.getChildren().addAll(amountLabel, amountText);

        // withdraw button
        Button withdrawButton = new Button("Withdraw Funds");
        withdrawButton.setOnAction(e -> {
            // set accountNumber as -1 as a flag for whether the account 
            // exists. 
            int accountNumber = -1;
            try {
                accountNumber = Integer.parseInt(acctNumText.getText());
                if (BankDB.getAccount(accountNumber) == null) {
                    accountNumber = -1;
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Account number not found.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Account number must be an integer.");
                alert.showAndWait();
            }

            // only try to withdraw funds if the account exists, i.e., the
            // accountNumber has been set to a value other than -1.
            if (accountNumber != -1) {
                try {
                    double amount = Double.parseDouble(amountText.getText());
                    BankDB.getAccount(accountNumber).makeWithdrawl(amount);
                    // success dialog
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("System Dialog");
                    alert.setHeaderText("Success");
                    alert.setContentText("Funds withdrawn successfully!");
                    alert.showAndWait();
                } catch (NumberFormatException nfe) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Input Error");
                    alert.setHeaderText("Invalid input");
                    alert.setContentText("Amount must be a number.");
                    alert.showAndWait();
                }
            }
        });

        withdrawPane.getChildren().addAll(title, row1, row2, withdrawButton);
    }

    /**
     * This method initializes the components for the "New customer" pane.
     */
    private void addCustPaneInit() {
        addCustPane = new VBox(5);
        addCustPane.setPadding(new Insets(5, 5, 5, 5));

        Label title = new Label("Add a new customer");
        title.setId("title-label");

        // get customer name
        HBox row1 = new HBox(5);
        row1.setPadding(new Insets(5, 5, 5, 5));
        Label nameLabel = new Label("Customer name:");
        TextField nameText = new TextField();
        row1.getChildren().addAll(nameLabel, nameText);

        // get customer phone
        HBox row2 = new HBox(5);
        row2.setPadding(new Insets(5, 5, 5, 5));
        Label phoneLabel = new Label("Customer phone:");
        TextField phoneText = new TextField();
        row2.getChildren().addAll(phoneLabel, phoneText);

        // get customer address
        HBox row3 = new HBox(5);
        row3.setPadding(new Insets(5, 5, 5, 5));
        Label addressLabel = new Label("Customer address:");
        TextField addressText = new TextField();
        row3.getChildren().addAll(addressLabel, addressText);

        // withdraw button
        Button addButton = new Button("Add customer");
        addButton.setOnAction(e -> {
            String name = nameText.getText();
            String phone = phoneText.getText();
            String address = addressText.getText();
            BankDB.addCustomer(new Customer(name, phone, address));

            // success dialog
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("System Dialog");
            alert.setHeaderText("Success");
            alert.setContentText("New customer successfully added!");
            alert.showAndWait();
        });

        addCustPane.getChildren().addAll(title, row1, row2, row3, addButton);
    }

    /**
     * This method initializes the components for the "Generate a customer
     * report" TableView pane (custAcctTable).
     */
    private void custAcctTableInit() {

        // ID column
        TableColumn<CustomerAccount, Integer> ID
                = new TableColumn<>("Customer ID");
        ID.setMinWidth(150);
        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        // name column
        TableColumn<CustomerAccount, String> name
                = new TableColumn<>("Customer Name");
        name.setMinWidth(150);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        // phone column
        TableColumn<CustomerAccount, String> phone
                = new TableColumn<>("Customer Phone");
        phone.setMinWidth(150);
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // address column
        TableColumn<CustomerAccount, String> address
                = new TableColumn<>("Customer Address");
        address.setMinWidth(150);
        address.setCellValueFactory(new PropertyValueFactory<>("address"));

        // account number column
        TableColumn<CustomerAccount, Integer> accountNumber
                = new TableColumn<>("Account Number");
        accountNumber.setMinWidth(150);
        accountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        // type column
        TableColumn<CustomerAccount, Integer> type
                = new TableColumn<>("Account Type");
        type.setMinWidth(150);
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        // balance column
        TableColumn<CustomerAccount, Integer> balance
                = new TableColumn<>("Account Balance");
        balance.setMinWidth(150);
        balance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        custAcctTable = new TableView<>();
        custAcctTable.getColumns().addAll(ID, name, phone, address,
                accountNumber, type, balance);

    }

    /**
     * This method initializes the components for the "View all customers"
     * TableView pane (customersTable).
     */
    private void customersTableInit() {

        // ID column
        TableColumn<Customer, Integer> ID
                = new TableColumn<>("Customer ID");
        ID.setMinWidth(150);
        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        // name column
        TableColumn<Customer, String> name
                = new TableColumn<>("Customer Name");
        name.setMinWidth(150);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        // phone column
        TableColumn<Customer, String> phone
                = new TableColumn<>("Customer Phone");
        phone.setMinWidth(150);
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // address column
        TableColumn<Customer, String> address
                = new TableColumn<>("Customer Address");
        address.setMinWidth(150);
        address.setCellValueFactory(new PropertyValueFactory<>("address"));

        customersTable = new TableView<>();
        customersTable.getColumns().addAll(ID, name, phone, address);

    }

    /**
     * This method initializes the components for the "View all accounts"
     * TableView pane (accountsTable).
     */
    private void accountsTableInit() {

        // account number column
        TableColumn<Account, Integer> accountNumber
                = new TableColumn<>("Account Number");
        accountNumber.setMinWidth(150);
        accountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        // type column
        TableColumn<Account, Integer> type
                = new TableColumn<>("Account Type");
        type.setMinWidth(150);
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        // balance column
        TableColumn<Account, Integer> balance
                = new TableColumn<>("Account Balance");
        balance.setMinWidth(150);
        balance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        // ownerID column
        TableColumn<Account, Integer> ownerID
                = new TableColumn<>("Owner ID");
        ownerID.setMinWidth(150);
        ownerID.setCellValueFactory(new PropertyValueFactory<>("ownerID"));

        accountsTable = new TableView<>();
        accountsTable.getColumns().addAll(accountNumber, type, balance, ownerID);

    }

    /**
     * This method initializes the components for the "Generate an account
     * report" TableView pane (accountTable).
     */
    private void accountTableInit() {

        // account number column
        TableColumn<Account, Integer> accountNumber
                = new TableColumn<>("Account Number");
        accountNumber.setMinWidth(150);
        accountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        // type column
        TableColumn<Account, Integer> type
                = new TableColumn<>("Account Type");
        type.setMinWidth(150);
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        // balance column
        TableColumn<Account, Integer> balance
                = new TableColumn<>("Account Balance");
        balance.setMinWidth(150);
        balance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        // ownerID column
        TableColumn<Account, Integer> ownerID
                = new TableColumn<>("Owner ID");
        ownerID.setMinWidth(150);
        ownerID.setCellValueFactory(new PropertyValueFactory<>("ownerID"));

        accountTable = new TableView<>();
        accountTable.getColumns().addAll(accountNumber, type, balance, ownerID);

    }

    /**
     * This method initializes the components for the "Display system-wide bank
     * statistics" pane.
     */
    private void statsInit() {
        stats = new VBox(5);
        Label balanceTextLabel = new Label("Total funds deposited in bank :");
        balanceTextLabel.setId("heading-label");
        balanceLabel = new Label();
        Label averageBalanceTextLabel = new Label("Average account balance:");
        averageBalanceTextLabel.setId("heading-label");
        averageBalanceLabel = new Label();
        Label emptyAccountsTextLabel = new Label("Number of accounts with 0 balance: ");
        emptyAccountsTextLabel.setId("heading-label");
        emptyAccountsLabel = new Label();
        Label largestOwnerTextLabel = new Label("Owner of account with highest balance: ");
        largestOwnerTextLabel.setId("heading-label");
        largestOwnerLabel = new Label();
        stats.getChildren().addAll(
                balanceTextLabel,
                balanceLabel,
                averageBalanceTextLabel,
                averageBalanceLabel,
                emptyAccountsTextLabel,
                emptyAccountsLabel,
                largestOwnerTextLabel,
                largestOwnerLabel
        );

    }

    /**
     * @param args - unused
     */
    public static void main(String[] args) throws SQLException {
        BankDB.connect();
        BankDB.load();
        launch(args);
    }

    /**
     * Calculates system-wide statistics and updates display
     */
    private void calcStats() {

        // declare and init variables to compute
        double totalBalance = 0;
        double averageBalance = 0;
        int emptyAccounts = 0;
        double highBalance = 0;
        String largestOwner = "";

        ArrayList<Account> accounts = BankDB.getAccounts();
        // loop through the accounts
        for (Account account : accounts) {
            // compare balance of the account to highBalance; if it's greater or
            // equal, set largestOwner and highBalance to the info for this account
            if (account.getBalance() >= highBalance) {
                largestOwner = account.getOwner().getName();
                highBalance = account.getBalance();
            }
            // if the account is empty, increment emptyAccounts
            if (account.getBalance() == 0) {
                emptyAccounts += 1;
            }
            // add the balance of this account to the total balance
            totalBalance += account.getBalance();

        }

        // calculate averageBalance, avoiding divide by zero case
        if (accounts.isEmpty()) {
            averageBalance = 0;
        } else {
            averageBalance = totalBalance / accounts.size();
        }

        // print the statistics 
        balanceLabel.setText("" + totalBalance);
        averageBalanceLabel.setText("" + averageBalance);
        emptyAccountsLabel.setText("" + emptyAccounts);
        largestOwnerLabel.setText(largestOwner);
    }
}

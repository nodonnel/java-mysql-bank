
import java.util.ArrayList;


/**
 *
 * @author Nathanael O'Donnell
 * @date 4.1.2019
 */
public class Customer {

    /*
    Use a static integer to keep track of the number of customers. The count
    will be used to generate a unique ID for every account.
    
    The first customer created will have ID = 1, the second will have
    ID = 2, and so on. This number will correspond to the account's entry
    in the MySQL database. 
     */
    protected static int customerCount =0;
    
    // instance variables
    protected int ID;
    protected String name;
    protected String phone;
    protected String address;
    
    

    public Customer(String name, String phone,
            String address) {
        ID = ++customerCount;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    /* getters and setters */
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static int getCustomerCount() {
        return customerCount;
    }

    public static void setCustomerCount(int customerCount) {
        Customer.customerCount = customerCount;
    }
    
}

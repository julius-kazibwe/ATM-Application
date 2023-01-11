import java.sql.*;

public class Customer {
    
    String customerTable= "CREATE TABLE customer(id int AUTO_INCREMENT PRIMARY KEY, name varchar(30) NOT NULL, pin int(4) NOT NULL)";
    String insertCustomer= "INSERT INTO customer(name, pin) VALUES('Ernest Kitiibwa', 4321), ('Julius Kazibwe', 1234), ('Nyombi Shirat', 0125), ('Alio Daniel', 1234), ('Okongo Isaac', 1997), ('Job M. Perez', 8523), ('Maria Karungi', 7591), ('Mulungi Marble', 2299)";
    String accountTable= "CREATE TABLE account(accountNumber varchar(5) NOT NULL PRIMARY KEY UNIQUE, balance float DEFAULT '0.00', customerId int ,FOREIGN KEY (customerId) REFERENCES customer(id))";
    String insertAccount= "INSERT INTO account(accountNumber, customerId) VALUES('10101', 1), ('10103', 3), ('10102', 2), ('10105', 5), ('10104', 4), ('10106', 6), ('11017', 7), ('11111', 8)";

    public static void main(String[] args){
    try{
        Atm atmCustomer= new Atm();  
        Customer cust= new Customer();

        Connection con= atmCustomer.dbConnection(); 
        Class.forName("com.mysql.cj.jdbc.Driver");

        Statement st1 = con.createStatement();
    
        st1.execute(cust.customerTable);
        st1.execute(cust.insertCustomer);
        st1.execute(cust.accountTable); 
        st1.execute(cust.insertAccount);
       

        con.close();

    }
    catch(ClassNotFoundException c){
        c.printStackTrace();
    }
    catch(SQLException s){
      s.printStackTrace();
       
    }
} 
}

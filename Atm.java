import java.sql.*;
import java.util.*;

public class Atm {
    PreparedStatement pst;
    Connection conn;
    String accountNumber="";
    int pin=0;
    float balance;
    String join="";
    String name="";
    ResultSet rs;
    boolean verified=false;
    int count=1;
    int option;
    String proceed="no";

    public Connection dbConnection() throws SQLException{
        String url= "jdbc:mysql://localhost/atm";
        String user="root";
        String password="";

        this.conn= DriverManager.getConnection(url, user,password);
        return conn;
    }
    

    public boolean authorize(Scanner input) throws SQLException{
        this.conn= this.dbConnection();
        String join= "SELECT customer.name, customer.pin, account.accountNumber, account.balance FROM customer CROSS JOIN account WHERE customer.id=account.customerId AND account.accountNumber=? AND customer.pin=?";    
        
        System.out.print("LOGIN\n");
        System.out.print("Enter your account number here: ");

        this.accountNumber= input.next();
        input.nextLine();
        System.out.println("\n");
        System.out.print("Enter your pin here: ");
        this.pin= input.nextInt();
        System.out.println("\n");

    
        this.pst= conn.prepareStatement(join);
        pst.setString(1, this.accountNumber);
        pst.setInt(2, this.pin);
        this.rs= pst.executeQuery();

        this.verified=rs.next();

        return verified;
    }
    public boolean deposit(Scanner input) throws SQLException{
        this.conn= this.dbConnection();
        String join= "SELECT customer.name, account.accountNumber, account.balance FROM customer CROSS JOIN account WHERE customer.id=account.customerId AND account.accountNumber=?";    
        
        System.out.print("Enter your account number here: ");

        this.accountNumber= input.next();
        input.nextLine();
        System.out.println("\n");
    
        this.pst= conn.prepareStatement(join);
        pst.setString(1, this.accountNumber);
        this.rs= pst.executeQuery();

        this.verified= rs.next();
        if(this.verified){
            this.name= this.rs.getString("customer.name");
        }

        return verified;
    }

    public void displayMenu(Scanner input){
        System.out.println("MENU");
        System.out.println("1. Check Balance");
        System.out.println("2. Cash Deposit");
        System.out.println("3. Cash Withdraw\n");
        System.out.print("Enter your option here: ");

        this.option= input.nextInt();
        
        System.out.println("\n");
        
        while(this.option==0||this.option>3){
            System.out.println("Invalid Option");
            System.out.print("Renter your option here: ");
            this.option= input.nextInt();
            input.nextLine();
            System.out.println("\n");
            
        }

    }

    public void welcome(Scanner input){

        System.out.println("      WELCOME\n");
        System.out.println("Please select an operation from the menu below:\n");
        this.displayMenu(input);

    }
    public void proceed(Scanner input){
    
    
            System.out.println("Do you want to proceed? type yes/no below: ");
            this.proceed= input.next().toLowerCase().trim();
            
    }
        

public static void main(String[] a){

    String driverClassName = "com.mysql.cj.jdbc.Driver"; 

    Atm atm= new Atm();
    Scanner input= new Scanner(System.in);


    try{
        Class.forName(driverClassName);
        Connection conn= atm.dbConnection();
        
        
    do{
        atm.welcome(input);
        
        switch(atm.option){
            
            case 1:  
                System.out.println("BALANCE CHECK\n");
                atm.authorize(input);
                while(atm.verified== false && atm.count<=2) {

                    System.out.println("Wrong account number or pin provided...!\n");
                    atm.authorize(input);

                    atm.count++;
              }
              if(atm.count==3){
                System.out.println("You have exceeed the maximum number of trials, please re-try later\n");
                atm.count=1;
                atm.welcome(input);
              }
              if(atm.verified== true){
                atm.count=1;
                atm.balance= atm.rs.getFloat("account.balance");
                atm.name= atm.rs.getString("customer.name");
                System.out.println("Dear " + atm.name + ", your account balance is: "+ atm.balance+"\n");

                atm.proceed(input);

            }
             
            break;
            
            case 2: 
            

                System.out.println("CASH DEPOSIT");
                atm.deposit(input);
                while(atm.verified== false && atm.count<=2) {

                    System.out.println("Wrong account number provided...!\n");
                    atm.deposit(input);

                    atm.count++;
              }
              if(atm.count==3){
                System.out.println("You have exceeed the maximum number of trials, please re-try later\n");
                atm.count=1;
                atm.welcome(input);
              }
              if(atm.verified== true){
                atm.count=1;
                String increaseBal= "UPDATE account SET balance=? where accountNumber=? ";
                String newBal= "SELECT * FROM account";

                System.out.println("Enter amount: \n");
                float amount= input.nextInt();

                atm.pst= conn.prepareStatement(increaseBal);
                
                atm.pst.setFloat(1, (atm.balance + amount));
                atm.pst.setString(2, atm.accountNumber);
                atm.pst.execute();

                Statement smt= conn.createStatement();
                atm.rs= smt.executeQuery(newBal);
                
                if(atm.rs.next()){
                    atm.balance= atm.rs.getFloat("balance");
                    System.out.println("Dear " + atm.name + " you have successfully deposited "+ amount+".");
                    
                }
                

                atm.proceed(input);
              }
            break;
            
            case 3: 
            System.out.println("CASH WITHDRAW\n");
            atm.authorize(input);
                while(atm.verified== false && atm.count<=2) {
                    System.out.println("Wrong account number or pin provided...!\n");
                    atm.authorize(input);

                    atm.count++;
              }
              if(atm.count==3){
                System.out.println("You have exceeed the maximum number of trials,\nplease re-try later\n");
                atm.count=1;
                atm.welcome(input);
              }
              if(atm.verified== true){
                atm.count=1;
            
                System.out.println("Enter amount: \n");
                int amount= input.nextInt();
                atm.balance= atm.rs.getFloat("account.balance");
                atm.name= atm.rs.getString("customer.name");

                if(amount> atm.balance || atm.balance==0){
                    System.out.println("You have insuffient funds on your acount.\n");

                }else{
                    System.out.println("Select\n 1. to confirm a cash withdraw of "+ amount + "\n 2. to cancel the transaction.");
                    int flag= input.nextInt();

                    if(flag==1){
                        String deductbal= "UPDATE account SET balance=? where accountNumber=? ";

                        atm.pst= conn.prepareStatement(deductbal);

                        atm.pst.setFloat(1, (atm.balance-amount));
                        atm.pst.setString(2, atm.accountNumber);
                        atm.pst.execute();

                        String newBal= "SELECT * FROM account where accountNumber=?";
                        atm.pst= conn.prepareStatement(newBal);
                        atm.pst.setString(1, atm.accountNumber);
                        
                        atm.rs= atm.pst.executeQuery();

                        if(atm.rs.next()){

                            atm.balance= atm.rs.getFloat("balance");
                            System.out.println("Dear " + atm.name + " , you have withdrawn "+amount+ " \n your current account balance is: "+ atm.balance+"\n");

                        }
                        atm.proceed(input);

                    }else if(flag ==2){
                        atm.verified=false;
                        atm.proceed(input);

                    }
                    

                }
                

                

            }
            
                
            break;

            default:
        }
    }while(atm.proceed.equals("yes"));

        input.close();
        conn.close();

    } 
    catch(ClassNotFoundException c){
          c.printStackTrace();
        
    }
    catch(SQLException s){
        s.printStackTrace();
      
  }   
  
}
}
package io.github.drat333.customerregistration;

import java.sql.*;
import java.util.Scanner;

public class main {

    //user
    private static String email;
    private static String pass;
    private static String displayName;
    private static Scanner scanner;

    //sql
    private static Connection connection;
    private static ResultSet rs;

    public static void main(String[] args) throws Exception {

        connect();    //initialize connection to MySQL server
        scanner = new Scanner(System.in);
        scanner.reset();
        ///////////////////////
        //Customer login prompt
        ///////////////////////
        while (true) {
            String resp;    //user response
            scanner = new Scanner(System.in);

            email = null;
            pass = null;
            displayName = null;

            System.out.println("\n\n\n\n\nWelcome to the Hulton Customer Registration app!");
            System.out.println("1 | Login to existing account");
            System.out.println("2 | Register a new account");
            System.out.println("3 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    System.out.println(resp);
                    login(scanner);
                    break;
                case "2":
                    System.out.println(resp);
                    register(scanner);
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid response!");
            }
        }
    }

    private static void login(Scanner scanner){
        clearConsole();
        scanner.reset();
        System.out.println("Email: ");
        email = scanner.nextLine();
        System.out.println("Password: ");
        pass = scanner.nextLine();

        //SQL statement to check user credentials
        String query = ("SELECT Name, Email, Password " +
                "FROM CUSTOMER " +
                "WHERE Email='" + email + "' AND Password='" + pass + "'");
        Statement statement = null;
        ResultSet rs = null;

        //if (query.isEmpty()){ return rs; }

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            rs.next();
            if (rs.isAfterLast()){
                System.out.println("Access denied. Do you need to register an account?");
                return;
            }
            else {
                displayName = rs.getString("Name");
                System.out.println("Welcome to account management, " + displayName + "! Taking you back to main menu I guess");
            }

        } catch (SQLException e) {
            System.err.print("Error in SQL query. ");
            System.err.println(e.getMessage());
        } finally {
            if (statement != null) {
                try{
                    statement.close();
                } catch (SQLException ex){
                    System.err.println("Error in closing SQL query");
                }
            }
        }

        //account management
    }

    private static void register(Scanner scanner){
        clearConsole();
        String confirmPass;
        String address;
        String phone;

        while (true) {
            System.out.print("Enter your email: ");
            String user = scanner.nextLine();
            //SQL query checks for existing userName
            System.out.print("Enter a password: ");
            String pass = scanner.nextLine();
            System.out.print("Confirm your password: ");
            confirmPass = scanner.nextLine();

            if (pass.equals(confirmPass)) {
                break;
            }

            System.out.println("\nPasswords do not match, please try again.");
        }

        //basic account setup
        System.out.println("\n\nEnter your full name:");
        displayName = scanner.next();

        System.out.println("Enter your address:");
        address = scanner.next();

        while (true) {
            System.out.println("Enter your phone number:");
            phone = scanner.next();

            //check for valid phone number
            try{
                if (phone.length() == 0 || phone.length() == 10){
                    Integer.parseInt(phone);
                } else{
                    throw new NumberFormatException();
                }
                break;
            } catch (NumberFormatException e){
                System.out.println("Please enter a valid phone number, or leave the phone number blank.");
            }
        }

        System.out.println("\n\n\n");
        //credit card info
        String CNumber;
        String Ctype;
        String Baddress;
        String CCode;
        String ExpDate;
        String CName;

        while (true) {
            System.out.println("Enter your credit card number:");
            CNumber = scanner.next();

            if (CNumber.equalsIgnoreCase("exit")){
                return;
            }
            //check for valid phone number
            try{
                Integer.parseInt(CNumber);
                break;
            } catch (NumberFormatException e){
                System.out.println("Please enter a valid credit card number, or type 'exit' to cancel registration.");
            }
        }

        
        System.out.println("Enter your credit card type:");
        Ctype = scanner.next();

        System.out.println("Enter the name on your credit card:");
        CName = scanner.next();

        System.out.println("Enter your billing address:");
        Baddress = scanner.next();

        while (true) {
            System.out.println("Enter your credit card security code:");
            CCode = scanner.next();

            if (CCode.equalsIgnoreCase("exit")){
                return;
            }
            //check for valid phone number
            try{
                Integer.parseInt(CCode);
                break;
            } catch (NumberFormatException e){
                System.out.println("Please enter a valid credit card code, or type 'exit' to cancel registration.");
            }
        }

        System.out.println("Enter your credit card expiration date:");
        ExpDate = scanner.next();

        //insert all information into db
    }


    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private static void connect(){
        String url = "jdbc:mysql://sql2.njit.edu:3306/az62";
        String DBUsername = "az62";
        String DBpassword = "8wlgKd19A";
        String dbName = "az62";
        //Connect to database
        System.out.println("Connecting to MySQL server...");
        try {
            connection = DriverManager.getConnection(url, DBUsername, DBpassword);
        } catch (java.sql.SQLException e){
            System.err.println("MySQL server access denied, check your credentials.");
            return;
        }
    }
}

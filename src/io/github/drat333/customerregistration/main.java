package io.github.drat333.customerregistration;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class main {

    //user
    private static String email;
    private static String pass;
    private static String displayName;
    private static Scanner scanner;

    //sql
    private static Connection connection;
    private static ResultSet rs;
    private static Statement statement;
    private static String query;

    public static void main(String[] args) throws Exception {

        if (!connect()){            //initialize connection to MySQL server
            return;
        }

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
        System.out.println("Email: ");
        email = scanner.nextLine();
        System.out.println("Password: ");
        pass = scanner.nextLine();

        //SQL statement to check user credentials
        query = ("SELECT Name, Email, Password " +
                "FROM CUSTOMER " +
                "WHERE Email='" + email + "' AND Password='" + pass + "'");
        statement = null;
        rs = null;

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            rs.next();
            if (rs.isAfterLast()){
                System.out.println("Access denied. Do you need to register an account?");
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

        // TODO: 5/3/2017 allow exiting at any time 

        while (true){
            System.out.print("Enter your email: ");
            email = scanner.nextLine();

            try {
                query = "SELECT Email " +
                        "FROM CUSTOMER " +
                        "WHERE Email='" + email + "';"; //sql query checks if email is in use

                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                if (rs.isBeforeFirst()){
                    System.out.println("Sorry, that email is already in use. Please try again.\n");
                    continue;
                }
            } catch (java.sql.SQLException e){
                System.err.println("SQL Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                        System.err.println("Error in closing SQL query");
                    }
                }
            }
            break;
        }

        while (true) {
            System.out.print("Enter a password: ");
            pass = scanner.nextLine();
            System.out.print("Confirm your password: ");
            confirmPass = scanner.nextLine();

            if (pass.equals(confirmPass)) {
                break;
            }

            System.out.println("\nPasswords do not match, please try again.");
        }

        //basic account setup
        System.out.print("\n\nEnter your full name: ");
        displayName = scanner.nextLine();

        System.out.print("Enter your address: ");
        address = scanner.nextLine();

        while (true) {
            System.out.print("Enter your phone number: ");
            phone = scanner.nextLine();

            //check for valid phone number
            if (phone.length() == 10) {
                if (isNumeric(phone)) {
                    break;
                }
            } else if (phone.length() == 0) {
                break;
            } else {
                System.out.println(phone.length());
                System.out.println("Please enter a valid phone number, or leave the phone number blank.");
                continue;
            }
        }

        System.out.println("\n");
        //credit card info
        String CNumber;
        String Ctype;
        String Baddress;
        String CCode;
        String ExpDate;
        String CName;

        while (true) {
            System.out.println("Enter your credit card number:");
            CNumber = scanner.nextLine();

            if (CNumber.equalsIgnoreCase("exit")){
                return;
            }
            //check for valid phone number
            if (CNumber.length() == 16 && isNumeric(CNumber)){
                break;
            } else{
                System.out.println("Please enter a credit card number, or leave the phone number blank.");
            }
        }

        
        System.out.println("Enter your credit card type:");
        Ctype = scanner.nextLine();

        System.out.println("Enter the name on your credit card:");
        CName = scanner.nextLine();

        System.out.println("Enter your billing address:");
        Baddress = scanner.nextLine();

        while (true) {
            System.out.println("Enter your credit card security code:");
            CCode = scanner.nextLine();

            if (CCode.equalsIgnoreCase("exit")){
                return;
            }
            //check for valid credit card security code
            if (CNumber.length() == 16 && isNumeric(CNumber)){
                break;
            } else{
                System.out.println("Please enter a valid credit card security code, or leave the phone number blank.");
            }
        }

        while (true) {

            System.out.println("Enter your credit card expiration year:");
            ExpDate = scanner.nextLine();
            if (isNumeric(ExpDate) && ExpDate.length() == 4){
                break;
            }
            System.out.println("\nInvalid year format.");
        }

        try {
            // FIXME: 5/3/2017 Not the right query
            statement = connection.createStatement();
            query = "INSERT INTO CUSTOMER " +
                    "(Name,Address,Phone_No,Email,Password) " +
                    "VALUES " +
                    "('" + displayName + "','" + address + "','" + phone + "','" + email + "','" + pass + "');"; //insert user info
            statement.executeUpdate(query);
            System.out.println(query);

            query = "INSERT INTO CREDIT_CARD " +
                    "VALUES " +
                    "('" + CNumber + "','" + Ctype + "','" + Baddress + "','" + CCode + "','" + ExpDate + "','" + CName + "');"; //insert user CC info
            statement.executeUpdate(query);


        } catch (java.sql.SQLException e){
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.err.println("Error in closing SQL query");
                }
            }
        }

        System.out.println("\nSuccess! You are now registered to make reservation at Hulton Hotels.\n");
    }


    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private static boolean connect(){
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
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str)
    {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

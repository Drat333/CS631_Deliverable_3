package io.github.drat333.customerregistration;

import java.sql.*;
import java.util.*;

public class main {

    //user
    private static String email;
    private static String displayName;
    private static int customerID;

    private static Scanner scanner;
    private static String resp;

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
        ///////////////////////
        //Customer manageAccount prompt
        ///////////////////////
        System.out.println("\n\n\n");
        while (true) {
            scanner = new Scanner(System.in);

            email = null;
            displayName = null;

            System.out.println("\n\nWelcome to the Hulton Account Management and Registration app!");
            System.out.println("1 | Manage an existing account");
            System.out.println("2 | Register a new account");
            System.out.println("3 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    System.out.println(resp);
                    manageAccount();
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

    private static void manageAccount(){

        //login
        clearConsole();
        System.out.println("Email: ");
        email = scanner.nextLine();
        System.out.println("Password: ");
        String pass = scanner.nextLine();

        //SQL statement to check user credentials
        query = ("SELECT CID, Name, Email, Password " +
                "FROM CUSTOMER " +
                "WHERE Email='" + email + "' AND Password='" + pass + "'");

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            if (rs.next()){
                displayName = rs.getString("Name");
                customerID = rs.getInt("CID");
            }
            else {
                System.out.println("\n\n\nAccess denied. Do you need to register an account?");
                if (statement != null) {
                    statement.close();
                }
                return;
            }

            if (statement != null) {
                statement.close();
            }



            //account management
            clearConsole();
            while (true) {
                String attribute;
                String value;
                System.out.println("\n\n\n\nWelcome to account management, " + displayName + "!");
                System.out.println("1 | Change email address");
                System.out.println("2 | Change password");
                System.out.println("3 | Update your name");
                System.out.println("4 | Update phone number");
                System.out.println("5 | Update home address");
                System.out.println("  | ");
                System.out.println("0 | Logout");

                resp = scanner.nextLine();

                switch (resp) {
                    case "1":
                        attribute = "Email";

                        System.out.println("\n");
                        while (true) {
                            System.out.println("\nEnter a new email:");
                            value = scanner.nextLine();
                            if (value.equalsIgnoreCase("exit")) {
                                return;
                            }

                            query = "SELECT Email " +
                                    "FROM CUSTOMER " +
                                    "WHERE Email='" + value + "';"; //sql query checks if email is in use

                            statement = connection.createStatement();
                            rs = statement.executeQuery(query);

                            if (rs.next()) {
                                System.out.println("\n\nThat email is already in use, please try again.");
                                if (statement != null) {
                                    statement.close();
                                }
                                continue;
                            }

                            if (statement != null) {
                                statement.close();
                            }
                            break;
                        }
                        break;
                    case "2":
                        attribute = "Password";

                        System.out.println("\n");
                        while (true) {
                            System.out.println("\nEnter a new password:");
                            value = scanner.nextLine();
                            if (value.equalsIgnoreCase("exit")) {
                                return;
                            }
                            System.out.println("Confirm your new password:");
                            String confirmPass = scanner.nextLine();

                            if (!value.equals(confirmPass)) {
                                System.out.println("\n\nPasswords didn't match! Please try again.");
                                continue;
                            }
                            break;
                        }
                        break;

                    case "3":
                        attribute = "Name";
                        System.out.println("\n\nEnter your new name:");
                        value = scanner.nextLine();
                        displayName = value;
                        break;
                    case "4":
                        attribute = "Phone_No";

                        System.out.println("\n");
                        while (true) {
                            System.out.println("\nEnter your new phone number:");
                            value = scanner.nextLine();
                            if (value.equalsIgnoreCase("exit")) {
                                return;
                            }

                            if (value.length() == 0){
                                break;
                            } else if(value.length() == 10) {
                                if (isNumeric(value)) {
                                    break;
                                }
                            } else {
                                System.out.println("\n\nPlease enter a valid phone number, or leave the phone number blank.");
                            }
                        }
                        break;
                    case "5":
                        attribute = "Address";
                        System.out.println("\n\nEnter your new address:");
                        value = scanner.nextLine();
                        break;
                    case "0":
                        email = null;
                        displayName = null;
                        customerID = -1;
                        return;
                    default:
                        System.out.println("\nInvalid response!\n");
                        continue;
                }

                if (statement != null) {
                    statement.close();
                }

                if (value.equalsIgnoreCase("exit")) {
                    return;
                }

                query = "UPDATE CUSTOMER " +
                        "SET " + attribute + "='" + value + "'" +
                        "WHERE CID='" + customerID + "';";

                statement = connection.createStatement();
                statement.executeUpdate(query);

                System.out.println("\n\n\nAccount updated!");

                if (statement != null) {
                    statement.close();
                }
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
    }

    private static void register(Scanner scanner){
        clearConsole();
        String pass;
        String confirmPass;
        String address;
        String phone;

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
                System.out.println("Please enter a valid phone number, or leave the phone number blank.");
            }
        }

        try {
            statement = connection.createStatement();
            query = "INSERT INTO CUSTOMER " +
                    "(Name,Address,Phone_No,Email,Password) " +
                    "VALUES " +
                    "('" + displayName + "','" + address + "','" + phone + "','" + email + "','" + pass + "');"; //insert user info
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

    private static boolean isNumeric(String str)
    {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

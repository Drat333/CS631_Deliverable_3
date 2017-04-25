package io.github.drat333.customerregistration;

/**
 * Created by Adrian on 4/18/2017.
 */

import java.sql.*;
import java.util.Scanner;

public class main {
    //MySQL server info
    private static String url = "jdbc:mysql://sql2.njit.edu:3306/az62";
    private static String DBUsername = "az62";
    private static String DBpassword = "8wlgKd19A";
    private static Connection connection;
    private static String dbName = "az62";


    public static void main(String[] args) throws Exception {

        //Connect to database
        System.out.println("Connecting to MySQL server...");
        try {
            connection = DriverManager.getConnection(url, DBUsername, DBpassword);
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e){
            System.err.println("Failed to connect to MySQL server, exiting.");
            return;
        }

        clearConsole();

        ///////////////////////
        //Customer login prompt
        ///////////////////////
        while (true) {
            String resp;    //user response
            Scanner scanner = new Scanner(System.in);

            System.out.println("\n\n\n\n\nWelcome to the Hotel Customer Registration app!");
            System.out.println("1 | Login to existing account");
            System.out.println("2 | Register a new account");
            System.out.println("3 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    login(scanner);
                case "2":
                    register(scanner);
                case "3":
                    return;
                default:
                    System.out.println("Invalid response!");
            }
        }
    }

    private static void sampleQuery(){
        //Sample SQL query
        Statement statement = null;
        ResultSet rs;
        String query = "SELECT * FROM HOTEL";
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            System.out.println("\n" + query);
            System.out.println("HotelID\t| Street\t| Country\t\t| State\t| Zip");
            while (rs.next()) {
                String HotelID = rs.getString("HotelID");
                String Street = rs.getString("Street");
                String Country = rs.getString("Country");
                String State = rs.getString("State");
                String Zip = rs.getString("Zip");
                System.out.println(HotelID + "\t" + Street + "\t" + Country + "\t\t" + State + "\t" + Zip);
            }

        } catch (SQLException e) {
            System.err.println("Error in SQL query");
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

    private static void login(Scanner scanner){
        String user = scanner.next("User name: ");
        String pass = scanner.next("Password: ");

        if (!(user == pass && user == "admin")){        //SQL statement to check user credentials
            System.out.println("Access denied");
        }

        //account management
    }

    private static void register(Scanner scanner){
        String user = scanner.next("Enter a user name: ");
        //SQL query checks for existing username
        String pass = scanner.next("Enter a password: ");
        String confirmPass = scanner.next("Re-enter your password: ");
        if (pass == confirmPass){
            //insert password into db
        }

        //basic account setup
    }


    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

}

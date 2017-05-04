package io.github.drat333.reservations_statistics;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class main {

    //user variables
    public static boolean loggedIn;
    public static String email;
    public static String displayName;

    //variables for Scanner
    public static Scanner scanner;
    public static String resp;      //user response

    //SQL
    private static Connection connection;
    private static ResultSet rs;
    private static Statement statement;
    private static String query;


    public static void main(String[] args) throws Exception {

        if (!connect()) {            //initialize connection to MySQL server
            return;
        }

        scanner = new Scanner(System.in);
        clearConsole();

        while (true) {
            System.out.println("\n\n\nWelcome to the Hulton Hotels Statistics app!");
            System.out.println("1 | Login");
            System.out.println("0 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    System.out.println(resp);
                    if (!login()) {
                        continue;
                    }
                    break;
                case "0":
                    goodbye();
                    return;
                default:
                    System.out.println("\nInvalid response!\n");
            }
            if (loggedIn) {
                statistics();
            }
        }
    }

    private static boolean login() {
        //clearConsole();
        clearConsole();
        System.out.println("Email: ");
        email = scanner.nextLine();
        System.out.println("Password: ");
        String pass = scanner.nextLine();

        //SQL statement to check user credentials
        query = ("SELECT Name, Email, Password " +
                "FROM CUSTOMER " +
                "WHERE Email='" + email + "' AND Password='" + pass + "'");

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            if (!rs.next()) {
                System.out.println("Access denied. Do you need to register an account?");
            } else {
                displayName = rs.getString("Name");
                loggedIn = true;
                return true;
            }

        } catch (SQLException e) {
            System.err.print("Error in SQL query. ");
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.err.println("Error in closing SQL query");
                    return false;
                }
            }
        }

        return false;
    }


    private static void statistics() {
        //compute statistics
        Date startDate;
        Date endDate;

        System.out.println("Welcome to the Hulton Hotels Statistics app, " + displayName + "!");
        while (true) {
            try {

                DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy").toFormatter();
                LocalDate localDate;

                System.out.println("\n\n====Enter a date range, or type 'exit'====");

                System.out.println("Start date (MM/dd/yyyy):");
                resp = scanner.nextLine();

                if (resp.equalsIgnoreCase("exit")) {
                    loggedIn = false;
                    displayName = null;
                    email = null;
                    return;
                }

                localDate = LocalDate.parse(resp, formatter);
                startDate = Date.valueOf(localDate);

                System.out.println("\nEnd date (MM/dd/yyyy):");
                resp = scanner.nextLine();

                if (resp.equalsIgnoreCase("exit")) {
                    loggedIn = false;
                    displayName = null;
                    email = null;
                    return;
                }

                localDate = LocalDate.parse(resp, formatter);
                endDate = Date.valueOf(localDate);

            } catch (DateTimeParseException e) {
                System.out.println("\nInvalid date format! Please use the format MM/dd/yyyy, ex. 05/22/1997\n");
                continue;
            }

            System.out.println("\nmuh stats\n");
            try {
                // TODO: 4/30/2017 computed highest rated room type per hotel

                query = "SELECT HotelID, RType " +
                        "FROM(SELECT M.HotelID, M.RType, AVG(Rating) AS RAvgScore " +
                        "     FROM ROOM AS M,ROOM_REVIEW AS W,ROOM_RESERVATION AS R " +
                        "     WHERE CheckInDate BETWEEN '" + startDate + "' AND '" + endDate + "' AND " +
                        "        M.HotelID=W.HotelID AND M.HotelID=R.HotelID AND " +
                        "        M.RoomNo=W.RoomNo AND M.RoomNo=R.RoomNo " +
                        "     GROUP BY W.HotelID,RType) as avgscore " +
                        "GROUP BY HotelID " +
                        "HAVING MAX(RAvgScore);";

                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                if (rs.next()) {
                    System.out.println("\n\nHighest rated room type per hotel:");
                    rs.beforeFirst();
                    System.out.println("HotelID\t\t\tRoom");
                    System.out.println("----------------------");
                    while (rs.next()){
                        System.out.println(rs.getString("HotelID") + "\t|\t" + rs.getString("RType"));
                    }
                }

                if (statement != null) {
                    statement.close();
                }
/*
                // TODO: 4/30/2017 compute 5 best customers, in terms of money spent in reservations

                query = "SELECT RType " +
                        "FROM(SELECT RType, SUM(RATING) AS totalscore " +
                        "FROM ROOM,ROOM_REVIEW " +
                        "WHERE ROOM.HotelID=ROOM_REVIEW.HotelID AND " +
                        "ROOM.RoomNo=ROOM_REVIEW.RoomNo " +
                        "GROUP BY RType) as totalscore " +
                        "HAVING MAX(totalscore);";

                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                if (rs.next()) {
                    System.out.println("\n\nTop 5 Customers (in terms of money spent):");
                    rs.beforeFirst();
                    System.out.println("Customer Name");
                    System.out.println("--------------");
                    while (rs.next()){
                        System.out.println(rs.getString("Name"));
                    }
                }

                if (statement != null) {
                    statement.close();
                }

                */

                // TODO: 4/30/2017 compute highest rated breakfast type across all hotels

                query = "SELECT HotelID, BType " +
                        "FROM(SELECT R.HotelID, B.BType, AVG(Rating) AS BAvgScore " +
                        "     FROM BREAKFAST_REVIEW  AS W, BREAKFAST AS B, RRESV_BREAKFAST AS R " +
                        "     WHERE CheckInDate BETWEEN '" + startDate + "' AND '" + endDate + "' AND " +
                        "     W.HotelID=B.HotelID AND W.HotelID=R.HotelID AND " +
                        "     W.BType=B.BType AND W.BType=R.BType " +
                        "     GROUP BY HotelID, BType) AS avgscore " +
                        "HAVING MAX(BAvgScore);";
                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                if (rs.next()) {
                    System.out.println("\n\nHighest rated breakfast across all hotels:");
                    System.out.println("HotelID\t\t\tBreakfast");
                    System.out.println("--------------------------");
                    System.out.println(rs.getString("HotelID") + "\t|\t" + rs.getString("BType"));
                }

                if (statement != null) {
                    statement.close();
                }



                // TODO: 4/30/2017 compute highest rated service type across all hotels

                query = "SELECT HotelID, SType " +
                        "FROM(SELECT R.HotelID, S.SType, AVG(Rating) AS SAvgScore " +
                        "     FROM SERVICE_REVIEW  AS W, SERVICE AS S, RRESV_SERVICE AS R " +
                        "     WHERE CheckInDate BETWEEN '" + startDate + "' AND '" + endDate + "' AND " +
                        "     W.HotelID=S.HotelID AND W.HotelID=R.HotelID AND " +
                        "     W.SType=S.SType AND W.SType=S.SType " +
                        "     GROUP BY HotelID, SType) AS avgscore " +
                        "HAVING MAX(SAvgScore);";

                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                if (rs.next()) {
                    System.out.println("\n\nHighest rated service across all hotels:");
                    System.out.println("HotelID\t\t\tService");
                    System.out.println("--------------------------");
                    System.out.println(rs.getString("HotelID") + "\t|\t" + rs.getString("sType"));
                }


                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                System.err.print("Error in SQL query. ");
                System.err.println(e.getMessage());
                return;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                        System.err.println("Error in closing SQL query");
                        return;
                    }
                }
            }
        }
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


    private static void goodbye() {
        clearConsole();
        System.out.println("Thank you for using the Hulton Reservation Statistics app.");
        System.out.println("Goodbye!");
    }
}

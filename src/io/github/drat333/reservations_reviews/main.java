package io.github.drat333.reservations_reviews;

import javax.swing.tree.FixedHeightLayoutCache;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class main {

    //user variables
    private static boolean loggedIn;
    private static String email;
    private static String displayName;

    //variables for Scanner
    private static Scanner scanner;
    private static String resp;      //user response

    //SQL
    private static Connection connection;
    private static ResultSet rs;
    private static Statement statement;

    private static String query;




    public static void main(String[] args) {

        if (!connect()){            //initialize connection to MySQL server
            return;
        }

        clearConsole();

        ///////////////////////
        //Customer login prompt
        ///////////////////////
        while (true) {
            scanner = new Scanner(System.in);

            System.out.println("\n\n\nWelcome to the Hulton Reservation and Reviews app!");
            System.out.println("1 | Login");
            System.out.println("0 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    System.out.println(resp);
                    if (login()){
                        goodbye();
                        return;
                    }
                    break;
                case "0":
                    goodbye();
                    return;
                default:
                    System.out.println("Invalid response!");
            }
            if (loggedIn){
                break;
            }
        }

        while(true) {
            System.out.println("\n\n\n\n\nWelcome to the Hulton Reservation and Reviews app, " + displayName + "!");
            System.out.println("1 | Search hotels and make a reservation");
            System.out.println("2 | Leave a review");
            System.out.println("3 | Logout");
            System.out.println("  |");
            System.out.println("0 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    hotelSearch();
                    break;
                case "2":
                    leaveReviews();
                    break;
                case "3":
                    //no need for SQL statements here (probably)
                    email = null;
                    displayName = null;
                    loggedIn = false;
                    main(null);     //log out then return to main menu
                case "0":
                    goodbye();
                    return;
            }
        }

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



    private static boolean login(){
        //clearConsole();
        while (true) {
            System.out.println("\n\nEmail: ");
            String email = scanner.nextLine();
            System.out.println("Password: ");
            String pass = scanner.nextLine();

            if (email.equals("admin") || pass.equals("admin")) {        //SQL statement to check user credentials
                System.out.println("Invalid credentials.");
                System.out.println("1 | Login again");
                System.out.println("0 | Exit");
                resp = scanner.nextLine();

                switch (resp){
                    case "1":
                        break;
                    case "0":
                        return true;    //quit application
                    default:
                        System.out.println("Invalid response!");
                }
            } else{
                displayName = "admin";  //SQL statement: get user's real name
                loggedIn = true;
                return false;
            }
        }
    }








    private static void hotelSearch() {
        // TODO: 4/28/2017 Fill in SQL statements and test.
        // TODO: 4/29/2017 Add discounts. Change to allow user to input a room type and date; room numbers behind-the-scenes
        // this method should not make any table changes until the end, when the customer confirms their choices

        String country;
        String state;
        String hotelID; //make an int?
        String hotelName;
        ResultSet rs;


        clearConsole();
        System.out.println("======Hulton Hotel Search======");
        System.out.println("Type 'exit' to return to the main menu at any time.");

        try {
            //search for a hotel
            pickHotel:
            while (true) {

                country:
                while (true) {

                    System.out.println("\nPick a country:");

                    query = "SELECT DISTINCT Country " +
                            "FROM Hotels;"; //get a list of countries with hotels

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    int i = 1;
                    rs.beforeFirst();
                    while (rs.next()) {
                        System.out.println(Integer.toString(i) + " | " +
                                rs.getString("Country"));
                        i++;
                    }


                    resp = scanner.nextLine();
                    if (resp.equalsIgnoreCase("exit")) {
                        return;
                    }

                    //try to convert the input into an int, to check if it's a valid input
                    int selection;
                    try {
                        selection = Integer.parseInt(resp);
                        if (selection < 1 || selection > i) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;  //make em do it again
                    }


                    rs.beforeFirst();
                    while (i != 0) {
                        rs.next();
                        i--;
                    }

                    country = rs.getString("Country");
                    break;
                }

                //country is picked
                if (statement != null) {
                    statement.close();
                }

                state:
                while (true) {

                    System.out.println("\nPick a state in " + country + ":");

                    query = "SELECT DISTINCT State " +
                            "FROM Hotels" +
                            "WHERE Country='" + country + "';"; //get a list of states with hotels in selected country

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    int i = 1;
                    rs.beforeFirst();
                    while (rs.next()) {
                        System.out.println(Integer.toString(i) + " | " +
                                rs.getString("State"));
                        i++;
                    }


                    resp = scanner.nextLine();
                    if (resp.equalsIgnoreCase("exit")) {
                        return;
                    }

                    //try to convert the input into an int, to check if it's a valid input
                    int selection;
                    try {
                        selection = Integer.parseInt(resp);
                        if (selection < 1 || selection > i) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;  //make em do it again
                    }


                    rs.beforeFirst();
                    while (i != 0) {
                        rs.next();
                        i--;
                    }

                    state = rs.getString("State");
                    break;
                }

                //state is picked
                if (statement != null) {
                    statement.close();
                }


                //pick a hotel in state, country
                query = "SELECT * " +
                        "FROM HOTEL " +
                        "WHERE Country='" + country + "' AND State='" + state + "';";

                while (true) {

                    if (statement != null) {
                        statement.close();
                    }

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    System.out.println("Which hotel in " + state + ", " + country + " would you like to view?");
                    int i = 1;

                    rs.beforeFirst();
                    while (rs.next()) {
                        System.out.println(Integer.toString(i) + " | " +
                                rs.getString("Street"));
                        i++;
                    }


                    resp = scanner.nextLine();
                    if (resp.equalsIgnoreCase("exit")) {
                        return;
                    }

                    //try to convert the input into an int, to check if it's a valid input
                    int selection;
                    try {
                        selection = Integer.parseInt(resp);
                        if (selection < 1 || selection > i) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;  //make em do it again
                    }


                    rs.beforeFirst();
                    while (i != 0) {
                        rs.next();
                        i--;
                    }
                    hotelID = rs.getString("HotelID");
                    System.out.println("\nYou have chosen the hotel at " + rs.getString("Street") + " in " + state + ", " + country + ".");

                    break;
                }


                if (statement != null){
                    statement.close();
                }





                //hotel has been chosen at this point
                System.out.println("Would you like to create a reservation? (Y/N)");
                resp = scanner.nextLine();

                switch (resp.toUpperCase()) {
                    case "Y":
                        break pickHotel;
                    case "N":
                        continue pickHotel;
                    case "EXIT":
                        return;
                    default:
                        System.out.println("\nPlease enter Y or N to create a reservation, or 'exit' to return to the main menu.\n");
                }

            }




            //pick a room type
            query = null; // sql statement that gets the room types available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            roomTypes:
            while (true) {
                System.out.println("\nWhat type of room would you like to reserve?");
                int i = 1;

                //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
                rs.beforeFirst();
                while (rs.next()) {
                    System.out.println(Integer.toString(i) + " | " + rs.getString("Rtype"));
                    i++;
                }


                resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("exit")) {
                    return;
                }

                //try to convert the input into an int
                int selection;
                try {
                    selection = Integer.parseInt(resp);
                    if (selection < 1 || selection > i) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;  //make em do it again
                }

                String Rtype;
                rs.beforeFirst();
                while (i != 0) {
                    rs.next();
                    i--;
                }
                Rtype = rs.getString("Rtype");

                break;
            }

            if (statement != null){
                statement.close();
            }

            //start making a reservation - pick a room
            query = null; // sql statement that gets all AVAILABLE rooms, using Rtype

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            rooms:
            while (true) {
                System.out.println("\nWhich room would you like to reserve?");
                int i = 1;

                //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
                rs.beforeFirst();
                while (rs.next()) {
                    System.out.print(Integer.toString(i) + " | " +
                            rs.getString("Price") + " per day," +
                            rs.getString("Capacity") + " people, Floor " +
                            rs.getString("Floor"));
                    if (rs.getString("Discount") != null) {
                        System.out.print("\tDiscounted! ");
                        int discount = Integer.parseInt(rs.getString("Discounted"));
                        discount *= 100;
                        System.out.print(Integer.toString(discount) + "% off");
                    }
                    System.out.println("\n\tDescription: " + rs.getString("Description"));
                    i++;
                }


                resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("exit")) {
                    return;
                }

                //try to convert the input into an int
                int selection;
                try {
                    selection = Integer.parseInt(resp);
                    if (selection < 1 || selection > i) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;  //make em do it again
                }

                String roomID;
                    rs.beforeFirst();
                    while (i != 0) {
                        rs.next();
                        i--;
                    }
                    roomID = rs.getString("RoomNo");
                break;
            }

            if (statement != null){
                statement.close();
            }

            //pick reservation dates
            // TODO: 5/2/2017 put this above room selection
            dates:
            while (true) {
                System.out.println("Enter your desired check-in date for your stay: ");
                String startDate = scanner.nextLine();
                if (startDate.equalsIgnoreCase("exit")) {
                    return;
                }

                System.out.println("Enter your desired check-out date for your stay:");
                String endDate = scanner.nextLine();
                if (endDate.equalsIgnoreCase("exit")) {
                    return;
                }

                query = null;//SQL statement checks if date is available

                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                if (startDate.equals(endDate) /*placeholder, dates available*/) {
                    System.out.println("\nCongrats! Those dates are available.");
                    break;
                } else {
                    System.out.println("We're sorry, those dates are unavailable.");
                }
            }

            if (statement != null){
                statement.close();
            }


            //pick breakfasts
            query = null; //sql statement that gets all breakfasts available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            ArrayList<String> BType = new ArrayList<>();
            ArrayList<Integer> BCount = new ArrayList<>();

            rs.next();
            if (!rs.isAfterLast()) {
                System.out.println("\n\nBreakfasts are available for your reservation.");
                System.out.println("Please indicate how many of each breakfast you would like (1 per day, per person).");

                rs.beforeFirst();
                while (rs.next()) {   //if breakfasts are available
                    while (true) {
                        BType.add(rs.getString("BType"));
                        System.out.print(rs.getString("BType") + " ($ " + rs.getString("BPrice") + "/order): ");

                        resp = scanner.nextLine();
                        if (resp.equalsIgnoreCase("exit")) {
                            return;
                        }

                        //try to convert the input into an int
                        int selection;
                        try {
                            selection = Integer.parseInt(resp);
                            if (selection < 0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("\nPlease enter a valid number (at least 0), or type 'exit' to return to main menu.\n");
                            continue;  //make em do it again
                        }
                        BCount.add(selection);
                        break;
                    }
                }
            } else{
                System.out.println("\n\nThere are no breakfasts available for this reservation.");
            }


            if (statement != null){
                statement.close();
            }

            //pick services
            query = null;   //sql query that gets the services available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            ArrayList<String> SType = new ArrayList();
            services:
            while (true) {
                rs.next();
                if (!rs.isAfterLast()) {
                    System.out.println("\n\nServices are available for your reservation.");
                    System.out.println("Please indicate (Y/N) for each service.");

                    rs.beforeFirst();
                    while (rs.next()) {   //if services are available
                        prompt:
                        while (true) {
                            System.out.print(rs.getString("SType") + " ($" + rs.getString("SPrice") + "): ");

                            resp = scanner.nextLine();
                            switch (resp.toUpperCase()) {
                                case "Y":
                                    SType.add(rs.getString("SType"));
                                    break prompt;
                                case "N":
                                    break prompt;
                                case "EXIT":
                                    return;
                                default:
                                    System.out.println("\nPlease indicate (Y/N) for if you want the service, or exit to return to the main menu.\n");
                                    break;
                            }
                        }
                    }
                }
                break;
            }

            if (statement != null){
                statement.close();
            }

            int TotalPrice; //TODO: this is probably optional but could be nice to implement. requires sql query
            System.out.println("\nAll aspects of your visit are set!");

            confirmation:
            while (true) {
                System.out.println("Would you like to confirm your reservation now? (Y/N)"); //total price ~would~ go here

                resp = scanner.nextLine();

                switch (resp.toUpperCase()) {
                    case "Y":
                        query = null;  //insert all values into sql tables

                        statement = connection.createStatement();
                        rs = statement.executeQuery(query);
                        System.out.println("Congratulations! You have successfully reserved your visit.");  //TODO: perhaps add more info here?
                        break;
                    case "N":
                        break;
                    case "EXIT":
                        return;
                }
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
    }








    private static void leaveReviews(){
        // TODO: 4/30/2017 Add in SQL statements and test
        try {
            query = null; //sql statement that gets the list of reservations the user has made

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            rs.next();
            if (rs.isAfterLast()) {
                System.out.println("You don't have any reservations to review currently.");
                System.out.println("Returning to the main menu.\n");
                return;
            }


            System.out.println("======Hulton Review App======");
            //pick a stay
            String InvoiceNo;
            while (true) {
                System.out.println("Please pick a stay that you would like to review:");

                int i = 1;

                rs.beforeFirst();
                while (rs.next()) {
                    System.out.println(Integer.toString(i) + " | " +
                            rs.getString("State") + ", " +
                            rs.getString("Country") + " - " +
                            rs.getString("CheckInDate"));
                    i++;
                }


                resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("exit")) {
                    return;
                }

                //try to convert the input into an int
                int selection;
                try {
                    selection = Integer.parseInt(resp);
                    if (selection < 1 || selection > i) {
                        throw new NumberFormatException();
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    //make em do it again
                }


                rs.beforeFirst();
                while (i != 0) {
                    rs.next();
                    i--;
                }
                InvoiceNo = rs.getString("InvoiceNo");

                break;

            }


            //pick something to review
            int selection;
            while (true) {
                System.out.println("What would you like to review?");
                System.out.println("1 | Room");
                System.out.println("2 | Breakfast");
                System.out.println("3 | Services");

                resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("exit")) {
                    return;
                }

                try {
                    selection = Integer.parseInt(resp);
                    if (selection < 1 || selection > 3) {
                        throw new NumberFormatException();
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    //make em do it again
                }
            }

            String review;
            int i = 0;
            // TODO: 4/30/2017 should we cancel the review if the user only types "exit"? very minor detail
            switch (selection) {
                case 1:
                    //room

                    System.out.println("Write your review of your room (limit 500 characters):");
                    review = scanner.nextLine();

                    query = null; //sql statement that inserts review

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);
                    break;
                case 2:
                    //breakfast - reviews are opt-out (with "skip")

                    query = null;  //sql statement that gets list of breakfasts ordered

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    while (rs.next()) {
                        i++;
                        System.out.println("Write your review for " + rs.getString("BType") + " (limit 500 characters), or type 'skip':");
                        review = scanner.nextLine();
                        if (review.equalsIgnoreCase("skip")) {
                            continue;
                        }

                        if (statement != null){
                            statement.close();
                        }

                        query = null;  //sql statement that inserts review

                        statement = connection.createStatement();
                        rs = statement.executeQuery(query);
                    }
                    if (i == 0) {
                        // FIXME: 5/2/2017 not how to do this
                        System.out.println("You did not order any breakfasts for this stay.");
                    }
                    break;
                case 3:
                    //services - reviews are opt-out (with "skip")

                    query = null;  //sql statement that gets list of services ordered

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    while (rs.next()) {
                        i++;
                        System.out.println("Write your review for " + rs.getString("SType") + " (limit 500 characters), or type 'skip':");
                        review = scanner.nextLine();
                        if (review.equalsIgnoreCase("skip")) {
                            continue;
                        }

                        if (statement != null) {
                            statement.close();
                        }

                        query = null;  //sql statement that inserts review

                        statement = connection.createStatement();
                        rs = statement.executeQuery(query);

                    }
                    if (i == 0) {
                        // FIXME: 5/2/2017 not how to do this
                        System.out.println("You did not order any services for this stay.");
                    }
                    break;
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
        System.out.println("\n Thank you for your reviews! We greatly appreciate any and all feedback.");

    }

    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private static void goodbye() {
        clearConsole();
        System.out.println("Thank you for using the Hulton Reservation and Review app.");
        System.out.println("Goodbye!");
    }
}

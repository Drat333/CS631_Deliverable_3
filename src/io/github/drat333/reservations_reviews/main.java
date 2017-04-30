package io.github.drat333.reservations_reviews;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Adrian on 4/28/2017.
 * NOTES: methods like login() return booleans; true = exit, false = continue operations
 */
public class main {

    public static MySQLAccess sql;

    //user variables
    public static boolean loggedIn;
    public static String username;
    public static String userDisplayName;

    //variables for Scanner
    public static Scanner scanner;
    public static String resp;      //user response







    public static void main(String[] args) {

        sql = new MySQLAccess();    //initialize connection to MySQL server
        if (!sql.connected()){
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
            System.out.println("\n\n\n\n\nWelcome to the Hulton Reservation and Reviews app, " + userDisplayName + "!");
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
                    username = null;
                    userDisplayName = null;
                    loggedIn = false;
                    main(null);     //log out then return to main menu
                case "0":
                    goodbye();
                    return;
            }
        }

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
                userDisplayName = "admin";  //SQL statement: get user's real name
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



        //search for a hotel
        hotels: while (true) {
            clearConsole();
            System.out.println("======Hulton Hotel Search======");
            System.out.println("Type 'exit' to return to the main menu at any time.");

            System.out.println("\nEnter a country:");
            country = scanner.nextLine();
            if (country.equalsIgnoreCase("exit")) {
                return;
            }
            //SQL statement to confirm hotels exist in that country

            System.out.println("Enter a state in " + country + ":");
            state = scanner.nextLine();
            if (state.equalsIgnoreCase("exit")) {
                return;
            }

            rs = sql.runStatement("temp"); //SQL statement to confirm hotels exist in that state

            //TODO: check if there are hotels available from input
            while (true) {
                System.out.println("Which hotel in " + state + ", " + country + " would you like to view?");
                int i = 1;
                try {
                    //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
                    rs.beforeFirst();
                    while (rs.next()) {
                        System.out.println(Integer.toString(i) + " | " +
                                rs.getString("Street"));
                        i++;
                    }
                } catch (java.sql.SQLException e) {
                    System.err.println(e);
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

                try {
                    rs.beforeFirst();
                    while (i != 0) {
                        rs.next();
                        i--;
                    }
                    hotelID = rs.getString("HotelID");
                    System.out.println("\nYou have chosen the hotel at " + rs.getString("Street") + " in " + state + ", " + country + ".");
                } catch (java.sql.SQLException e) {
                    System.err.println(e);
                    continue;
                    //I don't know what this error would mean
                }
                break;
            }

            //hotel has been chosen at this point
            scanner:
            while (true) {

                System.out.println("Would you like to create a reservation? (Y/N)");
                resp = scanner.nextLine();

                switch(resp.toUpperCase()){
                    case "Y":
                        break hotels;
                    case "N":
                        continue hotels;
                    case "EXIT":
                        return;
                    default:
                        System.out.println("\nPlease enter Y or N to create a reservation, or 'exit' to return to the main menu.\n");
                }
            }
        }

        //pick a room type
        rs = sql.runStatement("temp"); // sql statement that gets the room types available

        roomTypes:
        while (true) {
            System.out.println("\nWhat type of room would you like to reserve?");
            int i = 1;
            try {
                //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
                rs.beforeFirst();
                while (rs.next()) {
                    System.out.println(Integer.toString(i) + " | " + rs.getString("Rtype"));
                    i++;
                }
            } catch (java.sql.SQLException e) {
                System.err.println(e);
                continue;
                //I don't know what this error would mean
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
            try {
                rs.beforeFirst();
                while (i != 0) {
                    rs.next();
                    i--;
                }
                Rtype = rs.getString("Rtype");
            } catch (java.sql.SQLException e) {
                System.err.println(e);
                //I don't know what this error would mean
            }
            break;
        }





        //start making a reservation - pick a room
        rs = sql.runStatement("temp"); // sql statement that gets all AVAILABLE rooms, using Rtype
        rooms:
        while (true) {
            System.out.println("\nWhich room would you like to reserve?");
            int i = 1;
            try {
                //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
                rs.beforeFirst();
                while (rs.next()) {
                    System.out.print(Integer.toString(i) + " | " +
                            rs.getString("Price") + " per day," +
                            rs.getString("Capacity") + " people, Floor " +
                            rs.getString("Floor"));
                    if (rs.getString("Discount") != null) {
                        System.out.print("\tDiscounted! " );
                        int discount = Integer.parseInt(rs.getString("Discounted"));
                        discount *= 100;
                        System.out.print(Integer.toString(discount) + "% off");
                    }
                    System.out.println("\n\tDescription: " + rs.getString("Description"));
                    i++;
                }
            } catch (java.sql.SQLException e) {
                System.err.println(e);
                continue;
                //I don't know what this error would mean
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
            try {
                rs.beforeFirst();
                while (i != 0) {
                    rs.next();
                    i--;
                }
                roomID = rs.getString("RoomNo");
            } catch (java.sql.SQLException e) {
                System.err.println(e);
                //I don't know what this error would mean
            }
            break;
        }



        //pick reservation dates
        dates:
        while (true) {
            System.out.println("Enter your desired start date for your stay: ");
            String startDate = scanner.nextLine();
            if (startDate.equalsIgnoreCase("exit")) {
                return;
            }

            System.out.println("Enter your desired end date for your stay:");
            String endDate = scanner.nextLine();
            if (endDate.equalsIgnoreCase("exit")) {
                return;
            }

            //SQL statement checks if date is available

            if (startDate == endDate /*placeholder, dates available*/) {
                System.out.println("\nCongrats! Those dates are available.");
                break;
            } else {
                System.out.println("We're sorry, those dates are unavailable.");
            }
        }



        //pick breakfasts
        rs = sql.runStatement("temp"); //sql statement that gets all breakfasts available
        ArrayList<String> BType = new ArrayList<>();
        ArrayList<Integer> BCount = new ArrayList<>();

        breakfasts:
        while (true) {
            try {
                rs.beforeFirst();
                rs.next();
                if (!rs.wasNull()) {
                    System.out.println("\n\nBreakfasts are available for your reservation.");
                    System.out.println("Please indicate how many of each breakfast you would like (1 per day, per person).");

                    rs.beforeFirst();
                    while (rs.next()) {   //if breakfasts are available
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
                            break;  //make em do it again
                        }

                        BCount.add(selection);
                    }
                }
            } catch (java.sql.SQLException e) {
                System.err.println(e);
            }

            break;
        }



        //pick services
        rs = sql.runStatement("temp");   //sql query that gets the services available
        ArrayList<String> SType = new ArrayList();
        services:
        while (true) {
            try {
                rs.beforeFirst();
                rs.next();
                if (!rs.wasNull()) {
                    System.out.println("\n\nServices are available for your reservation.");
                    System.out.println("Please indicate (Y/N) for each service.");

                    rs.beforeFirst();
                    while (rs.next()) {   //if services are available
                        System.out.print(rs.getString("SType") + " ($" + rs.getString("SPrice") + "): ");

                        resp = scanner.nextLine();
                        switch(resp.toUpperCase()) {
                            case "Y":
                                SType.add(rs.getString("SType"));
                                break;
                            case "N":
                                break;
                            case "EXIT":
                                return;
                            default:
                                System.out.println("\nPlease indicate (Y/N) for if you want the service, or exit to return to the main menu.\n");
                                break;
                        }
                    }
                }
            } catch (java.sql.SQLException e){
                System.err.println(e);
                continue services;
            }
            break;
        }

        int TotalPrice; //TODO: this is probably optional but could be nice to implement. requires sql query
        System.out.println("\nAll aspects of your visit are set!");

        confirmation:
        while (true) {
            System.out.println("Would you like to confirm your reservation now? (Y/N)"); //total price ~would~ go here

            resp = scanner.nextLine();

            switch(resp.toUpperCase()) {
                case "Y":
                    rs = sql.runStatement("insert");  //insert all values into sql tables
                    System.out.println("Congratulations! You have successfully reserved your visit.");  //TODO: perhaps add more info here?
                    break;
                case "N":
                    break;
                case "EXIT":
                    return;
            }
        }

    }








    private static void leaveReviews(){
        // TODO: 4/30/2017 Add in SQL statements and test
        ResultSet rs = sql.runStatement("temp"); //sql statement that gets the list of reservations the user has made

        try {
            rs.beforeFirst();
            rs.next();
            if (rs.wasNull()) {
                System.out.println("You don't have any reservations to review currently.");
                System.out.println("Returning to the main menu.\n");
                return;
            }

        } catch (java.sql.SQLException e) {
            System.err.println(e);
            return;
        }

        System.out.println("======Hulton Review App======");
        //pick a stay
        String InvoiceNo;
        while (true) {
            System.out.println("Please pick a stay that you would like to review:");

            int i = 1;
            try {
                rs.beforeFirst();
                while (rs.next()) {
                    System.out.println(Integer.toString(i) + " | " +
                            rs.getString("State") + ", " +
                            rs.getString("Country") + " - " +
                            rs.getString("CheckInDate"));
                    i++;
                }
            } catch (java.sql.SQLException e) {
                System.err.println(e);
                continue;
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

            try {
                rs.beforeFirst();
                while (i != 0) {
                    rs.next();
                    i--;
                }
                InvoiceNo = rs.getString("InvoiceNo");
            } catch (java.sql.SQLException e) {
                System.err.println(e);
                continue;
                //I don't know what this error would mean
            }
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
            if (resp.equalsIgnoreCase("exit")){
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
        switch (selection){
            case 1:
                //room
                System.out.println("Write your review of your room (limit 500 characters):");
                review = scanner.nextLine();
                rs = sql.runStatement("insert"); //sql statement that inserts review
                break;
            case 2:
                //breakfast - reviews are opt-out (with "skip")
                try{
                    rs = sql.runStatement("temp");  //sql statement that gets list of breakfasts ordered
                    while (rs.next()){
                        i++;
                        System.out.println("Write your review for " + rs.getString("BType") + " (limit 500 characters), or type 'skip':");
                        review = scanner.nextLine();
                        if (review.equalsIgnoreCase("skip")){
                            continue;
                        }
                        sql.runStatement("insert");  //sql statement that inserts review
                    }
                } catch (java.sql.SQLException e){
                    System.err.println(e);
                }
                if (i == 0){
                    System.out.println("You did not order any breakfasts for this stay.");
                }
                break;
            case 3:
                //services - reviews are opt-out (with "skip")
                try{
                    rs = sql.runStatement("temp");  //sql statement that gets list of services ordered
                    while (rs.next()){
                        i++;
                        System.out.println("Write your review for " + rs.getString("SType") + " (limit 500 characters), or type 'skip':");
                        review = scanner.nextLine();
                        if (review.equalsIgnoreCase("skip")){
                            continue;
                        }
                        sql.runStatement("insert");  //sql statement that inserts review
                    }
                } catch (java.sql.SQLException e){
                    System.err.println(e);
                }
                if (i == 0){
                    System.out.println("You did not order any services for this stay.");
                }
                break;
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

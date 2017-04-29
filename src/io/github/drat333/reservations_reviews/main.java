package io.github.drat333.reservations_reviews;

import java.sql.ResultSet;
import java.util.Scanner;

/**
 * Created by Adrian on 4/28/2017.
 * NOTES: methods like login() return booleans; true = exit, false = continue operations
 */
public class main {

    //user variables
    public static boolean loggedIn;
    public static String username;
    public static String userDisplayName;

    //variables for Scanner
    public static Scanner scanner;
    public static String resp;      //user response

    public static void main(String[] args) {

        MySQLAccess sql = new MySQLAccess();    //initialize connection to MySQL server
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
            System.out.println("2 | Find discounts for an existing reservation");
            System.out.println("3 | Leave a review");
            System.out.println("4 | Logout");
            System.out.println("  |");
            System.out.println("0 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    if (hotelSearch()) {
                        goodbye();
                        return;
                    }
                    break;
                case "2":
                    if (findDiscounts()) {
                        goodbye();
                        return;
                    }
                    break;
                case "3":
                    if (leaveReviews()) {
                        goodbye();
                        return;
                    }
                case "4":
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

            if (email.compareTo("admin") != 0 || pass.compareTo("admin") != 0) {        //SQL statement to check user credentials
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


    private static boolean hotelSearch(){
        // TODO: 4/28/2017 Implement (in separate class, for organization?). Fill in commented areas


        String country;
        String state;
        String hotelID; //make an int?
        String hotelName;

        //search for a hotel
        while (true) {
            clearConsole();
            System.out.println("======Hulton Hotel Search======");
            System.out.println("Type 'exit' to return to the main menu at any time.");

            System.out.println("\nEnter a country:");
            country = scanner.nextLine();
            if (country.compareToIgnoreCase("exit") == 0) {
                return false;
            }
            //SQL statement to confirm hotels exist in that country

            System.out.println("Enter a state in " + country + ":");
            state = scanner.nextLine();
            if (state.compareToIgnoreCase("exit") == 0) {
                return false;
            }

            //SQL statement to confirm hotels exist in that state

            //if hotels > 1, list and ask which hotel
            if (false /*placeholder*/) {
                System.out.println("Which hotel?");
                resp = scanner.nextLine();
                if (resp.compareToIgnoreCase("exit") == 0) {
                    return false;
                }
            }

            //assign hotelID
            System.out.println("You have chosen " + hotelName);
            System.out.println("Would you like create a reservation? (Y/N)");
            resp = scanner.nextLine();

            if (resp.compareToIgnoreCase("y") == 0) {
                break;
            }
        }


        //start making a reservation - pick a room
        ResultSet rs; //= sql statement that gets all AVAILABLE rooms
        while (true) {
            System.out.println("Which room would you like to reserve?");
            int i = 1;
            try {
                rs.beforeFirst();
                while (rs.next()) {
                    //print room information nicely. not explicitly like below, this is a loop
                    System.out.println("1 | A very nice room");
                    System.out.println("2 | Slightly less nice room");
                    System.out.println("3 | 'I mean I guess, fine' room");
                    i++;
                }
            } catch (java.sql.SQLException e){
                System.err.println(e);
                //I don't know what this error would mean
            }
            //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
            resp = scanner.nextLine();
            if (resp.compareToIgnoreCase("exit") == 0) {
                return false;
            }

            //try to convert the input into an int
            int selection;
            try {
                selection = Integer.parseInt(resp);
                if (selection < 1 || selection > i){
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                continue;  //make em do it again
            }

            String roomID;
            try {
                rs.beforeFirst();
                while (i !=0){
                    rs.next();
                    i--;
                }
                roomID = rs.getString("RoomNo");
            } catch (java.sql.SQLException e){
                System.err.println(e);
                //I don't know what this error would mean
            }
            break;
        }


        //pick reservation dates
        while (true) {
            System.out.println("Enter your desired start date for your stay: ");
            String startDate = scanner.nextLine();
            if (startDate.compareToIgnoreCase("exit") == 0) {
                return false;
            }

            System.out.println("Enter your desired end date for your stay:");
            String endDate = scanner.nextLine();
            if (endDate.compareToIgnoreCase("exit") == 0) {
                return false;
            }

            //SQL statement checks if date is available

            if (true /*placeholder, dates unavailable*/){
                System.out.println("\nCongrats! Those dates are available.");
                break;
            } else {
                System.out.println("We're sorry, those dates are unavailable.");
            }
        }

        //pick breakfasts
        //rs = sql statement that gets all breakfasts available
        while (true){   //if breakfasts are available
            System.out.println("\n\nBreakfasts are available for your reservation.");
            System.out.println("Please indicate how many of each breakfast you would like (1 per day, per person).");

            // TODO: 4/28/2017 put this in an array of some sort
            String bType;
            try{
                rs.beforeFirst();
                while (rs.next()){
                    System.out.println(bType + " breakfast: ");
                    

                }
            }


        }

        return false;
    }

    private static boolean findDiscounts(){
        // TODO: 4/28/2017 Implement (in separate class, for organization?)
        return false;
    }

    private static boolean leaveReviews(){
        // TODO: 4/28/2017 Implement (in separate class, for organization?)
        return false;
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

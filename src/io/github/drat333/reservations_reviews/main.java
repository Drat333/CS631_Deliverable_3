package io.github.drat333.reservations_reviews;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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


    //hotelSearch values
    private static int hotelID; //make an int?
    private static Date checkinDate;
    private static Date checkoutDate;
    private static String Rtype;
    private static int roomID;


    //credit card info
    private static String CNumber;
    private static String Ctype;
    private static String Baddress;
    private static String CCode;
    private static String ExpDate;
    private static String CName;


    public static void main(String[] args) {

        if (!connect()){            //initialize connection to MySQL server
            return;
        }

        scanner = new Scanner(System.in);
        clearConsole();

        ///////////////////////
        //Customer login prompt
        ///////////////////////
        while (true) {

            System.out.println("\n\n\nWelcome to the Hulton Reservation and Reviews app!");
            System.out.println("1 | Login");
            System.out.println("0 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    System.out.println(resp);
                    if (!login()){
                        continue;
                    }
                    break;
                case "0":
                    goodbye();
                    return;
                default:
                    System.out.println("\nInvalid response!\n");
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



    private static boolean login() {
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







    private static void hotelSearch() {
        // TODO: 4/28/2017 Fill in SQL statements and test.
        // this method should not make any table changes until the end, when the customer confirms their choices
        
        
        clearConsole();
        System.out.println("======Hulton Hotel Search======");
        System.out.println("Type 'exit' to return to the main menu at any time.");

        
        if (!pickHotel()) return;
        if (!pickRoom()) return;
        if (!pickBreakfasts()) return;
        if (!pickServices()) return;


        int TotalPrice; //TODO: this is probably optional but could be nice to implement. requires sql query
        System.out.println("\nAll aspects of your visit are set!");

        while (true) {
            System.out.println("Would you like to confirm your reservation now? (Y/N)"); //total price ~would~ go here

            resp = scanner.nextLine();

            switch (resp.toUpperCase()) {
                case "Y":
                    break;
                case "N":
                    // TODO: 5/3/2017 call hotelSearch() again?
                    return;
                case "EXIT":
                    return;
                default:
                    System.out.println("Please indicate whether you would like to reserve this room (Y/N), or type 'exit' to return to the main menu.");
                    continue;
            }
            break;
        }


        if (!enterCardInfo()) return;


        try {
            statement = connection.createStatement();

            query = null;  //insert credit card values
            rs = statement.executeQuery(query);

            // TODO: 5/3/2017 loop for more room reservations
            query = null;  //insert hotel values
            rs = statement.executeQuery(query);



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




    private static boolean pickHotel(){
        //search for a hotel

        String country;
        String state;

        try {
            pickHotel:
            while (true) {

                //pick a country
                // TODO: 5/3/2017 call hotelSearch instead of looping?
                while (true) {

                    System.out.println("\nPick a country:");

                    query = "SELECT DISTINCT Country " +
                            "FROM HOTEL;"; //get a list of countries with hotels

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
                        return false;
                    }

                    if (!isNumeric(resp)) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;
                    }

                    int selection = Integer.parseInt(resp);
                    if (selection < 1 || selection > i) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;
                    }

                    rs.beforeFirst();
                    while (selection != 0) {
                        rs.next();
                        selection--;
                    }

                    country = rs.getString("Country");
                    break;
                }

                //country is picked
                if (statement != null) {
                    statement.close();
                }



                //pick a state
                while (true) {

                    System.out.println("\nPick a state in " + country + ":");

                    query = "SELECT DISTINCT State " +
                            "FROM HOTEL " +
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
                        return false;
                    }

                    if (!isNumeric(resp)) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;
                    }

                    int selection = Integer.parseInt(resp);
                    if (selection < 1 || selection > i) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;
                    }


                    rs.beforeFirst();
                    while (selection != 0) {
                        rs.next();
                        selection--;
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
                        return false;
                    }

                    if (!isNumeric(resp)) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;
                    }

                    int selection = Integer.parseInt(resp);
                    if (selection < 1 || selection > i) {
                        System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                        continue;
                    }


                    rs.beforeFirst();
                    while (selection != 0) {
                        rs.next();
                        selection--;
                    }
                    hotelID = rs.getInt("HotelID");
                    System.out.println("\nYou have chosen the hotel at " + rs.getString("Street") + " in " + state + ", " + country + ".");

                    break;
                }


                if (statement != null) {
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
                        return false;
                    default:
                        System.out.println("\nPlease enter Y or N to create a reservation, or 'exit' to return to the main menu.\n");
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

        return true;
    }



    private static boolean pickRoom(){
        try {
            //pick a room type
            query = "SELECT Rtype " +
                    "FROM ROOM " +
                    "WHERE HotelID='"+ hotelID + "';"; // sql statement that gets the room types available

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
                    return false;
                }

                if (!isNumeric(resp)){
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }

                int selection = Integer.parseInt(resp);
                if (selection < 1 || selection > i) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }

                rs.beforeFirst();
                while (selection != 0) {
                    rs.next();
                    selection--;
                }
                Rtype = rs.getString("Rtype");

                break;
            }

            if (statement != null){
                statement.close();
            }



            //pick reservation dates
            dates:
            while (true) {
                // TODO: 5/3/2017 unskip
                break;
                /*
                DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy").toFormatter();
                LocalDate localDate;

                System.out.println("Enter your desired check-in date for your stay (MM/dd/yyyy): ");
                String resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("exit")) {
                    return false;
                }

                localDate = LocalDate.parse(resp,formatter);
                checkinDate = Date.valueOf(localDate);

                System.out.println("Enter your desired check-out date for your stay (MM/dd/yyyy):");
                String endDate = scanner.nextLine();
                if (endDate.equalsIgnoreCase("exit")) {
                    return false;
                }

                localDate = LocalDate.parse(resp,formatter);
                checkoutDate = Date.valueOf(localDate);
                query = "SELECT * " +
                        "FROM ROOM, ROOM_RESERVATION " +
                        "WHERE (ROOM.HotelID != ROOM_RESERVATION.HotelID AND " +
                                "ROOM.RoomNo != ROOM_RESERVATION.RoomNo AND " +
                                "ROOM.HotelID=" + hotelID + " AND " +
                                "Rtype='" + Rtype + "' AND " +
                                "CheckInDate!='" + checkinDate + "' AND " +
                                "CheckOutDate!='" + checkoutDate + "');";//SQL statement checks if date is available

                System.out.println(query);
                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                if (rs.next()) {
                    System.out.println("\nCongrats! Those dates are available.");
                    break;
                } else {
                    System.out.println("We're sorry, those dates are unavailable.");
                }*/

            }

            if (statement != null){
                statement.close();  // FIXME: 5/3/2017 can we get rid of this?
            }



            //start making a reservation - pick a room

            query = "SELECT * " +
                    "FROM ROOM " +
                    "WHERE HotelID='" + hotelID +"';";    // FIXME: 5/3/2017 temp query



            statement = connection.createStatement();
            rs = statement.executeQuery(query); //query probably doesn't have to change here

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
                    return false;
                }

                if (!isNumeric(resp)){
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }

                int selection = Integer.parseInt(resp);
                if (selection < 1 || selection > i) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }

                rs.beforeFirst();
                while (selection != 0) {
                    rs.next();
                    selection--;
                }
                roomID = rs.getInt("RoomNo");
                break;
            }

            if (statement != null){
                statement.close();
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

        return true;
    }



    private static boolean pickBreakfasts() {
        try{
            //pick breakfasts
            query = null; //sql statement that gets all breakfasts available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            ArrayList<String> BType = new ArrayList<>();
            ArrayList<Integer> BCount = new ArrayList<>();

            if (rs.next()) {
                System.out.println("\n\nBreakfasts are available for your reservation.");
                System.out.println("Please indicate how many of each breakfast you would like (1 per day, per person).");

                rs.beforeFirst();
                while (rs.next()) {   //if breakfasts are available
                    while (true) {
                        BType.add(rs.getString("BType"));
                        System.out.print(rs.getString("BType") + " ($ " + rs.getString("BPrice") + "/order): ");

                        resp = scanner.nextLine();
                        if (resp.equalsIgnoreCase("exit")) {
                            return false;
                        }

                        if (!isNumeric(resp)){
                            System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                            continue;
                        }

                        int selection = Integer.parseInt(resp);
                        if (selection < 0) {
                            System.out.println("\nPlease enter a valid number of at least 0, or type 'exit' to return to main menu.\n");
                            continue;
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

        return true;
    }


    private static boolean pickServices() {
        try{
            //pick services
            query = null;   //sql query that gets the services available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            ArrayList<String> SType = new ArrayList();
            services:
            while (true) {

                if (rs.next()) {
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
                                    return false;
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

        return true;
    }


    private static boolean enterCardInfo(){
        System.out.println("\nIn order to reserve your room, we require that you enter a credit card.");

        //credit card info
        CNumber = null;
        Ctype = null;
        Baddress = null;
        CCode = null;
        ExpDate = null;
        CName = null;

        while (true) {
            System.out.println("Enter your credit card number:");
            CNumber = scanner.nextLine();

            if (CNumber.equalsIgnoreCase("exit")){
                return false;
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
        if (Ctype.equalsIgnoreCase("exit")){
            return false;
        }

        System.out.println("Enter the name on your credit card:");
        CName = scanner.nextLine();
        if (CName.equalsIgnoreCase("exit")){
            return false;
        }

        // TODO: 5/3/2017 check for length
        System.out.println("Enter your billing address:");
        Baddress = scanner.nextLine();
        if (Baddress.equalsIgnoreCase("exit")){
            return false;
        }

        while (true) {
            System.out.println("Enter your credit card security code:");
            CCode = scanner.nextLine();

            if (CCode.equalsIgnoreCase("exit")){
                return false;
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
            if (ExpDate.equalsIgnoreCase("exit")){
                return false;
            }
            if (isNumeric(ExpDate) && ExpDate.length() == 4){
                break;
            }
            System.out.println("\nInvalid year format.");
        }

        return true;
    }


    private static void leaveReviews(){
        // TODO: 4/30/2017 Add in SQL statements and test
        try {
            query = null; //sql statement that gets the list of reservations the user has made

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            if (!rs.next()) {
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

                if (!isNumeric(resp)){
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }

                int selection = Integer.parseInt(resp);
                if (selection < 1 || selection > i) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }


                rs.beforeFirst();
                while (selection != 0) {
                    rs.next();
                    selection--;
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

                if (!isNumeric(resp)){
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }

                selection = Integer.parseInt(resp);
                if (selection < 1 || selection > 3) {
                    System.out.println("\nPlease enter a valid number, or type 'exit' to return to main menu.\n");
                    continue;
                }
                break;
            }

            String review;
            int i = 0;
            // TODO: 4/30/2017 should we cancel the review if the user only types "exit"? very minor detail
            // TODO: 5/3/2017 account for multiple room_reserves per reserve
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

                    if (rs.next()){
                        rs.beforeFirst();
                        while (rs.next()) {
                            i++;
                            System.out.println("Write your review for " + rs.getString("BType") + " (limit 500 characters), or type 'skip':");
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
                    } else {
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

    private static boolean isNumeric(String str)
    {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static void goodbye() {
        //clearConsole();
        System.out.println("\n\n\n\n\nThank you for using the Hulton Reservation and Review app.");
        System.out.println("Goodbye!");
    }
}

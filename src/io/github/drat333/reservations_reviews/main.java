package io.github.drat333.reservations_reviews;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class main {

    //user variables
    private static boolean loggedIn;
    private static String email;
    private static String displayName;
    private static int customerID;

    //variables for Scanner
    private static Scanner scanner;
    private static String resp;      //user response

    //SQL
    private static Connection connection;
    private static ResultSet rs;
    private static Statement statement;
    private static String query;



    // FIXME: 5/3/2017 assign to null at some point?
    //hotelSearch values
    private static int hotelID;
    private static Date checkinDate;
    private static Date checkoutDate;
    private static String Rtype;
    private static int roomNo;
    private static Date reserveDate;
    private static int totalCost;
    private static int updateCost;
    private static ArrayList<String> BType;
    private static ArrayList<Integer> BCount;
    private static ArrayList<String> SType;


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

            System.out.println("\n\nWelcome to the Hulton Reservation and Reviews app!");
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
                    customerID = -1;
                    main(null);     //log out then return to main menu
                case "0":
                    goodbye();
                    return;
                default:
                    System.out.println("\nInvalid response!\n");
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
        query = ("SELECT CID, Name, Email, Password " +
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
                customerID = rs.getInt("CID");
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
        // this method should not make any table changes until the end, when the customer confirms their choices
        
        clearConsole();
        System.out.println("======Hulton Hotel Search======");
        System.out.println("Type 'exit' to return to the main menu at any time.");

        totalCost = 0;
        updateCost = 0;

        if (!pickHotel()) return;
        if (!pickRoom()) return;
        if (!pickBreakfasts()) return;
        if (!pickServices()) return;

        System.out.println("\n\n\nAll aspects of your visit are set!");

        while (true) {
            System.out.println("Would you like to confirm your reservation now? (Y/N)"); //total price ~would~ go here

            resp = scanner.nextLine();

            switch (resp.toUpperCase()) {
                case "Y":
                    break;
                case "N":
                    hotelSearch();
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

        totalCost += updateCost;
        updateCost = 0;

        try {
            int invoiceNo;
            LocalDate localDate = LocalDate.now();
            reserveDate = Date.valueOf(localDate);


            //see if card already exists
            query = "SELECT * " +
                    "FROM CREDIT_CARD " +
                    "WHERE CNumber='" + CNumber + "' AND Ctype='" + Ctype + "' AND Baddress='" + Baddress + "' AND Code='" + CCode + "' AND ExpDate='" + ExpDate + "' AND Name='" + CName + "';";
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            if (!rs.next()){    //insert into CREDIT_CARD
                if (statement != null) {
                    statement.close();
                }

                query = "INSERT INTO CREDIT_CARD " +
                        "VALUES " +
                        "('" + CNumber + "', '" + Ctype + "', '" + Baddress + "', '" + CCode + "', '" + ExpDate + "', '" + CName + "');";
                statement = connection.createStatement();
                statement.executeUpdate(query);
            }

            if (statement != null) {
                statement.close();
            }



            //insert into RESERVATION
            query = "INSERT INTO RESERVATION " +
                    "(CID, Cnumber, RDate, TotalCost) " +
                    "VALUES " +
                    "(" + customerID + ",'" + CNumber + "','" + reserveDate + "','" + totalCost + "');";
            statement = connection.createStatement();
            invoiceNo = statement.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);

            if (statement != null) {
                statement.close();
            }



            //insert into ROOM_RESERVATION
            query = "INSERT INTO ROOM_RESERVATION " +
                    "VALUES " +
                    "(" + invoiceNo + "," + hotelID + "," + roomNo + ",'" + checkinDate + "','" + checkoutDate + "');";
            System.out.println(query);
            statement = connection.createStatement();
            statement.executeUpdate(query);

            if (statement != null) {
                statement.close();
            }


            //insert into RRESV_BREAKFAST
            for (int i = 0; i < BType.size(); i++) {
                String btype = BType.get(i);
                String bcount = Integer.toString(BCount.get(i));

                query = "INSERT INTO RRESV_BREAKFAST " +
                        "VALUES " +
                        "('" + btype + "'," + hotelID + ",'" + roomNo + "','" + checkinDate + "','" + bcount + "')";
                statement = connection.createStatement();
                statement.executeUpdate(query);

                if (statement != null) {
                    statement.close();
                }
            }



            //insert into RRESV_SERVICE
            for (String stype:
                 SType) {

                query = "INSERT INTO RRESV_SERVICE " +
                        "VALUES " +
                        "('" + stype + "'," + hotelID + ",'" + roomNo + "','" + checkinDate + "')";
                statement = connection.createStatement();
                statement.executeUpdate(query);

                if (statement != null) {
                    statement.close();
                }
            }


            System.out.println("\n\n\nCongratulations! You have successfully created your Hulton Hotels reservation.");
            while (true){
                System.out.println("\nWould you like to add another room to this reservation?  (Y/N)");
                System.out.println("This will add another reservation to your " + Ctype + "-**" + CNumber.substring(CNumber.length() - 4) + ".");
                resp = scanner.nextLine();

                switch (resp.toUpperCase()) {
                    case "Y":
                        if (!pickHotel()) return;
                        if (!pickRoom()) return;
                        if (!pickBreakfasts()) return;
                        if (!pickServices()) return;

                        totalCost += updateCost;
                        updateCost = 0;

                        //insert into ROOM_RESERVATION
                        statement = connection.createStatement();
                        query = "INSERT INTO ROOM_RESERVATION " +
                                "VALUES " +
                                "(" + invoiceNo + "," + hotelID + "," + roomNo + ",'" + checkinDate + "','" + checkoutDate + "');";
                        statement.executeUpdate(query);

                        if (statement != null) {
                            statement.close();
                        }


                        //insert into RRESV_BREAKFAST
                        for (int i = 0; i < BType.size(); i++) {
                            String btype = BType.get(i);
                            String bcount = Integer.toString(BCount.get(i));

                            query = "INSERT INTO RRESV_BREAKFAST " +
                                    "VALUES " +
                                    "('" + btype + "'," + hotelID + ",'" + roomNo + "','" + checkinDate + "','" + bcount + "')";
                            statement = connection.createStatement();
                            statement.executeUpdate(query);

                            if (statement != null) {
                                statement.close();
                            }
                        }


                        //insert into RRESV_SERVICE
                        for (String stype:
                                SType) {

                            query = "INSERT INTO RRESV_SERVICE " +
                                    "VALUES " +
                                    "('" + stype + "'," + hotelID + ",'" + roomNo + "','" + checkinDate + "')";
                            statement = connection.createStatement();
                            statement.executeUpdate(query);

                            if (statement != null) {
                                statement.close();
                            }
                        }

                        System.out.println("\n\n\nCongratulations! You have successfully created your Hulton Hotels reservation.");
                        continue;
                    case "N":
                        break;
                    case "EXIT":
                        break;
                    default:
                        System.out.println("Please indicate if you would like to add another room to your reservation (Y/N)");
                        continue;
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
    }




    private static boolean pickHotel(){
        //search for a hotel

        String country;
        String state;

        try {
            pickHotel:
            while (true) {

                //pick a country
                while (true) {

                    System.out.println("\nPick a country:");

                    query = "SELECT DISTINCT Country " +
                            "FROM HOTEL;"; //get a list of countries with hotels

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    int i = 0;
                    rs.beforeFirst();
                    while (rs.next()) {
                        i++;
                        System.out.println(Integer.toString(i) + " | " +
                                rs.getString("Country"));
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

                    int i = 0;
                    rs.beforeFirst();
                    while (rs.next()) {
                        i++;
                        System.out.println(Integer.toString(i) + " | " +
                                rs.getString("State"));
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

                    int i = 0;
                    rs.beforeFirst();
                    while (rs.next()) {
                        i++;
                        System.out.println(Integer.toString(i) + " | " +
                                rs.getString("Street"));
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
                while (true) {
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
            query = "SELECT DISTINCT Rtype " +
                    "FROM ROOM " +
                    "WHERE HotelID='"+ hotelID + "';"; // sql statement that gets the room types available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            roomTypes:
            while (true) {
                System.out.println("\nWhat type of room would you like to reserve?");

                int i = 0;
                //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
                rs.beforeFirst();
                while (rs.next()) {
                    i++;
                    System.out.println(Integer.toString(i) + " | " + rs.getString("Rtype"));
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

                try {
                    DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy").toFormatter();
                    LocalDate localDate;

                    System.out.println("Enter your desired check-in date for your stay (MM/dd/yyyy): ");
                    resp = scanner.nextLine();
                    if (resp.equalsIgnoreCase("exit")) {
                        return false;
                    }

                    localDate = LocalDate.parse(resp, formatter);
                    checkinDate = Date.valueOf(localDate);

                    System.out.println("Enter your desired check-out date for your stay (MM/dd/yyyy):");
                    resp = scanner.nextLine();
                    if (resp.equalsIgnoreCase("exit")) {
                        return false;
                    }

                    localDate = LocalDate.parse(resp, formatter);
                    checkoutDate = Date.valueOf(localDate);

                    query = "SELECT * " +
                            "FROM ROOM " +
                            "WHERE NOT EXISTS(SELECT HotelID, RoomNo " +
                            "             FROM ROOM_RESERVATION " +
                            "             WHERE HotelID='" + hotelID + "' AND ('" + checkinDate + "' BETWEEN CheckInDate AND CheckOutDate) AND " +
                            "             ('" + checkoutDate + "' BETWEEN CheckInDate AND CheckOutDate));";//SQL statement checks if date is available

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    if (rs.next()) {
                        System.out.println("\nCongrats! Those dates are available.");
                        break;
                    } else {
                        if (statement != null){
                            statement.close();
                        }
                        System.out.println("We're sorry, those dates are unavailable.");
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("\nInvalid date format! Please use the format MM/dd/yyyy, ex. 05/22/1997\n");
                }
            }


            if (statement != null){
                statement.close();
            }


            //start making a reservation - pick a room
            query = "SELECT R.*, Discount\n" +
                    "FROM  (\n" +
                    "    SELECT * \n" +
                    "    FROM ROOM  \n" +
                    "    WHERE NOT EXISTS(SELECT HotelID, RoomNo \n" +
                    "                                FROM ROOM_RESERVATION \n" +
                    "                                WHERE HotelID='14012' AND \n" +
                    "                                ('" + checkinDate + "' BETWEEN CheckInDate AND CheckOutDate) AND \n" +
                    "                                ('" + checkoutDate + "' BETWEEN CheckInDate AND CheckOutDate))) AS R\n" +
                    "LEFT JOIN DISCOUNTED_ROOM AS D ON R.HotelID=D.HotelID AND R.RoomNo=D.RoomNo;";

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            rooms:
            while (true) {
                System.out.println("\nWhich room would you like to reserve?");

                int i = 0;
                //rs is unusable after a while loop; use rs.beforeFirst() to use rs again
                rs.beforeFirst();
                while (rs.next()) {
                    i++;
                    System.out.print(Integer.toString(i) + " | $" +
                            rs.getString("Price") + " per day, " +
                            rs.getString("Capacity") + " people, Floor " +
                            rs.getString("Floor") + " Room " +
                            rs.getString("RoomNo"));
                    if (rs.getString("Discount") != null) {
                        System.out.print("\t\t\t***Discounted!*** ");
                        double discount = Double.parseDouble(rs.getString("Discount"));
                        discount *= 100;
                        System.out.print(Double.toString(discount) + "% off");
                    }
                    System.out.println("\n\tDescription: " + rs.getString("Description"));
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
                roomNo = rs.getInt("RoomNo");
                updateCost += rs.getInt("Price");
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
            query = "SELECT * " +
                    "FROM BREAKFAST " +
                    "WHERE HotelID='" + hotelID + "';"; //sql statement that gets all breakfasts available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            BType = new ArrayList<>();
            BCount = new ArrayList<>();

            if (rs.next()) {
                System.out.println("\n\n\nBreakfasts are available for your reservation.");
                System.out.println("Please indicate how many of each breakfast you would like (1 per day, per person)\n");

                rs.beforeFirst();
                while (rs.next()) {   //if breakfasts are available
                    while (true) {
                        System.out.println();
                        BType.add(rs.getString("BType"));
                        System.out.println(rs.getString("BType") + " ($" + rs.getString("BPrice") + "/order): ");
                        System.out.println(rs.getString("Description"));
                        System.out.print("Number of orders: ");

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
                        updateCost += rs.getInt("BPrice") * selection;
                        break;
                    }
                }
            } else{
                System.out.println("\n\n\nThere are no breakfasts available for this reservation.");
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
            query = "SELECT * " +
                    "FROM SERVICE " +
                    "WHERE HotelID='" + hotelID + "';";   //sql query that gets the services available

            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            SType = new ArrayList();
            services:
            while (true) {

                if (rs.next()) {
                    System.out.println("\n\n\nServices are available for your reservation.");
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
                                    updateCost += rs.getInt("SPrice");
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
                } else{
                    System.out.println("\n\n\nThere are no services available for this reservation.");
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
            query = "SELECT * " +
                    "FROM RESERVATION AS R, CREDIT_CARD AS C " +
                    "WHERE R.Cnumber=C.Cnumber and R.CID=" + customerID + ";";  //sql statement that gets the list of reservations the user has made
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            if (!rs.next()) {
                System.out.println("\n\n\nYou don't have any reservations to review currently.");
                System.out.println("Returning to the main menu.");
                return;
            }


            System.out.println("======Hulton Review App======");


            //pick a reservation
            String InvoiceNo;
            while (true) {
                System.out.println("Please pick a reservation that you would like to review:");

                int i = 0;
                rs.beforeFirst();
                while (rs.next()) {
                    i++;
                    System.out.println(Integer.toString(i) + " | Invoice: " +
                            rs.getString("InvoiceNo") + ",\tCard: " +
                            rs.getString("Ctype") + "-**" +
                            rs.getString("Cnumber").substring(rs.getString("Cnumber").length() - 4) + ",\t Reserve Date: " +
                            rs.getString("RDate"));
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





            //pick a room reservation

            query = "SELECT * " +
                    "FROM RESERVATION AS R, ROOM_RESERVATION AS M, ROOM AS O, HOTEL AS H " +
                    "WHERE R.InvoiceNo=M.InvoiceNo AND M.InvoiceNo='" + InvoiceNo + "' " +
                    "AND M.HotelID=O.HotelID AND M.RoomNo=O.RoomNo AND M.HotelID=H.HotelID;"; //sql statement that gets the list of room reservations the user has made
            statement = connection.createStatement();
            rs = statement.executeQuery(query);


            while (true) {
                System.out.println("Please pick a room reservation that you would like to review:");


                int i = 0;
                rs.beforeFirst();
                while (rs.next()) {
                    i++;
                    System.out.println(Integer.toString(i) + " | " +
                            rs.getString("State") + ", " +
                            rs.getString("Country") + " - " +
                            rs.getString("Rtype") + ", Room #" +
                            rs.getString("RoomNo"));
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
                hotelID = rs.getInt("HotelID");
                roomNo = rs.getInt("RoomNo");
                checkinDate = rs.getDate("CheckInDate");
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
            int rating;
            Statement insertStatement;
            switch (selection) {
                case 1:
                    //room
                    while (true) {
                        System.out.println("On a scale of 1-10, how would you rate your room?");
                        resp = scanner.nextLine();

                        if (!isNumeric(resp)) {
                            System.out.println("\n\nPlease enter a rating between 1 and 10.\n");
                            continue;
                        }

                        rating = Integer.parseInt(resp);

                        if (rating < 1 || rating > 10) {
                            System.out.println("\n\nPlease enter a rating between 1 and 10.\n");
                            continue;
                        }
                        break;
                    }

                    System.out.println("Write your review of your room (limit 500 characters):");
                    review = scanner.nextLine();

                    query = "INSERT INTO ROOM_REVIEW " +
                            "(Rating,Text,CID,HotelID,RoomNo) " +
                            "VALUES " +
                            "('" + rating + "','" + review + "','" + customerID + "','" + hotelID + "','" + roomNo + "');"; //sql statement that inserts review

                    insertStatement = connection.createStatement();
                    insertStatement.executeUpdate(query);
                    resp = null;
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                    break;
                case 2:
                    //breakfast - reviews are opt-out (with "skip")

                    query = "SELECT * " +
                            "FROM BREAKFAST AS B, RRESV_BREAKFAST AS R " +
                            "WHERE B.HotelID=R.HotelID AND B.BType=R.BType AND " +
                            "R.HotelID='" + hotelID + "' AND R.RoomNo='" + roomNo + "' AND R.CheckInDate='" + checkinDate + "';";  //sql statement that gets list of breakfasts ordered

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    if (rs.next()){
                        rs.beforeFirst();
                        while (rs.next()) {
                            while (true) {
                                System.out.println("On a scale of 1-10, how would you rate the " + rs.getString("BType") + " breakfast?");
                                resp = scanner.nextLine();

                                if (!isNumeric(resp)) {
                                    System.out.println("\n\nPlease enter a rating between 1 and 10.\n");
                                    continue;
                                }

                                rating = Integer.parseInt(resp);

                                if (rating < 1 || rating > 10) {
                                    System.out.println("\n\nPlease enter a rating between 1 and 10.\n");
                                    continue;
                                }
                                break;
                            }

                            System.out.println("Write your review for " + rs.getString("BType") + " (limit 500 characters), or type 'skip':");
                            review = scanner.nextLine();
                            if (review.equalsIgnoreCase("skip")) {
                                continue;
                            }


                            query = "INSERT INTO BREAKFAST_REVIEW " +
                                    "(Rating,Text,CID,HotelID,BType) " +
                                    "VALUES " +
                                    "('" + rating + "','" + review + "','" + customerID + "','" + hotelID + "','" + rs.getString("BType") + "');";  //sql statement that inserts review

                            insertStatement = connection.createStatement();
                            insertStatement.executeUpdate(query);
                            resp = null;
                            if (insertStatement != null) {
                                insertStatement.close();
                            }
                        }
                    } else {
                        System.out.println("You did not order any breakfasts for this stay.");
                    }
                    break;
                case 3:
                    //services - reviews are opt-out (with "skip")

                    query = "SELECT * " +
                            "FROM SERVICE AS S, RRESV_SERVICE AS R " +
                            "WHERE S.HotelID=R.HotelID AND S.SType=R.SType AND " +
                            "R.HotelID='" + hotelID + "' AND R.RoomNo='" + roomNo + "' AND R.CheckInDate='" + checkinDate + "';";  //sql statement that gets list of services ordered

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    if (rs.next()) {
                        rs.beforeFirst();
                        while (rs.next()) {
                            while (true) {
                                System.out.println("On a scale of 1-10, how would you rate the " + rs.getString("SType") + " service?");
                                resp = scanner.nextLine();

                                if (!isNumeric(resp)) {
                                    System.out.println("\n\nPlease enter a rating between 1 and 10.\n");
                                    continue;
                                }

                                rating = Integer.parseInt(resp);

                                if (rating < 1 || rating > 10) {
                                    System.out.println("\n\nPlease enter a rating between 1 and 10.\n");
                                    continue;
                                }
                                break;
                            }
                            System.out.println("Write your review for " + rs.getString("SType") + " (limit 500 characters), or type 'skip':");
                            review = scanner.nextLine();
                            if (review.equalsIgnoreCase("skip")) {
                                continue;
                            }


                            query = "INSERT INTO SERVICE_REVIEW " +
                                    "(Rating,Text,CID,HotelID,SType) " +
                                    "VALUES " +
                                    "('" + rating + "','" + review + "','" + customerID + "','" + hotelID + "','" + rs.getString("SType") + "');";  //sql statement that inserts review

                            insertStatement = connection.createStatement();
                            insertStatement.executeUpdate(query);
                            resp = null;
                            if (insertStatement != null) {
                                insertStatement.close();
                            }

                        }
                    } else{
                        System.out.println("You did not order any services for this stay.");
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    if (statement != null) {
                        statement.close();
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
        System.out.println("\nThank you for your reviews! We greatly appreciate any and all feedback.");

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

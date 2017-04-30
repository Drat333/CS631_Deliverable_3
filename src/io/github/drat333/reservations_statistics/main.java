package io.github.drat333.reservations_statistics;

import java.util.Scanner;

/**
 * Created by Adrian on 4/28/2017.
 *
 */
public class main {

    public static MySQLAccess sql;

    //user variables
    public static boolean loggedIn;
    public static String userName;
    public static String userDisplayName;

    //variables for Scanner
    public static Scanner scanner;
    public static String resp;      //user response




    public static void main(String[] args) throws Exception {

        MySQLAccess sql = new MySQLAccess();    //initialize connection to MySQL server
        clearConsole();
        if(login()){    //returns true, then exit
            goodbye();
            return;
        }

        //compute statistics
        //date ranges are month/year - month/year, ignoring days
        String startMonth;
        String startYear;
        String endMonth;
        String endYear;

        while (true) {
            System.out.println("====Enter a start date====");
            System.out.print("Start month:"); startMonth = scanner.nextLine();
            System.out.print("Start year:"); startYear = scanner.nextLine();
            System.out.print("End month:"); endMonth = scanner.nextLine();
            System.out.print("End year:"); endYear = scanner.nextLine();

            // TODO: 4/30/2017 check/sanitize input
            // TODO: 4/30/2017 computed highest rated room type per hotel
            // TODO: 4/30/2017 compute 5 best customers, in terms of money spent in reservations
            // TODO: 4/30/2017 compute highest rated breakfast type across all hotels
            // TODO: 4/30/2017 compute highest rated service type across all hotels

        }


    }


    private static boolean login(){
        //clearConsole();
        while (true) {
            System.out.println("\n\n\nWelcome to the Hulton Reservation Statistics app!");

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
                userName = "admin";
                loggedIn = true;
                return false;
            }
        }
    }


    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private static void goodbye() {
        clearConsole();
        System.out.println("Thank you for using the Hulton Reservation Statistics app.");
        System.out.println("Goodbye!");
    }
}

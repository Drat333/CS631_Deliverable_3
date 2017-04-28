package io.github.drat333.reservations_reviews;

import java.util.Scanner;

/**
 * Created by Adrian on 4/28/2017.
 * NOTES: methods like login() return booleans; true = exit, false = continue operations
 */
public class main {

    //user variables
    public static boolean loggedIn;
    public static String username;

    //variables for Scanner
    public static Scanner scanner;
    public static String resp;      //user response

    public static void main(String[] args) {

        MySQLAccess sql = new MySQLAccess();    //initialize connection to MySQL server
        clearConsole();

        ///////////////////////
        //Customer login prompt
        ///////////////////////
        while (true) {
            scanner = new Scanner(System.in);

            System.out.println("\n\n\n\n\nWelcome to the Hulton Reservation and Reviews app!");
            System.out.println("1 | Login");
            System.out.println("0 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    System.out.println(resp);
                    if (login()){
                        return;
                    }
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid response!");
            }
            if (loggedIn){
                break;
            }
        }

        System.out.println("\n\n\n\n\nWelcome to the Hulton Reservation and Reviews app!");
        System.out.println("1 | Search hotels and make a reservation");
        System.out.println("2 | Find discounts for an existing reservation");
        System.out.println("3 | Logout");
        System.out.println("  |");
        System.out.println("0 | Exit");
        resp = scanner.nextLine();

        switch (resp){
            case "1":
                if (hotelSearch()){
                    return;
                }
                break;
            case "2":
                break;
            case "3":
                loggedIn = false;
                main(null);     //log out then return to main menu
            case "0":
                return true;
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
                        return true;
                    default:
                        System.out.println("Invalid response!");
                }
            } else{
                loggedIn = true;
                return false;
            }
        }
    }


    private static boolean hotelSearch(){

    }

    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}

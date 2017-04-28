package io.github.drat333.reservations_statistics;

import java.util.Scanner;

/**
 * Created by Adrian on 4/28/2017.
 */
public class main {

    public static void main(String[] args) throws Exception {

        MySQLAccess sql = new MySQLAccess();    //initialize connection to MySQL server
        clearConsole();

        ///////////////////////
        //Customer login prompt
        ///////////////////////
        while (true) {
            String resp;    //user response
            Scanner scanner = new Scanner(System.in);

            System.out.println("\n\n\n\n\nWelcome to the Hulton Reservation Statistics app!");
            System.out.println("1 | Login to existing account");
            System.out.println("2 | Register a new account");
            System.out.println("3 | Exit");
            resp = scanner.nextLine();

            switch (resp) {
                case "1":
                    System.out.println(resp);
                    login(scanner);
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



    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}

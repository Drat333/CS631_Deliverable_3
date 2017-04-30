package io.github.drat333.customerregistration;

import java.sql.*;
import java.util.Scanner;

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

            System.out.println("\n\n\n\n\nWelcome to the Hulton Customer Registration app!");
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

    private static void login(Scanner scanner){
        //clearConsole();
        scanner.reset();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Password: ");
        String pass = scanner.nextLine();
        System.out.println(email + " " + pass);
 
        if (email.equals("admin") || pass.equals("admin")){        //SQL statement to check user credentials
            System.out.println("Success!");
        }

        //account management

    }

    private static void register(Scanner scanner){
        //clearConsole();
        System.out.print("Enter a user name: ");
        String user = scanner.nextLine();
        //SQL query checks for existing userName
        System.out.print("Enter a password: ");
        String pass = scanner.nextLine();
        System.out.print("Confirm your password: ");
        String confirmPass = scanner.nextLine();
        if (pass == confirmPass){
            //insert password into db
        }

        //basic account setup
    }


    private static void clearConsole(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

}

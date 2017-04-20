package io.github.drat333.customerregistration;

/**
 * Created by Adrian on 4/18/2017.
 */

import java.sql.*;

public class main {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://sql2.njit.edu:3306/az62";
        String username = "az62";
        String password = "8wlgKd19A";
        String dbName = "az62";

        Connection connection;

        //Connect to database
        System.out.println("Connecting to MySQL server...");
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e){
            System.err.println("Failed to connect to MySQL server, exiting.");
            return;
        }

        //Sample SQL query
        Statement statement = null;
        ResultSet rs;
        String query = "SELECT * FROM HOTEL";
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            System.out.println("\n" + query);
            System.out.println("HotelID\t| Street\t| Country\t\t| State\t| Zip");
            while (rs.next()) {
                String HotelID = rs.getString("HotelID");
                String Street = rs.getString("Street");
                String Country = rs.getString("Country");
                String State = rs.getString("State");
                String Zip = rs.getString("Zip");
                System.out.println(HotelID + "\t" + Street + "\t" + Country + "\t\t" + State + "\t" + Zip);
            }

        } catch (SQLException e) {
            System.err.println("Error in SQL query");
        } finally {
            if (statement != null) { statement.close(); }
        }

    }

}

package io.github.drat333.customerregistration;

import java.sql.*;

public class MySQLAccess {

    //MySQL server info
    private String url;
    private String DBUsername;
    private String DBpassword;
    private Connection connection;
    private String dbName;

    public MySQLAccess(){
        url = "jdbc:mysql://sql2.njit.edu:3306/az62";
        DBUsername = "az62";
        DBpassword = "8wlgKd19A";
        dbName = "az62";
        //Connect to database
        System.out.println("Connecting to MySQL server...");
        try {
            connection = DriverManager.getConnection(url, DBUsername, DBpassword);
        } catch (java.sql.SQLException e){
            System.err.println("MySQL server access denied, check your credentials.");
            return;
        }
    }

    public ResultSet runStatement(String query){
        Statement statement = null;
        ResultSet rs = null;

        if (query.isEmpty()){ return rs; }

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

        } catch (SQLException e) {
            System.err.println("Error in SQL query");
        } finally {
            if (statement != null) {
                try{
                    statement.close();
                } catch (SQLException ex){
                    System.err.println("Error in closing SQL query");
                }
            }
        }

        return rs;
    }
    
    public void sampleQuery(){
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
            if (statement != null) {
                try{
                    statement.close();
                } catch (SQLException ex){
                    System.err.println("Error in closing SQL query");
                }
            }
        }
    }

}

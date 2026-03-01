/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DbConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Admin
 */
public class DatabaseConnection {  
    public static Connection dbConnection() {
        String host = "localhost";
        String username = "root";
        String password = "";
        String databaseName = "pos_db";

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + databaseName, username, password);
            return con;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }

    
}

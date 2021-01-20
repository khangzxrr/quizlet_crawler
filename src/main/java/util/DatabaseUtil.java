/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author skyho
 */
public class DatabaseUtil {
    public static final String DATABASE_NAME = "swt301_bank.sqlite";
    
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
    }
    
    public static void closeConnection(Statement statement, ResultSet resultSet) throws SQLException{
        if (resultSet != null) resultSet.close();
        if (statement != null) statement.close();
    }
}

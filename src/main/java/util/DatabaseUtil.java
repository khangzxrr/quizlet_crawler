/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

/**
 *
 * @author skyho
 */
public class DatabaseUtil {
    public static final String DATABASE_NAME = "swt301_bank.sqlite";
    public static final String ORIGINAL_DATABASE_NAME = "original_db.sqlite";
    
    public static void backupOldDatabaseAndCreateNewOne() throws IOException{
        File currentSqlFile = new File(DATABASE_NAME);
        if (currentSqlFile.exists()){
            
            Random random = new Random();
            File newBackupFile = new File(random.nextLong() + "_" + DATABASE_NAME); //37128332_swt301_bank.sqlite
            
            Files.copy(currentSqlFile.toPath(), newBackupFile.toPath());
            
            currentSqlFile.delete(); //delete current database
        }
        
        File originalSqlFile = new File(ORIGINAL_DATABASE_NAME);
        
        Files.copy(originalSqlFile.toPath(), currentSqlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
    }
    
    public static void closeConnection(Statement statement, ResultSet resultSet) throws SQLException{
        if (resultSet != null) resultSet.close();
        if (statement != null) statement.close();
    }
}

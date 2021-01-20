/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dto.Bank;
import dto.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import util.DatabaseUtil;

/**
 *
 * @author skyho
 */
public class QuestionBankManager {
    
    public void addKeys(int bankId, List<Key> keys){
        try{
            Connection connection = DatabaseUtil.getConnection();
            
            
            PreparedStatement statement = connection.prepareStatement("INSERT INTO keys(question,answer,owned_by_bank) VALUES (?, ?, ?)");
            
            for(Key key : keys){
                statement.setString(1, key.getQuestion());
                statement.setString(2, key.getAnswer());    
                statement.setInt(3, bankId);
                
                statement.addBatch();
            }
            
            statement.executeBatch();
            
            DatabaseUtil.closeConnection(statement, null);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void addBank(Bank bank){
        try{
            Connection connection = DatabaseUtil.getConnection();
            
            PreparedStatement statement = connection.prepareStatement("INSERT INTO banks(name, owner) VALUES (?,?)", new String[]{ "id" });
            
            statement.setString(1, bank.getName());
            statement.setString(2, bank.getOwner());
            
            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()){
                bank.setId(generatedKeys.getInt(1)); //push new ID to bank obj, doesn't need to return
            }
            
            DatabaseUtil.closeConnection(statement, generatedKeys);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean isExistQuestion(String question){
        boolean isExist = false;
        
        try{
            Connection connection = DatabaseUtil.getConnection();
            
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM keys WHERE question = ?");
            
            statement.setString(1, question);
            
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()){
                isExist = true;
            }
            
            DatabaseUtil.closeConnection(statement, resultSet);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return isExist;
    }
}

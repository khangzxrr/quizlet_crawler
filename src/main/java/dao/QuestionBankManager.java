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
import java.util.ArrayList;
import java.util.List;
import util.DatabaseUtil;

/**
 *
 * @author skyho
 */
public class QuestionBankManager {
    public static int keyCounter = 0;
    
    public void removeDuplicateKeys() {
        try {
            Connection connection = DatabaseUtil.getConnection();

            PreparedStatement statement = connection.prepareStatement("DELETE FROM keys WHERE id NOT IN (SELECT MIN(id) FROM keys GROUP BY question);");

            int totalUpdated = statement.executeUpdate();
            
            System.out.println("Removed " + totalUpdated + " duplicated keys");

            DatabaseUtil.closeConnection(statement, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addKeys(int bankId, List<Key> keys) {
        try {
            Connection connection = DatabaseUtil.getConnection();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO keys(question,answer,owned_by_bank) VALUES (?, ?, ?)");

            for (Key key : keys) {
                statement.setString(1, key.getQuestion());
                statement.setString(2, key.getAnswer());
                statement.setInt(3, bankId);

                statement.addBatch();
            }

            statement.executeBatch();

            System.out.println(" INSERTED " + keys.size() + " keys to bank " + bankId);
            
            DatabaseUtil.closeConnection(statement, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Key> getKeysOfBank(int bankId){
        List<Key> keys = new ArrayList<>();
        try {
            Connection connection = DatabaseUtil.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM keys WHERE owned_by_bank = ?");
            statement.setInt(1, bankId);

            ResultSet resultSet = statement.executeQuery();
            
            while(resultSet.next()){
                keyCounter++;
                Key key = new Key(keyCounter, resultSet.getString("question"), resultSet.getString("answer"));
                keys.add(key);
            }

            DatabaseUtil.closeConnection(statement, resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return keys;
    }
    public List<Bank> getBanks() {
        List<Bank> banks = new ArrayList<>();
        try {
            Connection connection = DatabaseUtil.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM banks ORDER BY owner, name");

            ResultSet resultSet = statement.executeQuery();
            
            while(resultSet.next()){
                Bank bank = new Bank(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("owner"), resultSet.getString("url"));
                banks.add(bank);
            }

            DatabaseUtil.closeConnection(statement, resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return banks;
    }

    public void addBank(Bank bank) {
        try {
            Connection connection = DatabaseUtil.getConnection();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO banks(name, owner, url) VALUES (?, ?, ?)", new String[]{"id"});

            statement.setString(1, bank.getName());
            statement.setString(2, bank.getOwner());
            statement.setString(3, bank.getSourceUrl());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                bank.setId(generatedKeys.getInt(1)); //push new ID to bank obj, doesn't need to return
            }
            
            System.out.print("INSERTED bank " + bank.getName() + " owner " + bank.getOwner() + "  ");

            DatabaseUtil.closeConnection(statement, generatedKeys);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExistQuestion(String question) {
        boolean isExist = false;

        try {
            Connection connection = DatabaseUtil.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT id FROM keys WHERE question = ?");

            statement.setString(1, question);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                isExist = true;
            }

            DatabaseUtil.closeConnection(statement, resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isExist;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

/**
 *
 * @author skyho
 */
public class Bank {
    int id;
    String name;
    String owner;

    public Bank(String name, String owner){
        this.name = name;
        this.owner = owner;
    }
    
    public Bank(int id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    
}

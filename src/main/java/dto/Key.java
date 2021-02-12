/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import util.ConstantUtil;

/**
 *
 * @author skyho
 */
public class Key {
    int id;
    String question;
    String answer;

    //auto split and sort constructor
    public Key(String rawStr){
        String[] data = rawStr.split(ConstantUtil.SPLIT_BOX);
        
        if (data[0].length() > data[1].length()){
            question = data[0];
            answer = data[1];
        }else{
            question = data[1];
            answer = data[0];
        }
        
    }
    
    public Key(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Key(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "question: " + question + "  answer: " + answer;
    }
    
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    
}

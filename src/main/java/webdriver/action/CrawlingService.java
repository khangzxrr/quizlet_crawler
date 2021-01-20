/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdriver.action;

import dto.Bank;
import dto.Key;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.WebDriverUtil;

/**
 *
 * @author skyho
 */
public class CrawlingService {

    WebDriver driver;

    public CrawlingService(WebDriver driver) {
        this.driver = driver;
    }

    
    
    public Bank getCurrentBank() {
        try {

            By titleDivSelector = By.cssSelector(".SetPage-titleWrapper");
            //title div css selector
            WebElement titleElement = driver.findElement(titleDivSelector);

            By ownerDivSelector = By.cssSelector(".UserLink-username");
            WebElement ownerElement = driver.findElement(ownerDivSelector);

            String name = titleElement.getAttribute("innerText"); //innerText property of title div
            String owner = ownerElement.getAttribute("innerText"); //innerText property of owner div

            Bank bank = new Bank(name, owner);

            return bank;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Key> crawlingKeys(){
        List<Key> keys = new ArrayList<>();
        
        try {
            //quizlet is tricky, so do us. simulating scrolling to force quizlet show all DOM
            JavascriptExecutor jsExecutor = WebDriverUtil.getJSExecutor(driver); //javascript boi!
            long pageHeight = (long)jsExecutor.executeScript("return Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight);");
            //get pageHeight so we can scroll each part, not whole page at once!
            
            for(int currentHeight = 0; currentHeight < pageHeight; currentHeight += pageHeight / 20){ //scrolling...scrolling...
                jsExecutor.executeScript(String.format("window.scrollTo(0,%d);", currentHeight)); //scroll using javascript
                Thread.sleep(200); //sleep...make it human like
            }
            
            By leftSideSelector = By.cssSelector(".SetPageTerm-side.SetPageTerm-smallSide"); //left box selector
            By rightSideSelector = By.cssSelector(".SetPageTerm-side.SetPageTerm-largeSide"); //right box selector
            
            //get 10 (you define this number) first text|text to determine which is question and which is answer
            List<WebElement> leftDivsElements = driver.findElements(leftSideSelector); //left div element
            List<WebElement> rightDivsElements = driver.findElements(rightSideSelector); //right div element
            
            int leftIsAnswerConfident = 0;
            
            String leftText;
            String rightText;
            
            System.out.println("comparing leftbox..rightbox....");
            
            for(int i = 0; i < 10; i++){
                leftText = leftDivsElements.get(i).getAttribute("innerText");
                rightText = rightDivsElements.get(i).getAttribute("innerText");
                
                if (leftText.length() < rightText.length()){ //if left text is shorter than right text
                    //assume it is answer => confident += 1
                    leftIsAnswerConfident++;
                }
            }
            
            //prediction use only 10 elements, it will be much faster than you compare every single time 
            //============================================================
            
            System.out.println("crawling...");
            
            for(int i = 0; i < leftDivsElements.size(); i++){ //left or right divs are equal element count
                
                leftText = leftDivsElements.get(i).getAttribute("innerText");
                rightText = rightDivsElements.get(i).getAttribute("innerText");
                
                Key key;
                if (leftIsAnswerConfident >= 5){
                    //if left is answer
                    key = new Key(rightText,leftText);
                }else{
                    //if right is answer
                    key = new Key(leftText, rightText);
                }
                
                keys.add(key);
                System.out.println(key);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //note: longer text is Question always!
        return keys;
    }
}

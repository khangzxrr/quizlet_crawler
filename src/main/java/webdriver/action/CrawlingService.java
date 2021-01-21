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
import org.openqa.selenium.NoSuchElementException;
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

            Bank bank = new Bank(name, owner, driver.getCurrentUrl());

            return bank;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    

    public List<Key> crawlingKeys() {
        List<Key> keys = new ArrayList<>();

        try {
            //quizlet is tricky, so do us. simulating scrolling to force quizlet show all DOM
            JavascriptExecutor jsExecutor = WebDriverUtil.getJSExecutor(driver); //javascript boi!

            long currentHeight = 0;
            for(;;){
                try{
                    WebElement navigateElement = driver.findElement(By.cssSelector(".SlidingCarousel-counter"));
                    break;
                }catch(NoSuchElementException e){
                    if (e.getMessage().contains("SlidingCarousel-counter")){
                        System.out.println("still not reach end of page.. keep scrolling");
                    }
                }
                currentHeight += 1500;
                jsExecutor.executeScript(String.format("window.scrollTo(0,%d);", currentHeight)); //scroll using javascript
                Thread.sleep(100);
            }

            By leftSideSelector = By.cssSelector(".SetPageTerm-side.SetPageTerm-smallSide"); //left box selector
            By rightSideSelector = By.cssSelector(".SetPageTerm-side.SetPageTerm-largeSide"); //right box selector

            //get 10 (you define this number) first text|text to determine which is question and which is answer
            List<WebElement> leftDivsElements = driver.findElements(leftSideSelector); //left div element
            List<WebElement> rightDivsElements = driver.findElements(rightSideSelector); //right div element

            List<String> leftDivsText = new ArrayList<>(); //list contain left div innerText
            List<String> rightDivsText = new ArrayList<>();  //list contain right div innerText
            
            
            leftDivsElements.stream().forEach((leftDiv) -> { 
                leftDivsText.add(leftDiv.getAttribute("innerHTML").replaceAll("<br><br>", "<br>")); //get innerHTML to keep <br> 
            });

            rightDivsElements.stream().forEach((rightDiv) -> {
                rightDivsText.add(rightDiv.getAttribute("innerHTML").replaceAll("<br><br>", "<br>")); 
            });

            int leftIsAnswerConfident = 0;
            System.out.println("comparing leftbox..rightbox....");
            for (int i = 0; i < 10; i++) {
                if (leftDivsText.get(i).length() < leftDivsText.get(i).length()) { //if left text is shorter than right text
                    //assume it is answer => confident += 1
                    leftIsAnswerConfident++;
                }
            }

            //prediction use only 10 elements, it will be much faster than you compare every single time 
            //============================================================
            System.out.println("crawling...");

            for (int i = 0; i < leftDivsElements.size(); i++) { //left or right divs are equal element count

                Key key;
                if (leftIsAnswerConfident >= 5) {
                    //if left is answer
                    key = new Key(rightDivsText.get(i), leftDivsText.get(i));
                } else {
                    //if right is answer
                    key = new Key(leftDivsText.get(i), rightDivsText.get(i));
                }

                keys.add(key);
                System.out.println(key);
            }
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println("array of innerHTML is out of bounds,... please check left right box selectors again");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //note: longer text is Question always!
        return keys;
    }
}

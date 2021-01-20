/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdriver.action;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.WebDriverUtil;

/**
 *
 * @author skyho
 */
public class SubjectPagingService {
    WebDriver driver;
    String subject;
    
    int currentPage = 0;
    
    WebDriverWait wait;
    
    public SubjectPagingService(WebDriver driver, String subject) {
        this.driver = driver;
        this.subject = subject;
        
        wait = WebDriverUtil.getWait(driver);
    }
    
    public int getCurrentPageIndex(){
        return currentPage;
    }
    
    public List<String> getCurrentPageSources(){
        List<String> sourceUrls = new ArrayList<>();
        
        By sourceHrefSelector = By.cssSelector(".SetPreview-cardHeader > a");
        
        List<WebElement> hrefElements = driver.findElements(sourceHrefSelector);
        for(WebElement href : hrefElements){
            sourceUrls.add(href.getAttribute("href"));  
        }
        
        return sourceUrls;
    }
    
    public void nextPage(){ //next page and waiting for fully loaded HTML
        currentPage++;
        
        driver.get(String.format("https://quizlet.com/subject/%s/?page=%d", subject, currentPage));
        
        By pageNavigationTextSelector = By.cssSelector(".SearchPage-paginationText");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(pageNavigationTextSelector));
        //wait for it, if it appear that mean whole thing we need is loaded
    }
    
    public boolean isNotTheEnd(){ 
        // the end is near!! kidding, this method will let you know, when should we stop crawling ( reach end of pages )
        try{
            By endTextDivSelector = By.cssSelector(".SearchPage-emptyState");
            driver.findElement(endTextDivSelector); //if this not throw exception => end text is found => reached the end
            
            return false; //is the end
        }catch(Exception e){
            System.out.println("not end of pages tho...");
        }
        
        return true; //is not the end
    }
    
}

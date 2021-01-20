/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vongockhang.swt_crawling;

import dao.QuestionBankManager;
import dto.Bank;
import dto.Key;
import java.util.List;
import webdriver.action.LoginService;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import webdriver.action.CrawlingService;
import webdriver.action.SubjectPagingService;

/**
 *
 * @author skyho
 */
public class Main {

    public static String CHROME_DRIVER_PATH = "D:\\chromedriver.exe";
    
    public static String SUBJECT = "SWT301"; //this is what thay Hoang need
    
    public static String username = "khangzxrr";
    public static String password = "123123aaa";
    
    public static int maxPage = 3; //how many page should we craw... 3 is alots!
    
    
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        
        System.out.println("hello maven project");
        
        ChromeOptions chromeOptions = new ChromeOptions();
        
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER); 
        //no need to wait for fully loaded website
        
        WebDriver driver = new ChromeDriver(chromeOptions);
        
        try{
            QuestionBankManager questionBankManager = new QuestionBankManager();
            LoginService loginService = new LoginService(username, password, driver);
            loginService.login();
            
            
            CrawlingService crawlingService = new CrawlingService(driver);
            SubjectPagingService subjectPagingService = new SubjectPagingService(driver, SUBJECT);
            
            subjectPagingService.nextPage();
            
            
            while(subjectPagingService.isNotTheEnd()){
                List<String> sourceUrls = subjectPagingService.getCurrentPageSources();
                
                for(String sourceUrl : sourceUrls){
                    driver.get(sourceUrl);
                    
                    Bank bank = crawlingService.getCurrentBank();
                    questionBankManager.addBank(bank);
                    
                    if (bank != null){
                        List<Key> keys = crawlingService.crawlingKeys();
                        questionBankManager.addKeys(bank.getId(), keys);
                    }else{
                        System.out.println("cant get bank information,.... then we shoudnt continue, skip this source");
                    }
                    
                    
                }
                
                subjectPagingService.nextPage();
            }
            //https://quizlet.com/subject/SWT301 <-> quizlet subject search foramt
        }finally{
            //driver.quit();
        }
        
        
        
    }
}

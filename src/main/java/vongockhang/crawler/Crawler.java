/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vongockhang.crawler;

import dao.QuestionBankManager;
import dto.Bank;
import dto.Key;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.nio.file.FileSystemException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import webdriver.action.LoginService;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import util.DatabaseUtil;
import webdriver.action.CrawlingService;
import webdriver.action.SubjectPagingService;

/**
 *
 * @author skyho
 */
@Command(name = "Crawler", version = {"1.0"}, mixinStandardHelpOptions = true)
public class Crawler implements Runnable{
    @Parameters(paramLabel = "<subject>", defaultValue = "PRN292", description = "subject code which need to craw")
    public String SUBJECT = "PRN292"; //this is what thay Hoang need

    @Option(names = {"-user", "--username"}, description = "quizlet Username")
    public String username = "khangzxrr";
    @Option(names= {"-pass", "--password"}, description = "quizlet Password")
    public String password = "123123aaa";

    @Option(names= {"--page"}, description = { "how many page should we get"}) 
    public int MAX_PAGE = 1; //how many page should we craw... 1 is alots!

    @Option(names= {"--export", "-e"}, description = { "Export to static HTML page"})
    public String exportName = "";
    
    @Override
    public void run() {
        if (!exportName.isEmpty()){
            SourceExporter exporter = new SourceExporter(exportName);
            exporter.run();
            return;
        }
        
       WebDriver driver = null;
        
        try {
            DatabaseUtil.backupOldDatabaseAndCreateNewOne(); //backup and restore blank database
            
            WebDriverManager.chromedriver().setup(); //automatic driver searching. 
            
            ChromeOptions chromeOptions = new ChromeOptions(); 
            
            chromeOptions.addArguments("--blink-settings=imagesEnabled=false"); //disable image
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER); //no need to wait for every elements, DOM only is enough

            driver = new ChromeDriver(chromeOptions); //init new driver

            QuestionBankManager questionBankManager = new QuestionBankManager(); //sqlite data manager

            LoginService loginService = new LoginService(username, password, driver); //login service
            loginService.login();  //start login's actions

            CrawlingService crawlingService = new CrawlingService(driver); //crawling question | answer 
            
            SubjectPagingService subjectPagingService = new SubjectPagingService(driver, SUBJECT); //navigating between sources
            subjectPagingService.nextPage(); //next => 0 -> 1 => first page

            while (subjectPagingService.isNotTheEnd()) {
                List<String> sourceUrls = subjectPagingService.getCurrentPageSources();

                if (subjectPagingService.getCurrentPageIndex() == MAX_PAGE + 1) {
                    break; //get 2 page only
                }
                for (String sourceUrl : sourceUrls) {
                    driver.get(sourceUrl);

                    Bank bank = crawlingService.getCurrentBank(0);
                    
                    
                    if (bank != null) {
                        bank.setSourceUrl(sourceUrl);
                        
                        questionBankManager.addBank(bank);
                    
                        if (bank.getOwner().contains("BuiQuangAnh")){
                            System.out.println("BREAK PONT");
                        }
                        List<Key> keys = crawlingService.crawlingKeys(0);
                        questionBankManager.addKeys(bank.getId(), keys);
                        
                    } else {
                        System.out.println("cant get bank information,.... then we shoudnt continue, skip this source");
                    }

                }
                subjectPagingService.nextPage();
            }
            
        }catch(FileSystemException e){
            System.err.println("Please close any CMD, sqlite tool that access to original or current sql file!");
        }catch (IOException ex) {
            System.err.println("Error at backup, restore sql file...");
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (driver != null){
                driver.quit();    
            }
            
        }  
    }
    
    public static void main(String[] args){
        int exitCode = new CommandLine(new Crawler()).execute(args);
        System.exit(exitCode);
    }

    
}

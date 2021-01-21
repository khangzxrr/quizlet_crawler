/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vongockhang.swt_crawling;

import dao.QuestionBankManager;
import dto.Bank;
import dto.Key;
import j2html.tags.Tag;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import webdriver.action.LoginService;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import util.HTMLUtil;
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

    public static int MAX_PAGE = 1; //how many page should we craw... 3 is alots!

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);

        ChromeOptions chromeOptions = new ChromeOptions();
        
        chromeOptions.addArguments("--blink-settings=imagesEnabled=false");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

        WebDriver driver = new ChromeDriver(chromeOptions);

        try {
            //QuestionBankManager questionBankManager = new QuestionBankManager();
            LoginService loginService = new LoginService(username, password, driver);
            loginService.login();

            CrawlingService crawlingService = new CrawlingService(driver);
            SubjectPagingService subjectPagingService = new SubjectPagingService(driver, SUBJECT);

            subjectPagingService.nextPage();

            List<Tag> domContentSources = new ArrayList<>();

            while (subjectPagingService.isNotTheEnd()) {
                List<String> sourceUrls = subjectPagingService.getCurrentPageSources();

                if (subjectPagingService.getCurrentPageIndex() == MAX_PAGE + 1) {
                    break; //get 2 page only
                }
                for (String sourceUrl : sourceUrls) {
                    driver.get(sourceUrl);

                    Bank bank = crawlingService.getCurrentBank();
                    //questionBankManager.addBank(bank);

                    if (bank != null) {
                        List<Key> keys = crawlingService.crawlingKeys();

                        domContentSources.add(HTMLUtil.getHTMLSource(bank, keys));
                        //questionBankManager.addKeys(bank.getId(), keys);
                    } else {
                        System.out.println("cant get bank information,.... then we shoudnt continue, skip this source");
                    }

                }
                subjectPagingService.nextPage();
            }

            try (PrintWriter out = new PrintWriter("output.html")) {
                out.print(HTMLUtil.generateHTMLPage(domContentSources));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        } finally {
            driver.quit();
        }

    }
}

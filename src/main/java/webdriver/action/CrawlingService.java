/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdriver.action;

import dto.Bank;
import dto.Key;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import util.ConstantUtil;
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

    public Bank getCurrentBank(int tried) throws InterruptedException {
        if (tried == 10) {
            return null;
        }
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
            Thread.sleep(1000);
            return getCurrentBank(++tried);
        }
    }

    private boolean closePopupModal() {
        try {
            WebElement popupModalCloseButton = driver.findElement(By.cssSelector("div.UIModal-closeButtonWrapper > div > button"));
            if (popupModalCloseButton != null) {
                popupModalCloseButton.click();
            }

            return true;
        } catch (Exception e) {
        }

        return false;
    }

    private boolean getIntoExportPopup(int retried) {
        if (retried == 5) {
            return false;
        }

        try {
            JavascriptExecutor jsExecutor = WebDriverUtil.getJSExecutor(driver);
            jsExecutor.executeScript("var buttons = document.querySelectorAll('.SetPage-options button'); "
                    + "buttons[buttons.length - 1].dispatchEvent(new MouseEvent('mouseover', {bubbles: true}));");
            //using javascript to trigger mouse hover event!

            Thread.sleep(250);
            
            jsExecutor.executeScript("document.querySelector('.UIIcon--export').parentNode.parentNode.parentNode.click()");

            return true;
            //click on export button
        } catch (Exception e) {
            System.out.println("Retry getting into popup");
            return getIntoExportPopup(++retried);
        }


    }

    public List<Key> crawlingKeys(int triedTime) {
        List<Key> keys = new ArrayList<>();

        if (triedTime == 3) {
            return keys; //skip this shit
        }

        try {
            //quizlet is tricky, so do us. simulating scrolling to force quizlet show all DOM
            JavascriptExecutor jsExecutor = WebDriverUtil.getJSExecutor(driver); //javascript boi!

            long currentHeight = 0;
            for (;;) {
                try {
                    currentHeight += 400;
                    jsExecutor.executeScript(String.format("window.scrollTo(0,%d);", currentHeight)); //scroll using javascript

                    closePopupModal(); //must close the popup modal, if not DOM will not load the more options button

                    //tricky hack to allow popup in browser view
                    driver.findElement(By.cssSelector(".SetPage-options button"));
                    currentHeight += 400;
                    jsExecutor.executeScript(String.format("window.scrollTo(0,%d);", currentHeight)); //scroll using javascript
                    //===============
                    
                    break;

                } catch (Exception e) {
                    if (currentHeight > 1000){
                        currentHeight = 0;    
                    }
                }

            }

            if (!getIntoExportPopup(0)) { //only crawling if only popup is poped, otherwise retried
                driver.navigate().refresh();
                return crawlingKeys(++triedTime);
            }else{
                WebElement betweenRowsInput = driver.findElement(By.cssSelector("#SetPageExportModal-CustomRowDelim-input"));
                betweenRowsInput.clear();
                betweenRowsInput.sendKeys("\\nLINE_BREAK\\n");

                WebElement boxDeviderInput = driver.findElement(By.cssSelector("#SetPageExportModal-CustomWordDelim-input"));
                boxDeviderInput.clear();
                boxDeviderInput.sendKeys("SPLIT_BOX");

                WebElement copyTextClipboardButton = driver.findElement(By.cssSelector(".SetPageExportModal-copy button"));
                copyTextClipboardButton.click();
                Thread.sleep(500);
                copyTextClipboardButton.click(); //make sure it actually copy!

                String rawBoxesStr = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                String[] boxesStr = rawBoxesStr.split(ConstantUtil.LINE_BREAK);

                for (int i = 0; i < boxesStr.length; i++) { //left or right divs are equal element count
                    Key key = new Key(boxesStr[i]);
                    keys.add(key);
                }
            }
                
        } catch (Exception e) {
            System.out.println(e.getMessage() + " retry....");
            driver.navigate().refresh();
            return crawlingKeys(++triedTime);
        }

        //note: longer text is Question always!
        return keys;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdriver.action;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import vongockhang.swt_crawling.Main;
import util.WebDriverUtil;

/**
 *
 * @author skyho
 */
public class LoginService {

    WebDriver webDriver;
    String username;
    String password;

    public LoginService(String username, String password, WebDriver webDriver) {
        this.webDriver = webDriver;

        this.username = username;
        this.password = password;
    }

    private boolean openLoginModal(int retriedTime) throws InterruptedException {
        try {
            if (retriedTime == 10) {
                return false;
            }

            By loginButtonSelector = By.cssSelector(".SiteHeader-signIn > button");
            //first button is login button
            By loginModalSelector = By.cssSelector(".LoginPromptModal-form");

            WebElement loginButton = webDriver.findElement(loginButtonSelector);
            loginButton.click();

            if (webDriver.findElements(loginModalSelector).size() > 0) {
                return true;
            }

            
        }catch(Exception e){
            if (e.getMessage().contains(".SiteHeader-signIn > button")){
                Thread.sleep(3000);
                System.out.println("Login button doesnt exist yet...lets try again");
            }
        }
        
        return openLoginModal(retriedTime + 1);
    }
    
    private void submitLoginForm(){
        By usernameInputSelector = By.cssSelector("#username");
        //select by ID
        By passwordInputSelector = By.cssSelector("#password");
        //select by ID
        By submitButtonSelector = By.cssSelector(".LoginPromptModal-form > button");
        //using form class to select first button (login button)
        
        WebElement usernameInput = webDriver.findElement(usernameInputSelector);
        usernameInput.sendKeys(username);

        WebElement passwordInput = webDriver.findElement(passwordInputSelector);
        passwordInput.sendKeys(password);

        WebElement submitButton = webDriver.findElement(submitButtonSelector);
        submitButton.click();
    }

    public void login() throws InterruptedException {
        webDriver.get("https://quizlet.com");

        WebDriverWait wait = WebDriverUtil.getWait(webDriver);
        
        openLoginModal(0);
        submitLoginForm();

        By profileImageSelector = By.cssSelector(".UserAvatar");
        //image profile, using to decide user is login or not
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(profileImageSelector));
        //waiting to redirect...
    }
}

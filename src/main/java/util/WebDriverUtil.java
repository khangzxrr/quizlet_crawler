/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author skyho
 */
public class WebDriverUtil {
    public static int TIMEOUT_IN_SECONDS = 30;
    public static WebDriverWait getWait(WebDriver webDriver) {
        return new WebDriverWait(webDriver, TIMEOUT_IN_SECONDS);
    }
    
    public static JavascriptExecutor getJSExecutor(WebDriver webDriver){
        return (JavascriptExecutor) webDriver;
    }
}

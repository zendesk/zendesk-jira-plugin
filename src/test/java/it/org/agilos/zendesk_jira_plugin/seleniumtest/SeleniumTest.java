package it.org.agilos.zendesk_jira_plugin.seleniumtest;

import it.org.agilos.zendesk_jira_plugin.integrationtest.JIRAClient;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.SeleneseTestNgHelper;

public class SeleniumTest extends SeleneseTestNgHelper {
	/** Which browser should selenium use for running the webtests */
	private static final String seleniumBrowser = System.getProperty("selenium.browser", null);
	
	/** Thw delay in ms between each selenium step.*/
	private static final String seleniumStepDelay = System.getProperty("selenium.speed", "0");
	
//	@BeforeMethod (alwaysRun = true)  
//	void setUpSeleniumTest() throws Exception {
//		setUp(JIRAClient.jiraUrl, seleniumBrowser);	
//		selenium.setSpeed(seleniumStepDelay);
//	}
//	
//	@Test public void testLogin() throws Exception {
//		selenium.open("/secure/Dashboard.jspa");
//		selenium.type("os_username", "bamboo");
//		selenium.type("os_password", "bamboo2997");
//		selenium.click("login");
//	}
}

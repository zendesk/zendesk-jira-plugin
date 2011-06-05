package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.util.Date;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.framework.issuehandler.IssueHandler;
import org.agilos.zendesk_jira_plugin.integrationtest.framework.issuehandler.IssueHandlerProvider;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.log4testng.Logger;

import com.thoughtworks.selenium.Selenium;

public abstract class JIRATest {

	private Logger log = Logger.getLogger(JIRATest.class);

	protected static final String PROJECT_KEY = "WST";
	protected static final String USER_ID = "integration";
	protected static final String PROJECT_DESCRIPTION = "This is a Zendesk JIRA plugin integrationtest project " + new Date();	 

	protected abstract JIRAFixture getFixture() throws Exception;
	
	protected Selenium selenium;
	protected WebDriver driver;

    protected IssueHandler issueHandler = IssueHandlerProvider.getIssueHandler();

	@BeforeMethod (alwaysRun = true)
	protected void setUpTest() throws Exception {
		selenium = getFixture().selenium;
		driver = getFixture().driver;
		
		log.info("Restoring data from "+"restoreData-JIRA-"+System.getProperty("jira.deploy.version", "4.3.2")+".xml");
		try {
	    	getFixture().loadData("restoreData-JIRA-"+System.getProperty("jira.deploy.version", "4.3.2")+".xml");
			getFixture().connect();
			getFixture().createUserWithUsername(USER_ID);
			getFixture().createProjectWithKeyAndNameAndLead(PROJECT_KEY, "WebserviceTest project", USER_ID); 

		} catch (Exception e) {
			log.error("Unable to initialize jira", e);
		}          
	}
}
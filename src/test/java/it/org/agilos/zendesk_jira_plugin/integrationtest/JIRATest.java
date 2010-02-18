package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.util.Date;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.testng.annotations.BeforeMethod;
import org.testng.log4testng.Logger;

public abstract class JIRATest {

	private Logger log = Logger.getLogger(JIRATest.class);

	protected static final String PROJECT_KEY = "WST";
	protected static final String USER_ID = "integration";
	protected static final String PROJECT_DESCRIPTION = "This is a Zendesk JIRA plugin integrationtest project " + new Date();	 

	protected abstract JIRAFixture getFixture() throws Exception;

	@BeforeMethod (alwaysRun = true)
	void setUpTest() throws Exception {
		log.info("Restoring data from "+"restoreData-JIRA-"+System.getProperty("jira.deploy.version", "4.0")+".xml");
		try {
	    	getFixture().loadData("restoreData-JIRA-"+System.getProperty("jira.deploy.version", "4.0")+".xml");
			getFixture().connect();
			getFixture().createUserWithUsername(USER_ID);
			getFixture().createProjectWithKeyAndNameAndLead(PROJECT_KEY, "WebserviceTest project", USER_ID); 

		} catch (Exception e) {
			log.error("Unable to initialize jira", e);
		}          
	}
}
package it.org.agilos.zendesk_jira_plugin.integrationtest;


import it.org.agilos.zendesk_jira_plugin.integrationtest.testcases.NotificationTest;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.apache.log4j.Logger;
import org.restlet.data.Request;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractNotificationTest extends JIRATest {
	public NotificationFixture fixture;
	protected String issueKey;    

	private Logger log = Logger.getLogger(NotificationTest.class.getName());

	@BeforeMethod (alwaysRun = true)  
	@Override
	protected void setUpTest() throws Exception {
		super.setUpTest();
		issueKey = fixture.createIssue(PROJECT_KEY).getKey();       
	}

	@AfterMethod  (alwaysRun = true)
	protected void tearDownTest() throws Exception {
		Request request = fixture.getNextRequestInstant();
		if (request != null) log.warn("Notification remains on message queue after testcase has finish" + request.getEntityAsText()); 
	}

	@Override
	protected JIRAFixture getFixture() {
		if (fixture == null ) fixture = new NotificationFixture();
		return fixture;
	}
}

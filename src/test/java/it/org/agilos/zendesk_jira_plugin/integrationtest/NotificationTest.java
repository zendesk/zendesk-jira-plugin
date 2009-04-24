package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.restlet.data.Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class NotificationTest {

	static final String JIRA_URL = "http://192.168.0.100:8080";
	static final String LOGIN_NAME = "bamboo";
	static final String LOGIN_PASSWORD = "bamboo2997";	
	
    public NotificationFixture fixture;
    private String issueKey;
    
	@BeforeMethod
    void setup() throws Exception {
		fixture = new NotificationFixture(JIRA_URL,LOGIN_NAME,LOGIN_PASSWORD);
		cleanData();
		issueKey = fixture.createIssue().getKey();        
    }

	public void testCommentAddedNotification() throws Exception  {
		fixture.updateIssueWithComment(issueKey, "Test comment");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testCommentAddedNotification.1"), request.getEntityAsText());
	}
	
	//Needs to exterminate all data before each test to ensure a stable test environment
	private void cleanData() {
		try {
		} catch (Exception e) {}
	}
}

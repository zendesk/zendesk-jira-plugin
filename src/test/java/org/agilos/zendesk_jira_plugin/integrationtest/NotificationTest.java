package org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.WebserviceFixture;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NotificationTest {

	static final String JIRA_URL = "http://localhost:1990/jira";
	static final String LOGIN_NAME = "test";
	static final String LOGIN_PASSWORD = "test";	
	
    public NotificationFixture fixture;
    private static final String user1 = "brian";
    private static final String user2 = "ole";
    
	@BeforeMethod
    void setup() throws Exception {
		fixture = new NotificationFixture(JIRA_URL,LOGIN_NAME,LOGIN_PASSWORD);
		cleanData();
        fixture.createIssue();        
    }

	@Test (groups = {"testfirst"} )
	public void testCommentAdded() throws Exception  {
		assert fixture.assignableUsers().length == 0;
	}
	
	
	//Needs to exterminate all data before each test to ensure a stable test environment
	private void cleanData() {
		try {
		} catch (Exception e) {}
	}
}

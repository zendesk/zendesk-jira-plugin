package org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.WebserviceFixture;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WebserviceTest {

	static final String JIRA_URL = "http://localhost:1990/jira";
	static final String LOGIN_NAME = "test";
	static final String LOGIN_PASSWORD = "test";	
	
    public WebserviceFixture fixture;
    private static final String user1 = "brian";
    private static final String user2 = "ole";
    
	@BeforeMethod
    void setup() throws Exception {
		fixture = new WebserviceFixture(JIRA_URL,LOGIN_NAME,LOGIN_PASSWORD);
		cleanData();
        fixture.createProject();        
    }

	@Test (groups = {"testfirst"} )
	public void testNoAssignableUsers() throws Exception  {
		assert fixture.assignableUsers().length == 0;
	}
	
	@Test (groups = {"testfirst"} )
	public void testSingleAssignableUser() throws Exception  {
		fixture.createUserWithUsername(user1);
		fixture.assignUserToProject(user1);
		assert fixture.assignableUsers().length == 1;
		fixture.unassignUserFromProject(user1);
	}
	
	//Needs to exterminate all data before each test to ensure a stable test environment
	private void cleanData() {
		try {
    	fixture.removeProject();
		} catch (Exception e) {}
		try {
		fixture.removeUser(user1);
		} catch (Exception e) {}
		try {
			fixture.removeUser(user2);
		} catch (Exception e) {}
	}
}

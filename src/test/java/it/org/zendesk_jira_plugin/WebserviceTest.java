package it.org.zendesk_jira_plugin;

import org.agilos.zendesk_jira_plugin.integrationtest.WebserviceFixture;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WebserviceTest {
	
    public WebserviceFixture fixture;
    private static final String user1 = "brian";
    private static final String user2 = "ole";
    
	@BeforeMethod
    protected void setUp() throws Exception {
        fixture = new WebserviceFixture();
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

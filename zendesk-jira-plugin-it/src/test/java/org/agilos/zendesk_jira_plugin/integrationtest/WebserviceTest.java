package org.agilos.zendesk_jira_plugin.integrationtest;

import org.testng.annotations.*;

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
    
    @AfterMethod
    protected void tearDown() throws Exception {
    }
	 
	@Test
	public void testNoAssignableUsers() throws Exception  {
		assert fixture.getAssignableUsers().size()==0;
	}
	
	@Test
	public void testSingleAssignableUser() throws Exception  {
		fixture.addUser(user1);
		fixture.assignUserToProject(user1);
		assert fixture.getAssignableUsers().size() == 1;
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

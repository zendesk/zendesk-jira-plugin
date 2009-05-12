package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.WebserviceFixture;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WebserviceTest extends JIRATest {

    private WebserviceFixture fixture;
    
	@BeforeMethod (alwaysRun = true)
    void setUpTest() throws Exception {
		fixture = new WebserviceFixture();
    	getFixture().loadData("restoreData.xml");
    	getFixture().connect();
		fixture.createUserWithUsername(USER_ID);
	    fixture.createProjectWithKeyAndNameAndLead(PROJECT_KEY, "WebserviceTest project", USER_ID);  
    }
   /**
   * Validates that different unassignable roles according to the default permission scheme, doesn't cause the number of assignable user to rise.
   * Instead of presuming 0 assignable users, which is pretty fragile, we calibrate the testcase from the initial number of assignable users. 
   * @throws Exception
   */
	@Test (groups = {"regressionTests"} )
	public void testNoAssignableUsers() throws Exception  {
		int intialNumberOfAssignableUers = fixture.assignableUsers(PROJECT_KEY).length; 
		String user2 = "nonassignableuser";
		fixture.createUserWithUsername(user2);
		//Because users are initially enrolled in the 'developer' group, the new user will initially be assignable
		assert fixture.assignableUsers(PROJECT_KEY).length == intialNumberOfAssignableUers+1;
		fixture.removeUserFromGroup(user2, JIRAFixture.JIRA_DEVELOPERS);
		assert fixture.assignableUsers(PROJECT_KEY).length == intialNumberOfAssignableUers;
		fixture.addUserToGroup(user2, JIRAFixture.JIRA_ADMINISTRATORS); 
		assert fixture.assignableUsers(PROJECT_KEY).length == intialNumberOfAssignableUers;
	}
	
	@Test (groups = {"regressionTests"} )
	public void testSingleAssignableUser() throws Exception  {
		int intialNumberOfAssignableUers = fixture.assignableUsers(PROJECT_KEY).length; 
		String user2 = "assignableuser";
		fixture.createUserWithUsername(user2);
		fixture.removeUserFromGroup(user2, JIRAFixture.JIRA_DEVELOPERS);
		assert fixture.assignableUsers(PROJECT_KEY).length == intialNumberOfAssignableUers;
		fixture.assignUserToProject(user2, PROJECT_KEY);
		assert fixture.assignableUsers(PROJECT_KEY).length == intialNumberOfAssignableUers+1;
		fixture.unassignUserFromProject(user2);
		assert fixture.assignableUsers(PROJECT_KEY).length == intialNumberOfAssignableUers;
	}

	@Override
	protected JIRAFixture getFixture() {
		return fixture;
	}
}

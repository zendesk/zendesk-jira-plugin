package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertTrue;

import org.agilos.jira.soapclient.RemoteAuthenticationException;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.WebserviceFixture;
import org.testng.annotations.Test;

public class WebserviceTest extends JIRATest {

    private WebserviceFixture fixture;
	
	/**
	 * Verifies that login requests with invalid users are rejected.
	 */
	@Test (groups = {"regressionTests"} )
	public void testLoginInvalidUser() throws Exception {
		try {
			ZendeskWSClient.getSoapService().login("Invalid user", JIRA.LOGIN_PASSWORD);
			assertTrue("No authenticationexception throw on invalid user", false);
		} catch (RemoteAuthenticationException e) {
			// Expected behavior
		} 
		ZendeskWSClient.getSoapService().login(JIRA.LOGIN_NAME, JIRA.LOGIN_PASSWORD);
	}
	
	/**
	 * Verifies that login requests with invalid passwords are rejected.
	 */
	@Test (groups = {"regressionTests"} )
	public void testLoginInvalidPassword() throws Exception  {
		try {
			ZendeskWSClient.getSoapService().login(JIRA.LOGIN_NAME, "Invalid password");
			assertTrue("No authenticationexception throw on invalid password", false);
		} catch (RemoteAuthenticationException e) {
			// Expected behavior
		} 
		ZendeskWSClient.getSoapService().login(JIRA.LOGIN_NAME, JIRA.LOGIN_PASSWORD);
	}
	
	/**
	 * Verifies that soap requests with invalid tokens are rejected
	 */
	@Test (groups = {"regressionTests"} )
	public void testWSAuthentication() throws Exception   {
		try {
			ZendeskWSClient.getSoapService().getAssignableUsers("InvalidToken", PROJECT_KEY);
			assertTrue("No authenticationexception throw on invalid token", false);
		} catch (RemoteAuthenticationException e) {
			// Expected behavior
		} 
		ZendeskWSClient.getSoapService().getAssignableUsers(ZendeskWSClient.getSoapToken(), PROJECT_KEY);
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
		if (fixture == null ) fixture = new WebserviceFixture();
		return fixture;
	}
}

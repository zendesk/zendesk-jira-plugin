package it.org.zendesk_jira_plugin;

import org.testng.annotations.*;
import org.agilos.zendesk_jira_plugin.integrationtest.WebserviceFixture;
import com.atlassian.jira.functest.framework.FuncTestCase;

public class WebserviceTest extends FuncTestCase {
	
    public WebserviceFixture fixture;
    private static final String user1 = "brian";
    private static final String user2 = "ole";
	 
	@Test
	public void testNoAssignableUsers() throws Exception  {
		assert fixture.assignableUsers().size()==0;
	}
	
	@Test
	public void testSingleAssignableUser() throws Exception  {
		fixture.createUserWithUsername(user1);
		fixture.assignUserToProject(user1);
		assert fixture.assignableUsers().size() == 1;
		fixture.unassignUserFromProject(user1);
	}
}

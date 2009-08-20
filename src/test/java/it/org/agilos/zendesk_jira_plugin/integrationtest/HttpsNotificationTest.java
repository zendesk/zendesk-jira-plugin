package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertNotNull;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.restlet.data.Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HttpsNotificationTest extends JIRATest {
	public NotificationFixture fixture;
    private String issueKey;
    
    @BeforeMethod (alwaysRun = true)
    void setUpTest() throws Exception {
    	fixture = new NotificationFixture();
    	getFixture().loadData("restoreData.xml");
    	getFixture().connect();
    	getFixture().createUserWithUsername(USER_ID);
    	fixture.createProjectWithKeyAndNameAndLead(PROJECT_KEY, "NotificationTest project", USER_ID);  
    	issueKey = fixture.createIssue(PROJECT_KEY).getKey();            	

		getFixture().getJiraClient().setZendeskUrl("https://localhost:8443");
		//getFixture().getJiraClient().setZendeskCredentials("mikis@agilis-software.dk", "jira1");
    }
    
    /**
	 * ZEN-37 Support for notifications over https, http://jira.agilos.org/browse/ZEN-37
	 */
	@Test 
	public void testHttpsNotification() throws Exception  {		
    	fixture.updateIssueWithComment(issueKey, "Automatic systemtest generated comment");
    	Request request = fixture.getNextHttpsRequest(); 
		assertNotNull("No notification received on https listener", request);		
	}
    
	@Override
	protected JIRAFixture getFixture() {
		return fixture;
	}
}

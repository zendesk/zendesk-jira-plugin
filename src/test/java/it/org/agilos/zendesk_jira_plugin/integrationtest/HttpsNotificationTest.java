package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.testng.annotations.AfterMethod;
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

		getFixture().getJiraClient().setZendeskUrl("https://localhost:8183");
    }
    
    @AfterMethod (alwaysRun = true)
    void tearDownTest() throws Exception {
		getFixture().getJiraClient().setZendeskUrl("https://localhost:8183");
    }
    
    /**
	 * ZEN-37 Option for private comments, http://jira.agilos.org/browse/ZEN-36
	 */
	//@Test 
	public void testHttpsNotification() throws Exception  {
		getFixture().getJiraClient().setZendeskUrl("https://localhost:8183");
	}
    
	@Override
	protected JIRAFixture getFixture() {
		return fixture;
	}
}

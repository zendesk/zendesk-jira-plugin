package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertNotNull;

import org.restlet.data.Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HttpsNotificationTest extends AbstractNotificationTest {

    @BeforeMethod (alwaysRun = true)  
	@Override
	void setUpTest() throws Exception {
		super.setUpTest();
		getFixture().getJiraClient().setZendeskUrl("https://localhost:8443");
	}

	/**
	 * ZEN-37 Support for notifications over https, http://jira.agilos.org/browse/ZEN-37
	 */
	@Test (groups = {"regressionTests"} )
	public void testHttpsNotification() throws Exception  {		
		fixture.updateIssueWithComment(issueKey, "Automatic systemtest generated comment");
		Request request = fixture.getNextHttpsRequest(); 
		assertNotNull("No notification received on https listener", request);		
	}
}

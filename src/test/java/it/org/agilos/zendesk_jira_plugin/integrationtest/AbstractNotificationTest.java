package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.apache.log4j.Logger;
import org.restlet.data.Request;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class AbstractNotificationTest extends JIRATest {
	public NotificationFixture fixture;
    protected String issueKey;    

	private Logger log = Logger.getLogger(NotificationTest.class.getName());
    
    @BeforeMethod (alwaysRun = true)  
    void setUpTest() throws Exception {
    	fixture = new NotificationFixture();
    	getFixture().loadData("restoreData.xml");
    	getFixture().connect();
    	getFixture().createUserWithUsername(USER_ID);
    	fixture.createProjectWithKeyAndNameAndLead(PROJECT_KEY, "NotificationTest project", USER_ID);  
    	issueKey = fixture.createIssue(PROJECT_KEY).getKey();           
    }

    @AfterMethod  (alwaysRun = true)
    void tearDownTest() throws Exception {
    	Request request = fixture.getNextRequestInstant();
    	if (request != null) log.warn("Notification remains on message queue after testcase has finish" + request.getEntityAsText()); 
    }
    
	@Override
	protected JIRAFixture getFixture() {
		return fixture;
	}
}

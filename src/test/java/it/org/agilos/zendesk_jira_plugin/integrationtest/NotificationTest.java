package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Calendar;

import junit.framework.AssertionFailedError;

import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.apache.log4j.Logger;
import org.restlet.data.Parameter;
import org.restlet.data.Request;
import org.restlet.engine.http.HttpConstants;
import org.restlet.engine.http.HttpRequest;
import org.restlet.util.Series;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NotificationTest extends JIRATest {

	public NotificationFixture fixture;
    private String issueKey;

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
    	Request request = fixture.getNextRequest();
    	if (request != null) log.warn("Notification remains on message queue after testcase has finish" + request.getEntityAsText()); 
    }

    @Test (groups = {"regressionTests"} )
    public void testCommentAddedNotification() throws Exception  {
    	fixture.updateIssueWithComment(issueKey, "Test comment");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testCommentAddedNotification.1"), request.getEntityAsText());
	}
	
	@Test (groups = {"regressionTests"} )
	public void testSummeryChangedNotification() throws Exception  {
		fixture.updateIssueWithSummary(issueKey, "This is a changed summary");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testSummaryChangedNotification.1"), request.getEntityAsText());		
	}
	
	@Test (groups = {"regressionTests"} )
	public void testStatusChangedNotification() throws Exception  {
		fixture.setIssueToResolved(issueKey);
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after resolving issue", TestDataFactory.getSoapResponse("testStatusChangedNotification.1"), request.getEntityAsText());		
	}
	
	/**
	 * ZEN-19 Unable to correctly detect changed parameters, http://jira.agilos.org/browse/ZEN-19
	 */
	@Test (groups = {"regressionTests"} )	
	public void testDescriptionChangedNotification() throws Exception  {
		fixture.updateIssueWithDescription(issueKey, "This is a changed description");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing desciption", TestDataFactory.getSoapResponse("testDescriptionChangedNotification.1"), request.getEntityAsText());		
	}
	
	//@Test  (groups = {"regressionTests"} )
	public void testIssueMoveNotification() throws Exception  {
		String newProjectKey = "IMN";
    	RemoteProject project = fixture.createProjectWithKeyAndNameAndLead(newProjectKey, "Issue move notification test project", USER_ID);  
		fixture.moveIssue(issueKey, project);
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testIssueMoveNotification.1"), request.getEntityAsText());		
	}
	
	/**
	 * ZEN-18 Content-length not set in notification http headers, http://jira.agilos.org/browse/ZEN-18
	 */
	@Test (groups = {"regressionTests"} )
	public void testNotificationHeaders() throws Exception  {
		fixture.updateIssueWithSummary(issueKey, "Testing testNotificationHeaders-"+Calendar.getInstance().getTimeInMillis());
		HttpRequest request = (HttpRequest)fixture.getNextRequest();
		Series<Parameter> headers = request.getHeaders();
		assertTrue("No content-length in headers of request received", headers.getValues(HttpConstants.HEADER_CONTENT_LENGTH) != null);
	}
	
	/**
	 * ZEN-32 Prevent Zendesk-JIRA notification loops, http://jira.agilos.org/browse/ZEN-32
	 */
	@Test (groups = {"regressionTests"} )
	public void testNotificationLoops() throws Exception  {
		getFixture().getJiraClient().login("zendesk","zendeskpw");

		fixture.updateIssueWithDescription(issueKey, "This is a changed description by the zendesk user, no notification should be set");
		Request request = fixture.getNextRequest(); 
		assertEquals("Notification received for Zendesk user update, ", null, request);
	}
	
	/**
	 * ZEN-36 Option for private comments, http://jira.agilos.org/browse/ZEN-36
	 */
	@Test 
	public void testPrivateNotification() throws Exception  {
		getFixture().getJiraClient().setCommentsPublic("false");
		
		fixture.updateIssueWithComment(issueKey, "Test comment");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received for private comment notification", TestDataFactory.getSoapResponse("testPrivateNotification.1"), request.getEntityAsText());

		getFixture().getJiraClient().setCommentsPublic("true");
		
		fixture.updateIssueWithComment(issueKey, "Test comment");
		request = fixture.getNextRequest(); 
		assertEquals("Wrong response received for public comment notification", TestDataFactory.getSoapResponse("testPrivateNotification.2"), request.getEntityAsText());
	}
	
	@Test 
	public void testPrivateNotificationInvalidString()throws Exception {
		getFixture().getJiraClient().setCommentsPublic("fllll");
		
		fixture.updateIssueWithComment(issueKey, "Test comment");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received for garbage comment notification is-public setting", TestDataFactory.getSoapResponse("testPrivateNotificationInvalidString.1"), request.getEntityAsText());

		getFixture().getJiraClient().setCommentsPublic("false");
		
		fixture.updateIssueWithComment(issueKey, "Test comment");
		request = fixture.getNextRequest(); 
		assertEquals("Wrong response received for public comment notification", TestDataFactory.getSoapResponse("testPrivateNotificationInvalidString.2"), request.getEntityAsText());
	
	}
    
	@Override
	protected JIRAFixture getFixture() {
		return fixture;
	}
}

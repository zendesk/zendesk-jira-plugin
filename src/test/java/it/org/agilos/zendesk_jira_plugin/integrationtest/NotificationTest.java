package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Calendar;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;
import org.restlet.data.Parameter;
import org.restlet.data.Request;
import org.restlet.engine.http.HttpConstants;
import org.restlet.engine.http.HttpRequest;
import org.restlet.util.Series;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NotificationTest {

	static final String JIRA_URL = "http://192.168.0.100:8080";
	static final String LOGIN_NAME = "bamboo";
	static final String LOGIN_PASSWORD = "bamboo2997";	
	
    public NotificationFixture fixture;
    private String issueKey;
    
	@BeforeMethod
    void setup() throws Exception {
		fixture = new NotificationFixture(JIRA_URL,LOGIN_NAME,LOGIN_PASSWORD);
		cleanData();
		issueKey = fixture.createIssue().getKey();        
    }

	@Test
	public void testCommentAddedNotification() throws Exception  {
		fixture.updateIssueWithComment(issueKey, "Test comment");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testCommentAddedNotification.1"), request.getEntityAsText());
	}
	
	@Test
	public void testSummeryChangedNotification() throws Exception  {
		fixture.updateIssueWithSummary(issueKey, "This is a changed summary");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testSummaryChangedNotification.1"), request.getEntityAsText());		
	}
	
	/**
	 * ZEN-19 Unable to correctly detect changed parameters, http://jira.agilos.org/browse/ZEN-19
	 */
	@Test (groups = {"testfirst"} )	
	public void testDescriptionChangedNotification() throws Exception  {
		fixture.updateIssueWithDescription(issueKey, "This is a changed description");
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after changing desciption", TestDataFactory.getSoapResponse("testDescriptionChangedNotification.1"), request.getEntityAsText());		
	}
	
//	@Test
//	public void testIssueMoveNotification() throws Exception  {
//        fixture.createProject("testMovingIssue");
//		fixture.moveIssue(issueKey, "second project");
//		Request request = fixture.getNextRequest(); 
//		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testDescriptionChangedNotification.1"), request.getEntityAsText());		
//	}
	
	/**
	 * ZEN-18 Content-length not set in notification http headers, http://jira.agilos.org/browse/ZEN-18
	 */
	@Test (groups = {"testfirst"} )
	public void testNotificationHeaders()throws Exception  {
		fixture.updateIssueWithSummary(issueKey, "Testing testNotificationHeaders-"+Calendar.getInstance().getTimeInMillis());
		HttpRequest request = (HttpRequest)fixture.getNextRequest();
		Series<Parameter> headers = request.getHeaders();
		assertTrue("No content-length in headers of request received", headers.getValues(HttpConstants.HEADER_CONTENT_LENGTH) != null);
//		assertTrue("Wrong content-length received", headers.getValues(HttpConstants.HEADER_CONTENT_LENGTH) > 0; 
	}
	
	//Needs to exterminate all data before each test to ensure a stable test environment
	private void cleanData() {
		try {
		} catch (Exception e) {}
	}
}

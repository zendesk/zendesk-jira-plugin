package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Calendar;

import org.agilos.jira.soapclient.RemoteComment;
import org.agilos.jira.soapclient.RemoteIssue;
import org.agilos.jira.soapclient.RemoteProject;
import org.apache.log4j.Logger;
import org.restlet.data.Parameter;
import org.restlet.engine.http.HttpConstants;
import org.restlet.engine.http.HttpRequest;
import org.restlet.util.Series;
import org.testng.annotations.Test;

public class NotificationTest extends AbstractNotificationTest {
	private static final Logger log = Logger.getLogger(NotificationTest.class.getName());
	
    @Test (groups = {"regressionTests"} )
    public void testCommentAddedNotification() throws Exception  {
    	fixture.updateIssueWithComment(issueKey, "Test comment");
		assertEquals("Wrong response received after changing comment", 
				TestDataFactory.getSoapResponse("testCommentAddedNotification.1"), 
				fixture.getNextRequest().getEntityAsText());
	}
    
	@Test (groups = {"regressionTests", "jira3-failing"} )  //Doesn't work on JIRA 3, priority field is set, even though the test doesn't touch the field.
	public void testDescriptionAndCommentChangedNotification() throws Exception  {
		fixture.updateIssueWithDescriptionAndComment(issueKey, "This is a description and comment change test", 
				"This is the comment part of the summery + comment change");
		assertEquals("Wrong change response received after changing description and comment", 
				TestDataFactory.getSoapResponse("testDescriptionAndCommentChangedNotification.1"), 
				fixture.getNextRequest().getEntityAsText());		
	    assertEquals("Wrong comment response received after changing description and comment", 
	    		TestDataFactory.getSoapResponse("testDescriptionAndCommentChangedNotification.2"), 
	    		fixture.getNextRequest().getEntityAsText());		
	}
	
	@Test (groups = {"regressionTests"} )
	public void testSummeryChangedNotification() throws Exception  {
		fixture.updateIssueWithSummary(issueKey, "This is a changed summary");
		assertEquals("Wrong change response received after changing comment", 
				TestDataFactory.getSoapResponse("testSummaryChangedNotification.1"), 
				fixture.getNextRequest().getEntityAsText());		
		assertEquals("Wrong comment response received after changing comment", 
				TestDataFactory.getSoapResponse("testSummaryChangedNotification.2"), 
				fixture.getNextRequest().getEntityAsText());		
		}
	
	@Test (groups = {"regressionTests", "jira3-failing"} )
	public void testStatusChangedNotification() throws Exception  {
		fixture.setIssueToResolved(issueKey);
		assertEquals("Wrong response received after resolving issue", 
				TestDataFactory.getSoapResponse("testStatusChangedNotification.1"), 
				fixture.getNextRequest().getEntityAsText());		
	}
	
	/**
	 * ZEN-19 Unable to correctly detect changed parameters, http://jira.agilos.org/browse/ZEN-19
	 */
	@Test (groups = {"regressionTests"} )	
	public void testDescriptionChangedNotification() throws Exception  {
		fixture.updateIssueWithDescription(issueKey, "This is a changed description");
		assertEquals("Wrong change response received after changing description", 
				TestDataFactory.getSoapResponse("testDescriptionChangedNotification.1"), 
				fixture.getNextRequest().getEntityAsText());
		assertEquals("Wrong comment response received after changing description", 
				TestDataFactory.getSoapResponse("testDescriptionChangedNotification.2"), 
				fixture.getNextRequest().getEntityAsText());		
		}
	
	@Test  (groups = {"regressionTests"} )
	public void testIssueMoveNotification() throws Exception  {
		String newProjectKey = "IMN";
    	RemoteProject project = fixture.createProjectWithKeyAndNameAndLead(newProjectKey, 
    			"Issue move notification test project", USER_ID);  
		fixture.moveIssue(issueKey, project);
		assertEquals("Wrong change response received after changing comment", 
				TestDataFactory.getSoapResponse("testIssueMoveNotification.1"), 
				fixture.getNextRequest().getEntityAsText());		
		assertEquals("Wrong comment response received after changing comment", 
				TestDataFactory.getSoapResponse("testIssueMoveNotification.2"), 
				fixture.getNextRequest().getEntityAsText());		
		}
	
	/**
	 * ZEN-18 Content-length not set in notification http headers, http://jira.agilos.org/browse/ZEN-18
     * ZEN-93 Wrong content type in header, http://jira.agilos.org/browse/ZEN-93
	 */
	@Test (groups = {"regressionTests"} )
	public void testNotificationHeaders() throws Exception  {
		fixture.updateIssueWithSummary(issueKey, "Testing testNotificationHeaders-"+Calendar.getInstance().getTimeInMillis());
		HttpRequest request = (HttpRequest)fixture.getNextRequest();
		Series<Parameter> headers = request.getHeaders();
		assertTrue("No content-length in headers of request received", headers.getValues(HttpConstants.HEADER_CONTENT_LENGTH) != null);           
		assertEquals("Wrong content in headers of request received", "application/xml; charset=UTF-8", headers.getValues(HttpConstants.HEADER_CONTENT_TYPE));
	}
	
	/**
	 * ZEN-32 Prevent Zendesk-JIRA notification loops, http://jira.agilos.org/browse/ZEN-32
	 */
	@Test (groups = {"regressionTests"} )
	public void testNotificationLoops() throws Exception  {
		getFixture().getJiraClient().connect("zendesk","zendeskpw");

		fixture.updateIssueWithDescription(issueKey, 
				"This is a changed description by the zendesk user, no notification should be sent");
		assertEquals("Notification received for Zendesk user update, ", null, fixture.getNextRequest());
	}
	
	/**
	 * ZEN-36 Option for private comments, http://jira.agilos.org/browse/ZEN-36
	 */
	@Test (groups = {"regressionTests"} )
	public void testPrivateNotification() throws Exception  {
		String commentsValue = "false";
		getFixture().getJiraClient().setCommentsPublic(commentsValue);
		
		fixture.updateIssueWithComment(issueKey, 
				"Adding comment after public commets attributes has been set to "+commentsValue);
		assertEquals("Wrong response received for private comment notification", 
				TestDataFactory.getSoapResponse("testPrivateNotification.1"), 
				fixture.getNextRequest().getEntityAsText());
		
		commentsValue = "true";
		getFixture().getJiraClient().setCommentsPublic(commentsValue);
		
		fixture.updateIssueWithComment(issueKey, 
				"Adding comment after public commets attributes has been set to " + commentsValue);
		assertEquals("Wrong response received for public comment notification", 
				TestDataFactory.getSoapResponse("testPrivateNotification.2"), 
				fixture.getNextRequest().getEntityAsText());
	}
	
	@Test (groups = {"regressionTests"} )
	public void testPrivateNotificationInvalidString()throws Exception {
		String commentsValue = "fllll";
		getFixture().getJiraClient().setCommentsPublic(commentsValue);
		
		fixture.updateIssueWithComment(issueKey, 
				"Adding comment after public commets attributes has been set to " + commentsValue);
		assertEquals("Wrong response received for garbage comment notification is-public setting", 
				TestDataFactory.getSoapResponse("testPrivateNotificationInvalidString.1"), 
				fixture.getNextRequest().getEntityAsText());
		
		commentsValue = "false";
		getFixture().getJiraClient().setCommentsPublic(commentsValue);
		
		fixture.updateIssueWithComment(issueKey, 
				"Adding comment after public commets attributes has been set to " + commentsValue);
		assertEquals("Wrong response received for public comment notification", 
				TestDataFactory.getSoapResponse("testPrivateNotificationInvalidString.2"), 
				fixture.getNextRequest().getEntityAsText());
	}
	/**
	 * Test whether issues without a Zendesk ticket ID are handled correctly, eg. no Zendesk notification should be 
	 * generated.
	 */
	@Test (groups = {"regressionTests"} )
	public void testNonZendeskassociatedIssueUpdate() throws Exception {
		RemoteIssue newIssue = new RemoteIssue();
		newIssue.setType("1");
		newIssue.setProject(PROJECT_KEY);
		newIssue.setSummary("Non Zendesk issue (No Zendesk ID)");
		newIssue = fixture.createIssue(newIssue);
		
		fixture.updateIssueWithDescription(newIssue.getKey(), 
				"This issue doesn't have a Zendesk ID associated, no notification should be set");
		assertEquals("Notification received for update of issue with a defined Zendesk ticket ID, ", 
				null, fixture.getNextRequest());
	}
	
	/**
	 * No notification should be set to Zendesk if the 'Viewable by' setting is different from 'All users' when adding a comment.
	 */
	@Test (groups = {"regressionTests"} )
	public void testRestrictedViewabilityComment() throws Exception {
		String commentLevel = "Developers";
		String comment = "This issue has restricted 'Viewable by' access, no notification shold be dispatched";
		log.info("Adding comment "+ comment + "with commentLevel "+ commentLevel+" to issue "+issueKey);
		fixture.jiraClient.getService().addComment(fixture.jiraClient.getToken(), issueKey, new RemoteComment(null, comment, null, null, null, commentLevel, null, null));
		assertEquals("Notification received for addition of comment with limited viewability, ", null, fixture.getNextRequest());
	}
	
	@Test (groups = {"regressionTests"} )
	public void testRestrictedViewabilityCommentOnIssueEdit() throws Exception {
		String commentLevel = "Developers";
		String comment = "This issue has restricted 'Viewable by' access, no notification shold be dispatched";
		log.info("Adding comment "+ comment + "with commentLevel "+ commentLevel+" to issue "+issueKey);
		fixture.jiraClient.getService().addComment(fixture.jiraClient.getToken(), issueKey, new RemoteComment(null, comment, null, null, null, commentLevel, null, null));
		assertEquals("Notification received for addition of comment with limited viewability, ", null, fixture.getNextRequest());
	}
	
	@Test (groups = {"regressionTests"} )
	public void testStripTrailingSlashFromZendeskUrl() throws Exception {
		getFixture().getJiraClient().setZendeskUrl("http://localhost:8182/");
		fixture.updateIssueWithComment(issueKey, "Test comment");
		assertEquals("Wrong response received after changing comment", TestDataFactory.getSoapResponse("testCommentAddedNotification.1"), fixture.getNextRequest().getEntityAsText());
	}
}

package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import it.org.agilos.zendesk_jira_plugin.integrationtest.RestServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.RemoteComment;
import org.agilos.jira.soapclient.RemoteCustomFieldValue;
import org.agilos.jira.soapclient.RemoteFieldValue;
import org.agilos.jira.soapclient.RemoteIssue;
import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.HttpsNotificationHandler;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationHandler;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationListener;
import org.apache.log4j.Logger;
import org.restlet.data.Protocol;
import org.restlet.data.Request;

import com.atlassian.jira.issue.IssueFieldConstants;

public class NotificationFixture extends JIRAFixture {

	private Logger log = Logger.getLogger(NotificationFixture.class.getName());

	private static final NotificationListener notificationListener = new NotificationListener(NotificationHandler.class);
	private static final NotificationListener httpsNotificationListener = new NotificationListener(HttpsNotificationHandler.class);
	public static final String MESSAGELIST = "Messagelist";	
	private String ticketID ="215";
	
	private static final RestServer httpServer = new RestServer();
	private static final RestServer httpsServer = new RestServer();
	
	static {
		httpServer.setListener(notificationListener, Protocol.HTTP, 8182);	
		httpsServer.setListener(httpsNotificationListener, Protocol.HTTPS, 8183);
	}

	public RemoteIssue createIssue(String project) throws Exception {
		RemoteIssue newIssue = new RemoteIssue();
		newIssue.setType("1");
		newIssue.setProject(project);
		newIssue.setSummary("TestIssue");
		RemoteCustomFieldValue[] customFieldValues = new RemoteCustomFieldValue[] { new RemoteCustomFieldValue("customfield_10000","", new String[] { ticketID })};
		newIssue.setCustomFieldValues(customFieldValues);
		RemoteIssue createdIssue = jiraClient.getService().createIssue(jiraClient.getToken(), newIssue);
		log.info("Created issue: "+createdIssue.getId()+" "+createdIssue.getSummary());
		return createdIssue;
	}

	public void updateIssueWithSummary(String issueKey, String summary)throws Exception {
		log.info("Changing summery on issue "+ issueKey + " to "+summary);
		jiraClient.getService().updateIssue(jiraClient.getToken(), issueKey, new RemoteFieldValue[] { 
			new RemoteFieldValue(IssueFieldConstants.SUMMARY, new String[] { summary } ) });		
	}

	public void updateIssueWithDescription (String issueKey, String description) throws Exception {
		log.info("Changing description on issue "+ issueKey + " to "+description);
		jiraClient.getService().updateIssue(jiraClient.getToken(), issueKey, new RemoteFieldValue[] { 
			new RemoteFieldValue(IssueFieldConstants.DESCRIPTION, new String[] { description } ) });
	}

	public void updateIssueWithDescriptionAndComment (String issueKey, String description, String comment) throws Exception {
		log.info("Changing description on issue "+ issueKey + " to "+description +" and adding comment: "+comment);
		jiraClient.getService().updateIssue(jiraClient.getToken(), issueKey, new RemoteFieldValue[] { 
			//new RemoteFieldValue(IssueFieldConstants.DESCRIPTION, new String[] { description }),
			new RemoteFieldValue(IssueFieldConstants.COMMENT, new String[] { comment } ) });
	}

	public void updateIssueWithComment (String issueKey, String comment) throws Exception {
		log.info("Adding comment "+ comment + " to issue "+issueKey);
		jiraClient.getService().addComment(jiraClient.getToken(), issueKey, new RemoteComment(null, comment, null, null, null, null, null, null));
	}
	
	public void setIssueToResolved(String issueKey) throws Exception {
		log.info("Setting issue "+issueKey +" InProgress  ");
		jiraClient.getService().progressWorkflowAction(jiraClient.getToken(), issueKey, "5", null);
	}

	/**
	 * Polls the notification listener for the next message for 10 seconds, and returns this. 
	 * If no notifications has been received during the 10 seconds, null is returned
	 */
	public Request getNextRequest() {
		Request request = null;
		try {
			request = NotificationHandler.getMessageQueue().poll(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("No response received in 10 seconds");
		}
		return request; 
	}
	

	public Request getNextHttpsRequest() {
		Request request = null;
		try {
			request = HttpsNotificationHandler.getMessageQueue().poll(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("No response received in 10 seconds");
		}
		return request; 
	} 
	
	/**
	 * Return the next notification received or null if the queue is empty. 
	 */
	public Request getNextRequestInstant() {
		return NotificationHandler.getMessageQueue().poll();
	}

	/**
	 * 
	 * @param issueKey
	 * @param newProjectName The project the issue should be moved to.
	 * @return The new issueKey
	 */
	public String moveIssue(String issueKey, RemoteProject newProjectName) {
		tester.gotoPage("browse/"+issueKey);
		tester.clickLink("move_issue");
		
		tester.assertTextPresent("Move Issue: "+issueKey);
		tester.setWorkingForm("jiraform");
		tester.selectOption("pid", newProjectName.getName());
		tester.submit();
		
		tester.assertTextPresent("Move Issue: Update Fields");
		tester.setWorkingForm("jiraform");
		tester.submit();

		tester.setWorkingForm("jiraform");
		tester.assertTextPresent("Move Issue: Confirm"); 
		tester.submit();

		tester.assertTitleEquals("");
		return null;
	}
}

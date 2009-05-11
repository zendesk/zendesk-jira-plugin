package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import it.org.agilos.zendesk_jira_plugin.integrationtest.RestServer;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.agilos.jira.soapclient.RemoteComment;
import org.agilos.jira.soapclient.RemoteCustomFieldValue;
import org.agilos.jira.soapclient.RemoteFieldValue;
import org.agilos.jira.soapclient.RemoteIssue;
import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationListener;
import org.apache.log4j.Logger;
import org.restlet.data.Request;

import com.atlassian.jira.issue.IssueFieldConstants;

public class NotificationFixture extends JIRAFixture {

	private Logger log = Logger.getLogger(NotificationFixture.class.getName());

	private final NotificationListener notificationListener = new NotificationListener();
	public static final String MESSAGELIST = "Messagelist";	
	private String ticketID ="215";

	public NotificationFixture() 
	throws ServiceException, RemoteException, MalformedURLException {
		RestServer.getInstance().setListener(notificationListener);
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
		jiraClient.getService().updateIssue(jiraClient.getToken(), issueKey, new RemoteFieldValue[] { new RemoteFieldValue(IssueFieldConstants.SUMMARY, new String[] { summary } ) });		
	}
	
	public void updateIssueWithDescription (String issueKey, String description) throws Exception {
		log.info("Changing description on issue "+ issueKey + " to "+description);
		jiraClient.getService().updateIssue(jiraClient.getToken(), issueKey, new RemoteFieldValue[] { new RemoteFieldValue(IssueFieldConstants.DESCRIPTION, new String[] { description } ) });
	}
	
	public void updateIssueWithDescriptionAndComment (String issueKey, String description, String comment) throws Exception {
		log.info("Changing description on issue "+ issueKey + " to "+description);
		jiraClient.getService().updateIssue(jiraClient.getToken(), issueKey, new RemoteFieldValue[] { 
				new RemoteFieldValue("description", new String[] { description }),
				new RemoteFieldValue("comment", new String[] { description } ) });
	}

	public void updateIssueWithComment (String issueKey, String comment) throws Exception {
		log.info("Adding comment "+ comment + " to issue "+issueKey);
		jiraClient.getService().addComment(jiraClient.getToken(), issueKey, new RemoteComment(null, comment, null, null, null, null, null, null));
	}
	
	public Request getNextRequest() {
		Request nextRequest = notificationListener.getNextRequest();
		if (nextRequest == null) throw new RuntimeException("No notification received");
		return nextRequest;
	}

	/**
	 * 
	 * @param issueKey
	 * @param projectKey
	 * @return The new issueKey
	 */
	public String moveIssue(String issueKey, RemoteProject newProjectName) {
		try {
			tester.gotoPage("browse/"+issueKey);
			tester.clickLink("move_issue");
			tester.assertTextPresent("Move Issue: "+issueKey);
			
			tester.setWorkingForm("jiraform");
			tester.selectOption("project", newProjectName.getName());
			tester.submit();
			
			tester.assertTextPresent("Move Issue: Update Fields");
			tester.submit();
			
			tester.assertTextPresent("Move Issue: Confirm"); 
			tester.submit();
			
			tester.assertTitleEquals("");
			
		} catch (AssertionFailedError e) {
			throw new AssertionError("Failed to move issue");
		}
		return null;
	} 
}

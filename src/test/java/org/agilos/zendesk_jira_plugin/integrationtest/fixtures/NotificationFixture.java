package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import it.org.agilos.zendesk_jira_plugin.integrationtest.RestServer;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.RemoteComment;
import org.agilos.jira.soapclient.RemoteCustomFieldValue;
import org.agilos.jira.soapclient.RemoteFieldValue;
import org.agilos.jira.soapclient.RemoteIssue;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationListener;
import org.apache.log4j.Logger;
import org.restlet.data.Request;

import com.atlassian.jira.issue.IssueFieldConstants;

public class NotificationFixture extends JIRAFixture {

	private Logger log = Logger.getLogger(NotificationFixture.class.getName());

	private final NotificationListener notificationListener = new NotificationListener();
	public static final String MESSAGELIST = "Messagelist";	
	private String ticketID ="215";

	public NotificationFixture() throws ServiceException, RemoteException, MalformedURLException {
		this(JIRA_URL, LOGIN_NAME, LOGIN_PASSWORD);
	}

	public NotificationFixture(String jiraUrl, String loginName, String loginPassword) 
	throws ServiceException, RemoteException, MalformedURLException {
		super(jiraUrl, loginName, loginPassword);

		RestServer.getInstance().setListener(notificationListener);
	}

	public RemoteIssue createIssue() throws Exception {
		RemoteIssue newIssue = new RemoteIssue();
		newIssue.setType("1");
		newIssue.setProject(PROJECT_KEY);
		newIssue.setSummary("TestIssue");
		RemoteCustomFieldValue[] customFieldValues = new RemoteCustomFieldValue[] { new RemoteCustomFieldValue("customfield_10000","", new String[] { ticketID })};
		newIssue.setCustomFieldValues(customFieldValues);
		RemoteIssue createdIssue = jiraSoapService.createIssue(jiraSoapToken, newIssue);
		log.info("Created issue: "+createdIssue.getId()+" "+createdIssue.getSummary());
		return createdIssue;
	}

	public void updateIssueWithSummary(String issueKey, String summary)throws Exception {
		log.info("Changing summery on issue "+ issueKey + " to "+summary);
		jiraSoapService.updateIssue(jiraSoapToken, issueKey, new RemoteFieldValue[] { new RemoteFieldValue(IssueFieldConstants.SUMMARY, new String[] { summary } ) });		
	}
	
	public void updateIssueWithDescription (String issueKey, String description) throws Exception {
		log.info("Changing description on issue "+ issueKey + " to "+description);
		jiraSoapService.updateIssue(jiraSoapToken, issueKey, new RemoteFieldValue[] { new RemoteFieldValue(IssueFieldConstants.DESCRIPTION, new String[] { description } ) });
	}
	
	public void updateIssueWithDescriptionAndComment (String issueKey, String description, String comment) throws Exception {
		log.info("Changing description on issue "+ issueKey + " to "+description);
		jiraSoapService.updateIssue(jiraSoapToken, issueKey, new RemoteFieldValue[] { 
				new RemoteFieldValue("description", new String[] { description }),
				new RemoteFieldValue("comment", new String[] { description } ) });
	}

	public void updateIssueWithComment (String issueKey, String comment) throws Exception {
		log.info("Adding comment "+ comment + " to issue "+issueKey);
		jiraSoapService.addComment(jiraSoapToken, issueKey, new RemoteComment(null, comment, null, null, null, null, null, null));
	}
	
	public Request getNextRequest() {
		return notificationListener.getNextRequest();
	}

	/**
	 * 
	 * @param issueKey
	 * @param projectKey
	 * @return The new issueKey
	 */
	public String moveIssue(String issueKey, String projectKey) {
		return null;
	}
}

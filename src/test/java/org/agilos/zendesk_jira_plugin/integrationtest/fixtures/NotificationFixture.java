package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import it.org.agilos.zendesk_jira_plugin.integrationtest.ZendeskServerStub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.agilos.jira.soapclient.RemoteComment;
import org.agilos.jira.soapclient.RemoteCustomFieldValue;
import org.agilos.jira.soapclient.RemoteFieldValue;
import org.agilos.jira.soapclient.RemoteIssue;
import org.agilos.jira.soapclient.RemoteProject;
import org.apache.log4j.Logger;
import org.restlet.data.Request;

import com.atlassian.jira.issue.IssueFieldConstants;

public class NotificationFixture extends JIRAFixture {

	private static final Logger log = Logger.getLogger(NotificationFixture.class.getName());
	
	private String ticketID ="215";

	protected static LinkedBlockingQueue<Request> httpNotificationQueue = new LinkedBlockingQueue<Request>();
	protected static LinkedBlockingQueue<Request> httpsNotificationQueue= new LinkedBlockingQueue<Request>();
	
	private static final ZendeskServerStub httpServer = new ZendeskServerStub(httpNotificationQueue, 8182);
	private static final ZendeskServerStub httpsServer = new ZendeskServerStub(httpsNotificationQueue, 8183);

	static {
		try {
			httpServer.start();
			httpsServer.start();
		} catch (Exception e) {
			log.error("Failed to start Zendesk test stub", e);
		}
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

	public void updateIssueWithAttachment(String issueKey, String[] names, File attachmentFiles[]) throws IOException {
		byte[][] attachments = new byte[names.length][];
		for (int attachmentInterator = 0; attachmentInterator < names.length ; attachmentInterator++) {
			attachments[attachmentInterator] = getBytesArrayFile(attachmentFiles[attachmentInterator]);

			log.info("Adding attachment "+attachmentFiles[attachmentInterator].getAbsolutePath() +" to issue "+issueKey + " as "+names[attachmentInterator]);
		}
		jiraClient.getService().addAttachmentsToIssue(jiraClient.getToken(), issueKey, names, attachments);
	}

	/**
	 * Polls the notification listener for the next message for 10 seconds, and returns this. 
	 * If no notifications has been received during the 10 seconds, null is returned
	 */
	public Request getNextRequest() {
		Request request = null;
		try {
			request = httpNotificationQueue.poll(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("No response received in 10 seconds");
		}
		return request; 
	}


	public Request getNextHttpsRequest() {
		Request request = null;
		try {
			request = httpsNotificationQueue.poll(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("No response received in 10 seconds");
		}
		return request; 
	} 

	/**
	 * Return the next notification received or null if the queue is empty. 
	 */
	public Request getNextRequestInstant() {
		return httpNotificationQueue.poll();
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

	private static byte[] getBytesArrayFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();	        

		return bytes;
	}
}

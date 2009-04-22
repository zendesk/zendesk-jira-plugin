package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.RemoteComment;
import org.agilos.jira.soapclient.RemoteIssue;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationListener;
import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class NotificationFixture extends JIRAFixture {
	
	private Logger log = Logger.getLogger(NotificationFixture.class.getName());
	
	private final NotificationListener notificationListener = new NotificationListener();
	public static final String MESSAGELIST = "Messagelist";	
	
	public NotificationFixture() throws ServiceException, RemoteException, MalformedURLException {
		this(JIRA_URL, LOGIN_NAME, LOGIN_PASSWORD);
}

public NotificationFixture(String jiraUrl, String loginName, String loginPassword) throws ServiceException, RemoteException, MalformedURLException {
	super(jiraUrl, loginName, loginPassword);

		 try {
		        // Create a new Component.
		        Component component = new Component();

		        // Add a new HTTP server listening on port 8182.
		        log.info("Adding rest server on port 8182");
		        component.getServers().add(Protocol.HTTP, 8182);

		        // Attach the sample application.
		        log.info("Adding Notification listener");
		        component.getDefaultHost().attach(notificationListener);

		        // Start the component.
		        log.info("Starting rest server");
		        component.start();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	}
	
	public RemoteIssue createIssue() throws Exception {
		    RemoteIssue newIssue = new RemoteIssue();
		    newIssue.setType("1");
		    newIssue.setProject(PROJECT_KEY);
		    newIssue.setSummary("TestIssue");
			RemoteIssue createdIssue = jiraSoapService.createIssue(jiraSoapToken, newIssue);
			log.info("Created issue: "+createdIssue.getId()+" "+createdIssue.getSummary());
			return createdIssue;
	}
	
	public void updateIssueWithComment (String issueKey, String comment) throws Exception {
		jiraSoapService.addComment(jiraSoapToken, issueKey, new RemoteComment(null, comment, null, null, null, null, null, null));
	}
	
	public String getNextRequest() {
		return notificationListener.getNextRequest();
	}
}

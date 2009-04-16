package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.RemotePermissionScheme;
import org.agilos.jira.soapclient.RemoteProjectRole;
import org.agilos.jira.soapclient.RemoteUser;
import org.agilos.zendesk_jira_plugin.integrationtest.ZendeskWSClient;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

public class WebserviceFixture extends JIRAFixture {
	static final String PROJECT_KEY = "TST";
	static final String PROJECT_NAME = "Test Project";
	static final String PROJECT_DESCRIPTION = "This is a Zendesk JIRA plugin integrationtest project " + new Date();
	static final String PROJECT_LEAD = "bamboo";

	protected ZendeskWSClient zendeskWSClient;
	
	private Logger log = Logger.getLogger(WebserviceFixture.class.getName());

	public WebserviceFixture() throws ServiceException, RemoteException, MalformedURLException {
			this(JIRA_URL, LOGIN_NAME, LOGIN_PASSWORD);
	}
	
	public WebserviceFixture(String jiraUrl, String loginName, String loginPassword) throws ServiceException, RemoteException, MalformedURLException {
		super(jiraUrl, loginName, loginPassword);

		zendeskWSClient = new ZendeskWSClient(jiraUrl, loginName, loginPassword);
		
		cleanData();
	}

	public void createProject() throws Exception {
		project = jiraSoapService.createProject(jiraSoapToken, PROJECT_KEY, PROJECT_NAME, PROJECT_DESCRIPTION, null, PROJECT_LEAD, new RemotePermissionScheme(null, new Long(0), null, null, null), null, null);
		log.info("Created project: "+project);
	}

	public void removeProject() throws Exception {
		jiraSoapService.deleteProject(jiraSoapToken, PROJECT_KEY);
	}

	public void createUserWithUsername(String name) throws Exception  {
		jiraSoapService.createUser(jiraSoapToken, name, name+"-pw", "Test User "+name, name+"@nowhere.test");
	}

	public void removeUser(String name) throws Exception  {
		jiraSoapService.deleteUser(jiraSoapToken, name);
	}

	public void assignUserToProject(String name) throws Exception {
		RemoteProjectRole developerRole = jiraSoapService.getProjectRoles(jiraSoapToken)[1];
		jiraSoapService.getProjectRoleActors(jiraSoapToken, developerRole, project); 
		jiraSoapService.addActorsToProjectRole(jiraSoapToken, new String[] { name }, new RemoteProjectRole(null, new Long(83), null), project, "atlassian-user-role-actor"); 
	}

	public void unassignUserFromProject(String name) throws Exception  {
		throw new NotImplementedException();
	}

	public RemoteUser[] assignableUsers() throws Exception {
		return zendeskWSClient.assignableUsers(PROJECT_KEY);
	}

	//ToDO Duplicate of WebserviceTest code, clean up
    private static final String user1 = "brian";
    private static final String user2 = "ole";

	//Needs to exterminate all data before each test to ensure a stable test environment
	private void cleanData() {
		try {
    	removeProject();
		} catch (Exception e) {}
		try {
		removeUser(user1);
		} catch (Exception e) {}
		try {
			removeUser(user2);
		} catch (Exception e) {}
	}
}

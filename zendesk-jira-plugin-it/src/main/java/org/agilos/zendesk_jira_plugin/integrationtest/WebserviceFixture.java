package org.agilos.zendesk_jira_plugin.integrationtest;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.*;
import org.apache.commons.lang.NotImplementedException;

public class WebserviceFixture {
	private final JiraSoapService jiraSoapService;
	private String token;
	private RemoteProject project;

	static final String LOGIN_NAME = "bamboo";
	static final String LOGIN_PASSWORD = "bamboo2997";

	static final String JIRA_URL = "http://192.168.0.100:8080";
	static final String PROJECT_KEY = "TST";
	static final String PROJECT_NAME = "Test Project";
	static final String PROJECT_DESCRIPTION = "This is a Zendesk JIRA plugin integrationtest project " + new Date();
	static final String PROJECT_LEAD = "bamboo";

	public WebserviceFixture() throws ServiceException,
			RemoteAuthenticationException,
			org.agilos.jira.soapclient.RemoteException, RemoteException {
		JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
		jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2();
		token = jiraSoapService.login(LOGIN_NAME, LOGIN_PASSWORD);
	}

	public void createProject() throws Exception {
		project = jiraSoapService.createProject(token, PROJECT_KEY, PROJECT_NAME, PROJECT_DESCRIPTION, null, PROJECT_LEAD, new RemotePermissionScheme(null, new Long(0), null, null, null), null, null);	
	}

	public void removeProject() throws Exception {
		jiraSoapService.deleteProject(token, PROJECT_KEY);
	}

	public void addUser(String name) throws Exception  {
		jiraSoapService.createUser(token, name, name+"-pw", "Test User "+name, name+"@nowhere.test");
	}

	public void removeUser(String name) throws Exception  {
		jiraSoapService.deleteUser(token, name);
	}

	public void assignUserToProject(String name) throws Exception {
		RemoteProjectRole developerRole = jiraSoapService.getProjectRoles(token)[1];
		jiraSoapService.getProjectRoleActors(token, developerRole, project); 
		jiraSoapService.addActorsToProjectRole(token, new String[] { name }, new RemoteProjectRole(null, new Long(83), null), project, "atlassian-user-role-actor"); 
	}

	public void unassignUserFromProject(String name) throws Exception  {
		throw new NotImplementedException();
	}

	public Set getAssignableUsers() throws Exception {
		throw new NotImplementedException();
	}
}

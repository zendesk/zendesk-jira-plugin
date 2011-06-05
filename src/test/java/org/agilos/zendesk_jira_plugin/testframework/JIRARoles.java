package org.agilos.zendesk_jira_plugin.testframework;

import java.util.HashMap;
import java.util.Map;

import org.agilos.jira.soapclient.RemoteException;
import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.jira.soapclient.RemoteProjectRole;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

public abstract class JIRARoles {
	public static final String Administrators = "Administrators";
	public static final String Developers = "Developers";
	public static final String Users = "Users";

	public enum Roles { jiraadministrators, jiradevelopers, jirausers }//Define default groups

	private static Logger log = Logger.getLogger(JIRARoles.class.getName());

	private static final Map<String, RemoteProjectRole> roleMap = new HashMap<String, RemoteProjectRole>();
	private static JIRAClient jiraClient = JIRAClient.instance();

	public static RemoteProjectRole getRole(String name) {
		if (roleMap.isEmpty()) retrieveRoles();
		return roleMap.get(name);
	}

	public static void addUserToProjectRoleInProject(String userName, String projectRole, RemoteProject project) throws RemoteException, java.rmi.RemoteException {
		jiraClient.getService().addActorsToProjectRole(jiraClient.getToken(), new String[] { userName }, getRole(projectRole), project, "atlassian-user-role-actor");
	}

	public static void removeUserFromProjectRoleInProject(String username, String projectRole, RemoteProject project) throws RemoteException, java.rmi.RemoteException {
		jiraClient.getService().removeActorsFromProjectRole(jiraClient.getToken(), new String[] { username }, getRole(projectRole), project, "atlassian-user-role-actor");
	}

	/**
	 * Always use this to add new roles, else the local roles cache will become obsolete
	 */
	public static void addRole() {
		throw new NotImplementedException();
	}

	public static void retrieveRoles() {
		try {
			RemoteProjectRole[] roles = jiraClient.getService().getProjectRoles(jiraClient.getToken());
			for (int i=0;i<roles.length;i++) {
				roleMap.put(roles[i].getName(), roles[i]);
			}
		} catch (Exception e) {
			log.error("Unable to retieve project roles");
		} 
	}
}

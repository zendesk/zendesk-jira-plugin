package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import it.org.agilos.zendesk_jira_plugin.integrationtest.JIRARoles;
import it.org.agilos.zendesk_jira_plugin.integrationtest.ZendeskWSClient;

import org.agilos.jira.soapclient.RemoteUser;
import org.apache.log4j.Logger;

public class WebserviceFixture extends JIRAFixture {
	protected ZendeskWSClient zendeskWSClient;
	
	private Logger log = Logger.getLogger(WebserviceFixture.class.getName());

	@Override
	public void connect() {
		super.connect();
		try {
			zendeskWSClient = new ZendeskWSClient();
		} catch (Exception e) {
			log.error("Failed to create ZendeskWSClient", e);
		}
	}

	public void assignUserToProject(String username, String projectKey) throws Exception {
		log.info("Assigning user: "+username+" to project: ");
		JIRARoles.addUserToProjectRoleInProject(username, JIRARoles.Developers, project); 
	}

	public void unassignUserFromProject(String username) throws Exception  {
		log.info("Unassigning user from project: "+username+" to project: ");
		JIRARoles.removeUserFromProjectRoleInProject(username, JIRARoles.Developers, project);  
	}

	public RemoteUser[] assignableUsers(String projectKey) throws Exception {
		return zendeskWSClient.assignableUsers(projectKey);
	}
}

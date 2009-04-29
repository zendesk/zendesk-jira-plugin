package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.util.HashMap;
import java.util.Map;

import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.jira.soapclient.RemoteUser;
import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;
import org.apache.log4j.Logger;

/**
 * Provides a user friendly remote access to JIRA based on the functionality exposed through the JIRA SOAP service.
 * 
 * The main contributions are:
 * <ul>
 * <li> Hides the Service retrievel , login and token passing boilerplating (Using the {@link JIRAClient} class).
 * <li> Attempts to hide the requirement to send full remote object in remote calls, using simple string keys instead.
 * <li> Together with the JIRAxxxxxHelper classes exposes the possible values to many of the String input parameters to the SOAP operations, like roles, parameter keys etc.
 * </ul> 
 */
public abstract class JIRAFacade {
	private final static Map<String, RemoteUser> userMap = new HashMap<String, RemoteUser>();
	private final static Map<String, RemoteProject> projectMap = new HashMap<String, RemoteProject>();
	
	private final static JIRAClient jiraClient = JIRAClient.instance();

	private final static Logger log = Logger.getLogger(JIRAFixture.class.getName());

}

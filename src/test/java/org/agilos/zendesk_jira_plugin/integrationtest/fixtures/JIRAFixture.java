package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.JiraSoapService;
import org.agilos.jira.soapclient.JiraSoapServiceService;
import org.agilos.jira.soapclient.JiraSoapServiceServiceLocator;
import org.agilos.jira.soapclient.RemotePermissionScheme;
import org.agilos.jira.soapclient.RemoteProject;
import org.apache.log4j.Logger;

/**
 * Parent fixture for access to a JIRA instance.
 */
public class JIRAFixture {
	protected JiraSoapService jiraSoapService;
	protected String jiraSoapToken;
	protected RemoteProject project;

	static final String LOGIN_NAME = "bamboo";
	static final String LOGIN_PASSWORD = "bamboo2997";

	static final String JIRA_URL = "http://192.168.0.100:8080";
	protected static final String PROJECT_KEY = "TST";
	protected static final String PROJECT_NAME = "Test Project";
	protected static final String PROJECT_DESCRIPTION = "This is a Zendesk JIRA plugin integrationtest project " + new Date();

	private Logger log = Logger.getLogger(JIRAFixture.class.getName());
	
	public JIRAFixture(String jiraUrl, String loginName, String loginPassword) throws ServiceException, RemoteException, MalformedURLException {
		URL jiraSOAPServiceUrl = new URL(jiraUrl+"/rpc/soap/jirasoapservice-v2");
		JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
		log.debug("Retriving jira soap service from "+jiraSOAPServiceUrl);
		jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2(jiraSOAPServiceUrl);
		log.debug("Logging in with user: "+loginName+" and password: "+loginPassword);
		jiraSoapToken = jiraSoapService.login(loginName, loginPassword);		
	}
	
	public RemoteProject createProject(String ProjectLead) throws Exception {
		project = jiraSoapService.createProject(jiraSoapToken, PROJECT_KEY, PROJECT_NAME, PROJECT_DESCRIPTION, null, ProjectLead, new RemotePermissionScheme(null, new Long(0), null, null, null), null, null);
		log.info("Created project: "+project);
		return project;
	}

	public void removeProject() throws Exception {
		jiraSoapService.deleteProject(jiraSoapToken, PROJECT_KEY);
	}
}

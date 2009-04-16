package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.JiraSoapService;
import org.agilos.jira.soapclient.JiraSoapServiceService;
import org.agilos.jira.soapclient.JiraSoapServiceServiceLocator;
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

	private Logger log = Logger.getLogger(JIRAFixture.class.getName());
	
	public JIRAFixture(String jiraUrl, String loginName, String loginPassword) throws ServiceException, RemoteException, MalformedURLException {
		JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
		log.debug("Retriving jira soap service from "+new URL(jiraUrl));
		jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2(new URL(jiraUrl));
		log.debug("Logging in with user: "+loginName+" and password: "+loginPassword);
		jiraSoapToken = jiraSoapService.login(loginName, loginPassword);		
	}
}

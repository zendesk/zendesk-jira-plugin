package org.agilos.zendesk_jira_plugin.integrationtest;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.AgilosSoapService;
import org.agilos.jira.soapclient.AgilosSoapServiceService;
import org.agilos.jira.soapclient.AgilosSoapServiceServiceLocator;
import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.jira.soapclient.RemoteUser;
import org.apache.log4j.Logger;

public class ZendeskWSClient {
	private final AgilosSoapService agilosSoapService;
	private String agilosSoapToken;
	
	private final String jiraUrl;
	private final String loginName;
	private final String loginPassword;

	private Logger log = Logger.getLogger(ZendeskWSClient.class.getName());

	public ZendeskWSClient(String jiraUrl, String loginName, String loginPassword) throws ServiceException, RemoteException, MalformedURLException {
		this.jiraUrl = jiraUrl;
		this.loginName = loginName;
		
		this.loginPassword = loginPassword;
		AgilosSoapServiceService agilosSoapServiceGetter = new AgilosSoapServiceServiceLocator();
		log.debug("Retriving jira soap service from "+new URL(jiraUrl));
		agilosSoapService = agilosSoapServiceGetter.getAgilossoapserviceV1();
		log.debug("Logging in with user: "+loginName+" and password: "+loginPassword);
		agilosSoapToken = agilosSoapService.login(loginName, loginPassword);
	}
	
	public RemoteUser[] assignableUsers(String projectKey) throws Exception {
		return agilosSoapService.getAssignableUsers(agilosSoapToken, projectKey);
	}
}

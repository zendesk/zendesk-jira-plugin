package org.agilos.zendesk_jira_plugin.integrationtest;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.AgilosSoapService;
import org.agilos.jira.soapclient.AgilosSoapServiceService;
import org.agilos.jira.soapclient.AgilosSoapServiceServiceLocator;
import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.jira.soapclient.RemoteUser;

public class ZendeskWSClient {
	private final AgilosSoapService agilosSoapService;
	private String agilosSoapToken;
	
	private final String jiraUrl;
	private final String loginName;
	private final String loginPassword;

	public ZendeskWSClient(String jiraUrl, String loginName, String loginPassword) throws ServiceException, RemoteException {
		this.jiraUrl = jiraUrl;
		this.loginName = loginName;
		
		this.loginPassword = loginPassword;
		AgilosSoapServiceService agilosSoapServiceGetter = new AgilosSoapServiceServiceLocator();
		agilosSoapService = agilosSoapServiceGetter.getAgilossoapserviceV1();
		agilosSoapToken = agilosSoapService.login(loginName, loginPassword);
	}
	
	public RemoteUser[] assignableUsers(String projectKey) throws Exception {
		return agilosSoapService.getAssignableUsers(agilosSoapToken, projectKey);
	}
}

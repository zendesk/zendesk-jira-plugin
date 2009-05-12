package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.AgilosSoapService;
import org.agilos.jira.soapclient.AgilosSoapServiceService;
import org.agilos.jira.soapclient.AgilosSoapServiceServiceLocator;
import org.agilos.jira.soapclient.RemoteUser;
import org.apache.log4j.Logger;

public class ZendeskWSClient {
	private final AgilosSoapService agilosSoapService;
	private String agilosSoapToken;

	private Logger log = Logger.getLogger(ZendeskWSClient.class.getName());

	public ZendeskWSClient() throws ServiceException, RemoteException, MalformedURLException {		
		String jiraUrl = System.getProperty("zendesk.jira.url","http://localhost:1990/jira");
		String loginName = System.getProperty("zendesk.jira.login.name", "bamboo");
		String loginPassword = System.getProperty("zendesk.jira.login.password","bamboo2997");
		
		AgilosSoapServiceService agilosSoapServiceGetter = new AgilosSoapServiceServiceLocator();
		URL agilosSOAPServiceUrl = new URL(jiraUrl+"/rpc/soap/agilossoapservice-v1");
		log.debug("Retriving jira soap service from "+agilosSOAPServiceUrl);
		agilosSoapService = agilosSoapServiceGetter.getAgilossoapserviceV1(agilosSOAPServiceUrl);
		log.debug("Logging in with user: "+loginName+" and password: "+loginPassword);
		agilosSoapToken = agilosSoapService.login(loginName, loginPassword);
	}
	
	public RemoteUser[] assignableUsers(String projectKey) throws Exception {
		return agilosSoapService.getAssignableUsers(agilosSoapToken, projectKey);
	}
}

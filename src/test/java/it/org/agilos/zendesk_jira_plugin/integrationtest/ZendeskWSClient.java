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
	private static AgilosSoapService agilosSoapService;
	private static String agilosSoapToken;

	public static AgilosSoapService getSoapService() {
		return agilosSoapService;
	}

	public static String getSoapToken() {
		return agilosSoapToken;
	}

	private Logger log = Logger.getLogger(ZendeskWSClient.class.getName());

	public ZendeskWSClient() throws ServiceException, RemoteException, MalformedURLException {	
		AgilosSoapServiceService agilosSoapServiceGetter = new AgilosSoapServiceServiceLocator();
		URL agilosSOAPServiceUrl = new URL(JIRAClient.jiraUrl+"/rpc/soap/agilossoapservice-v1");
		log.debug("Retriving jira soap service from "+agilosSOAPServiceUrl);
		agilosSoapService = agilosSoapServiceGetter.getAgilossoapserviceV1(agilosSOAPServiceUrl);
		log.debug("Logging in with user: "+JIRAClient.loginName+" and password: "+JIRAClient.loginPassword);
		agilosSoapToken = agilosSoapService.login(JIRAClient.loginName, JIRAClient.loginPassword);
	}
	
	public RemoteUser[] assignableUsers(String projectKey) throws Exception {
		return agilosSoapService.getAssignableUsers(agilosSoapToken, projectKey);
	}
}

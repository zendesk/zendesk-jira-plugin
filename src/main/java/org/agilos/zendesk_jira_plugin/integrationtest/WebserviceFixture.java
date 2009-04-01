package org.agilos.zendesk_jira_plugin.integrationtest;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.agilos.jira.soapclient.*;

/**
  * Sample JIRA SOAP client. Note that the constants sit in the {@link ClientConstants} interface
 */
public class WebserviceFixture implements ClientConstants
{
	private final JiraSoapService jiraSoapService;
	private String token;
	
	public WebserviceFixture() throws ServiceException {
		JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
		jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2();
	}
    
	public void loginWithUserAndPassword(String login_name, String password) throws RemoteAuthenticationException, org.agilos.jira.soapclient.RemoteException, RemoteException {
        token = jiraSoapService.login(login_name, password);
	}
	
	public void createProject() {
		
	}
}

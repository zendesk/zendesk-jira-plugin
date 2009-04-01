package org.agilos.jira.ws;

import org.agilos.jira.service.UserService;
import org.agilos.jira.soapclient.RemoteAuthenticationException;
import org.agilos.jira.soapclient.RemoteException;
import org.agilos.jira.soapclient.RemotePermissionException;
import org.agilos.jira.soapclient.RemoteUser;

import com.atlassian.jira.rpc.auth.TokenManager;

public class AgilosSoapServiceImpl implements AgilosSoapService {
	private TokenManager tokenManager;
	private UserService userService;
	
	public AgilosSoapServiceImpl(TokenManager tokenManager, UserService userService)
    {
        this.tokenManager = tokenManager;
        this.userService = userService;
    }
	public RemoteUser[] getAssignableUsers(String token, String projectKey)
			throws RemotePermissionException, RemoteAuthenticationException,
			RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}

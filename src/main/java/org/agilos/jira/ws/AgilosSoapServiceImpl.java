package org.agilos.jira.ws;

import org.agilos.jira.service.UserService;

import com.atlassian.jira.rpc.auth.TokenManager;
import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemoteException;
import com.atlassian.jira.rpc.exception.RemotePermissionException;
import com.atlassian.jira.rpc.soap.beans.RemoteUser;
import com.opensymphony.user.User;

public class AgilosSoapServiceImpl implements AgilosSoapService {
	private TokenManager tokenManager; // The TokenManager functionality is very much inspired by the com.atlassian.jira.rpc.auth.TokenManager
	private UserService userService;
	
	public AgilosSoapServiceImpl(TokenManager tokenManager, UserService userService)
    {
        this.tokenManager = tokenManager;
        this.userService = userService;
    }
	public void setTokenManager(TokenManager tokenManager)
    {
        this.tokenManager = tokenManager;
    }

    /**
     * This ability to resolve a token into a user name is used by the JIRA logging and is not exposed on the SOAP
     * service interface itself.
     *
     * @param token the given out previously via {@link #login}
     * @return the user name behind that token or null if the token is not valid
     * @throws RemotePermissionException 
     * @throws RemoteAuthenticationException 
     */
    public String resolveTokenToUserName(final String token) throws RemoteAuthenticationException, RemotePermissionException
    {
            User user = tokenManager.retrieveUserNoPermissionCheck(token);
            return user == null ? null : user.getName();
    }

    /**
     * This is called to work out which parameter the token is given a method name
     *
     * @param operationName the name of the SOAP operation
     * @return the parameter index of the user token
     */
    public int getTokenParameterIndex(final String operationName)
    {
        return 0;
    }

    public String login(String username, String password) throws RemoteException, com.atlassian.jira.rpc.exception.RemoteAuthenticationException, com.atlassian.jira.rpc.exception.RemoteException
    {
        return tokenManager.login(username, password);
    }

    public boolean logout(String token)
    {
        return tokenManager.logout(token);
    }
    
	public RemoteUser[] getAssignableUsers(String token, String projectKey) {
		return userService.getAssignableUsers(projectKey);
	}
}

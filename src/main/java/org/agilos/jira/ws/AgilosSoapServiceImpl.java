package org.agilos.jira.ws;

import com.atlassian.jira.rpc.auth.TokenManager;
import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemoteException;
import com.atlassian.jira.rpc.exception.RemotePermissionException;
import com.atlassian.jira.rpc.soap.beans.RemoteUser;
import com.opensymphony.user.User;
import org.agilos.jira.service.UserService;
import org.apache.log4j.Logger;

public class AgilosSoapServiceImpl implements AgilosSoapService {
    private TokenManager tokenManager; // The TokenManager functionality is very much inspired by the com.atlassian.jira.rpc.auth.TokenManager
    private UserService userService;

    private Logger log = Logger.getLogger(AgilosSoapServiceImpl.class.getName());

    public AgilosSoapServiceImpl(TokenManager tokenManager, UserService userService) {
        log.error("Starting Zendesk plugin");
        this.tokenManager = tokenManager;
        this.userService = userService;
    }

    public void setTokenManager(TokenManager tokenManager) {
        log.error("Setting token manager");
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
    public String resolveTokenToUserName(final String token) throws RemoteAuthenticationException, RemotePermissionException {
        log.error("Calling resolveTokenToUserName("+token+")");
        User user = tokenManager.retrieveUserNoPermissionCheck(token);
        return user == null ? null : user.getName();

    }

    /**
     * This is called to work out which parameter the token is given a method name
     *
     * @param operationName the name of the SOAP operation
     * @return the parameter index of the user token
     */
    public int getTokenParameterIndex(final String operationName) {
        log.error("Calling getTokenParameterIndex("+operationName+")");
        return 0;
    }

    public String login(String username, String password) throws RemoteException {
        log.error("Calling on token manager: "+tokenManager+" with "+username+":"+password);
        try {
            return tokenManager.login(username, password);
        } catch (RuntimeException e) {
            log.error(e);
            throw new RemoteException(e.getMessage());
        }
    }

    public boolean logout(String token) {
        return tokenManager.logout(token);
    }

    public RemoteUser[] getAssignableUsers(String token, String projectKey) throws RemoteException {
        log.error("Returning assignable users for prject "+projectKey);
        try {
            tokenManager.retrieveUserNoPermissionCheck(token);
            return userService.getAssignableUsers(projectKey);
        } catch (RuntimeException e) {
            log.error(e);
            throw new RemoteException(e);
        }
    }
}

package org.agilos.jira.ws;

import com.atlassian.jira.notification.type.RemoteUser;
import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemoteException;
import com.atlassian.jira.rpc.exception.RemotePermissionException;

public interface AgilosSoapService {
	/**
     * Returns an array of all users which can be assigned to a project.
     *
     * @param token the SOAP authentication token.
     * @param projectKey the key of the requested project
     * @return an array of {@link RemoteUser} objects
     * @throws RemoteException If there was some problem preventing the operation from working.
     * @throws RemotePermissionException If the user is not permitted to perform this operation in this context.
     * @throws RemoteAuthenticationException If the token is invalid or the SOAP session has timed out
     */
    RemoteUser[] getAssignableUsers(String token, String projectKey)
            throws RemotePermissionException, RemoteAuthenticationException, RemoteException;
    
    
    String login(String username, String password) 
    		throws RemoteException, RemoteAuthenticationException, RemoteException;
}

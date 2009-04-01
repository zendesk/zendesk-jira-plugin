package org.agilos.jira.ws;

import org.agilos.jira.soapclient.RemoteAuthenticationException;
import org.agilos.jira.soapclient.RemoteException;
import org.agilos.jira.soapclient.RemotePermissionException;
import org.agilos.jira.soapclient.RemoteUser;

public interface AgilosSoapService {
	/**
     * Returns an array of all user which can be assigned to a project.
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
}

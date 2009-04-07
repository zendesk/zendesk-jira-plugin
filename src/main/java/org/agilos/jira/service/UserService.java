package org.agilos.jira.service;

import com.atlassian.jira.rpc.soap.beans.RemoteUser;

public interface UserService {

	RemoteUser[] getAssignableUsers(String projectKey);

}

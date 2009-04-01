package org.agilos.jira.service;

import com.atlassian.jira.notification.type.RemoteUser;

public interface UserService {

	RemoteUser[] getAssignableUsers(String projectKey);

}

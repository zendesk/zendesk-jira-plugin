package org.agilos.jira.service;

import org.apache.commons.lang.NotImplementedException;

import com.atlassian.jira.notification.type.RemoteUser;

public class UserServiceImpl implements UserService {

	public RemoteUser[] getAssignableUsers(String projectKey) {
		throw new NotImplementedException();
	}
}

package org.agilos.jira.service;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.notification.type.RemoteUser;
import com.atlassian.jira.permission.PermissionContext;
import com.atlassian.jira.permission.PermissionContextFactory;
import com.atlassian.jira.security.Permissions;

public class UserServiceImpl implements UserService {
	private final PermissionContextFactory permissionContextFactory;
	
	public UserServiceImpl(PermissionContextFactory permissionContextFactory) {
		this.permissionContextFactory = permissionContextFactory;
	}
	
	public RemoteUser[] getAssignableUsers(String projectKey) {
		GenericValue project = ManagerFactory.getProjectManager().getProjectByKey(projectKey);
		PermissionContext ctx = permissionContextFactory.getPermissionContext(project);
		return (RemoteUser[])ManagerFactory.getPermissionSchemeManager().getUsers(new Long(Permissions.ASSIGNABLE_USER), ctx).toArray(new RemoteUser[0]);
	}
}

package org.agilos.jira.service;

import java.util.Collection;
import java.util.Iterator;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.permission.PermissionContext;
import com.atlassian.jira.permission.PermissionContextFactory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.rpc.soap.beans.RemoteUser;
import com.atlassian.jira.security.Permissions;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

public class UserServiceImpl implements UserService {
	private final PermissionContextFactory permissionContextFactory;
	private final ProjectManager projectService = ManagerFactory.getProjectManager();
	
	public UserServiceImpl(PermissionContextFactory permissionContextFactory) {
		this.permissionContextFactory = permissionContextFactory;
	}
	
	public RemoteUser[] getAssignableUsers(String projectKey) {
		GenericValue project = projectService.getProjectByKey(projectKey);
		PermissionContext ctx = permissionContextFactory.getPermissionContext(project);
		Collection<User> users = ManagerFactory.getPermissionSchemeManager().getUsers(new Long(Permissions.ASSIGNABLE_USER), ctx);
		RemoteUser[] remoteUsers = new RemoteUser[users.size()];
		Iterator<User> usersIterator = users.iterator();
		int i=0;
		while ( usersIterator.hasNext() ){
			remoteUsers[i++] = getUser(usersIterator.next().getName());
		}
		return remoteUsers;
	}
	
	private RemoteUser getUser(String username)   {
        try {
            return new RemoteUser(UserUtils.getUser(username));
        }
        catch (EntityNotFoundException e) {
            return null;
        }
    }
}

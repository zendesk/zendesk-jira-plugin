package org.agilos.zendesk_jira_plugin.integrationtest.fixtures;

import it.org.agilos.zendesk_jira_plugin.integrationtest.JIRAClient;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.AssertionFailedError;
import net.sourceforge.jwebunit.WebTester;

import org.agilos.jira.soapclient.RemoteException;
import org.agilos.jira.soapclient.RemoteGroup;
import org.agilos.jira.soapclient.RemotePermissionScheme;
import org.agilos.jira.soapclient.RemoteProject;
import org.agilos.jira.soapclient.RemoteUser;
import org.apache.log4j.Logger;

/**
 * Parent fixture for access to a JIRA instance.
 */
public class JIRAFixture {
	protected RemoteProject project;

	Map<String, RemoteUser> userMap = new HashMap<String, RemoteUser>();
	Map<String, RemoteProject> projectMap = new HashMap<String, RemoteProject>();

	protected JIRAClient jiraClient;

	private Logger log = Logger.getLogger(JIRAFixture.class.getName());

	public static final String JIRA_ADMINISTRATORS = "jira-administrators";
	public static final String JIRA_DEVELOPERS = "jira-developers";
	public static final String JIRA_USERS = "jira-users";
	
	private static final char FS = File.separatorChar;
	
	public WebTester tester;
	
	public JIRAFixture() {
		jiraClient = JIRAClient.instance();
		tester = jiraClient.getFuncTestHelperFactory().getTester();
		
	}
	
	/**
	 * Connects to JIRA
	 */
	public void connect() throws Exception {
		jiraClient.login();
	}

	public RemoteProject createProjectWithKeyAndNameAndLead(String key, String projectName, String ProjectLead) throws Exception {
		log.info("Creating project: "+key+" "+projectName);
		try {
			project = jiraClient.getService().createProject(jiraClient.getToken(), key, projectName, "", null, ProjectLead, new RemotePermissionScheme(null, new Long(0), null, null, null), null, null);
		} catch (RemoteException re) { // Let's add the user to the user map in case he already exists. This will cause the teardown to delete the user.
			log.error("Failed to create project "+key, re);
			if( re.getFaultReason().equals("com.atlassian.jira.rpc.exception.RemoteValidationException: A project with that project key already exists.")) {
				projectMap.put(key, null);
			}
			throw re;
		}
		projectMap.put(project.getKey(), project);
		return project;
	}

	public void removeProject(String key) throws Exception {
		log.info("Deleting project: "+key+" "+projectMap.get(key));
		jiraClient.getService().deleteProject(jiraClient.getToken(), key);
	}

	/**
	 * Creates user and adds the user to the developers role (to enable editing permission)
	 * @param name
	 * @throws Exception
	 */
	public void createUserWithUsername(String name) throws Exception  {
		log.info("Creating user: "+name);
		RemoteUser user = null;
		try {
			user = jiraClient.getService().createUser(jiraClient.getToken(), name, name+"-pw", "Test User "+name, name+"@nowhere.test");
		} catch (RemoteException re) { // Let's add the user to the user map in case he already exists. This will cause the teardown to delete the user.
			log.error("Failed to create user "+name, re);
			if( re.getFaultReason().equals("com.atlassian.jira.rpc.exception.RemoteValidationException: user for this name already exists, please choose a different user name")) {
				userMap.put(name, null);
			}
			throw re;
		}
		userMap.put(name, user);		
		log.debug("Adding "+name+" to "+JIRA_DEVELOPERS);
		addUserToGroup(name, JIRA_DEVELOPERS);
	}

	public void removeUser(String name) throws Exception  {
		log.info("Removing user: "+name);
		jiraClient.getService().deleteUser(jiraClient.getToken(), name);
	}

	public void addUserToGroup(String userName, String groupName) throws Exception   {
		log.info("Adding user: "+userName+" to group: "+groupName);
		RemoteGroup group = jiraClient.getService().getGroup(jiraClient.getToken(), groupName);
		RemoteUser user = jiraClient.getService().getUser(jiraClient.getToken(), userName);
		jiraClient.getService().addUserToGroup(jiraClient.getToken(), group, user);		
	}

	public void removeUserFromGroup(String userName, String groupName) throws Exception   {
		log.info("Removing user: "+userName+" from group: "+groupName);
		RemoteGroup group = jiraClient.getService().getGroup(jiraClient.getToken(), groupName);
		RemoteUser user = jiraClient.getService().getUser(jiraClient.getToken(), userName);
		jiraClient.getService().removeUserFromGroup(jiraClient.getToken(), group, user);		
	}

	//Needs to exterminate all data before each test to ensure a stable test environment
	public void cleanData() {
		Iterator<String> projectKeyIterator = projectMap.keySet().iterator();
		while (projectKeyIterator.hasNext()) {
			String projectKey = projectKeyIterator.next();
			try {
				removeProject(projectKey);
			} catch (Exception e) {
				log.error("Failed to clean project: "+projectKey);
			}
		}
		Iterator<String> userIDIterator = userMap.keySet().iterator();
		while (userIDIterator.hasNext()) {
			String userID = userIDIterator.next();
			try {
				removeUser(userID);
			} catch (Exception e) {
				log.error("Failed to clean user: "+userID);
			}
		}
	}
	
	public JIRAClient getJiraClient() {
		return jiraClient;
	}

	/**
	 * Loads data from the indicated backup file into JIRA. The file should be located in the <code>jira.xml.data.location</code> directory indicated in the 
	 * localtest.properties file (Generated during the pre-integration-test phase and located in the test-classes folder ).
	 * 
	 * Handles two cases, either a initial setup of JIRA, or a XML restore in a already configured JIRA.
	 */
	public void loadData (String fileName) { 
		String filePath = jiraClient.getFuncTestHelperFactory().getEnvironmentData().getXMLDataLocation().getAbsolutePath() + FS + fileName;
		String JIRAHomeDir = jiraClient.getFuncTestHelperFactory().getEnvironmentData().getJIRAHomeLocation().getAbsolutePath();
		try	{
			if (tester.getDialog().getResponsePageTitle().indexOf("JIRA installation") != -1) {
				tester.gotoPage("secure/SetupImport!default.jspa");
				tester.setWorkingForm("jiraform");
				tester.setFormElement("filename", filePath);
				tester.setFormElement("indexPath", JIRAHomeDir + FS + "indexes");	
				tester.submit();
				tester.assertTextPresent("Setup is now complete.");
			} else {
				jiraClient.getFuncTestHelperFactory().getNavigation().login("admin", "admin");// GUI login with default user
				tester.gotoPage("secure/admin/XmlRestore!default.jspa");
				tester.setWorkingForm("jiraform");
				tester.setFormElement("filename", filePath);
				tester.submit();
	            tester.assertTextPresent("Your project has been successfully imported");
			} 
			log.info("Restored data from '" + filePath + "'");
		} catch(AssertionFailedError e) {
			if (log.isDebugEnabled()) log.debug("Received unexpected text: "+tester.getDialog().getResponseText());
			throw new RuntimeException("Failed to restore data from "+filePath, e);
		} 
	}
}

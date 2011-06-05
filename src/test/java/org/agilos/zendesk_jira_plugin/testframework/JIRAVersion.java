package org.agilos.zendesk_jira_plugin.testframework;

public class JIRAVersion {
	public static final String JIRA_432 = "4.3.2";
	
	public static String getJiraVersion() {
		return System.getProperty("jira.deploy.version", "4.3.2");
	}
}

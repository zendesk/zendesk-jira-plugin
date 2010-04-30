package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian.issuehandler;

import it.org.agilos.zendesk_jira_plugin.integrationtest.JIRAClient;

/**
 * Creates a version specific issue handler
 */
public abstract class IssueHandlerProvider {
    public static IssueHandler getIssueHandler() {
        String jiraVersion = System.getProperty("jira.deploy.version");

        if(jiraVersion == null || jiraVersion.equals("4.1.1")) {
            return new IssueHandlerJIRA41OrLater();
        } else {
            return new IssueHandler();
        }
    }
}

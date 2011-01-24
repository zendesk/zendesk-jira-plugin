package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian.issuehandler;


/**
 * Creates a version specific issue handler
 */
public abstract class IssueHandlerProvider {
    public static IssueHandler getIssueHandler() {
        String jiraVersion = System.getProperty("jira.deploy.version");

        if(jiraVersion == null || jiraVersion.equals("4.2.2-b589")) {
            return new IssueHandlerJIRA42OrLater();
        } else if(jiraVersion.equals("4.1.1")) {
            return new IssueHandlerJIRA41OrLater();
        } else {
            return new IssueHandler();
        }
    }
}

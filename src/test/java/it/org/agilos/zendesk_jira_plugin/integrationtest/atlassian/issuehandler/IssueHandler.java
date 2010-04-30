package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian.issuehandler;

import it.org.agilos.zendesk_jira_plugin.integrationtest.JIRAClient;
import it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian.FuncTestHelperFactory;
import net.sourceforge.jwebunit.WebTester;

/**
 * The default IssueHandler. Methods may be overloaded in version specific implementations
 */
public class IssueHandler {
    protected WebTester tester = JIRAClient.instance().getFuncTestHelperFactory().getTester();

    public void update() {
        tester.clickButton("Update");
    }

    public void resolve() {
        tester.clickButton("Resolve");
    }
    public void reopen() {
        tester.clickLinkWithText("Edit");
    }

    public void attachFile() {
        tester.clickLink("attach_file");
    }

    public void editIssue() {
        tester.clickLink("edit_issue");
    }
}

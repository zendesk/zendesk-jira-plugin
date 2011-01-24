package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian.issuehandler;

import static org.testng.AssertJUnit.assertEquals;
import it.org.agilos.zendesk_jira_plugin.integrationtest.JIRAClient;
import it.org.agilos.zendesk_jira_plugin.integrationtest.TestDataFactory;
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
    	tester.clickLinkWithText("Resolve Issue");
		tester.setWorkingForm("jiraform");
		tester.assertTextPresent("Resolve Issue");
		tester.clickButton("Resolve");
		tester.assertTextPresent("Resolved");
    }
    public void close() {
    	tester.clickLinkWithText("Close Issue");
		tester.setWorkingForm("jiraform");
    	tester.assertTextPresent("Close Issue");
    	tester.submit();
    	tester.assertTextPresent("Closed");
    }
    public void reopen() {
		tester.clickLinkWithText("Reopen Issue");
		tester.setWorkingForm("jiraform");
		tester.submit();
		tester.assertTextPresent("Reopened");
    }

    public void attachFile() {
        tester.clickLink("attach_file");
    }

    public void editIssue() {
        tester.clickLink("edit_issue");
    }
}

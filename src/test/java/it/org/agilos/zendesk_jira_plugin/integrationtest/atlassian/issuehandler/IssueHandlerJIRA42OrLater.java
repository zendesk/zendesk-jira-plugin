package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian.issuehandler;

/**
 * Contains methods for accessing the new issue UI implemented in JIRA 4.1
 */
public class IssueHandlerJIRA42OrLater extends IssueHandler {
    @Override
    public void update() {
        tester.clickButton("update_submit");
    }

    @Override
    public void resolve() {
    	tester.clickLinkWithText("Resolve Issue");
		tester.setWorkingForm("issue-workflow-transition");
		tester.assertTextPresent("Resolve Issue");
        tester.clickButton("issue-workflow-transition-submit");
		tester.assertTextPresent("Resolved");
    }

    @Override
    public void close() {
    	tester.clickLinkWithText("Close Issue");
    	tester.setWorkingForm("issue-workflow-transition");
    	tester.assertTextPresent("Close Issue");
    	tester.submit();
    	tester.assertTextPresent("Closed");
    }
    
    @Override
    public void reopen() {
		tester.clickLinkWithText("Reopen Issue");
		tester.setWorkingForm("issue-workflow-transition");
		tester.submit();
		tester.assertTextPresent("Reopened");
    }

    @Override
    public void attachFile() {
        tester.clickLink("opsbar-operations_more");
        tester.clickLink("attach-file");
    }

    @Override
    public void editIssue() {
        tester.clickLink("editIssue");
    }
}

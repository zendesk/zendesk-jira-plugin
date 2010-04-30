package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian.issuehandler;

/**
 * Contains methods for accessing the new issue UI implemented in JIRA 4.1
 */
public class IssueHandlerJIRA41OrLater extends IssueHandler {
    @Override
    public void update() {
        tester.clickButton("update_submit");
    }

    @Override
    public void resolve() {
        tester.clickButton("comment_assign_submit");
    }

    @Override
    public void reopen() {
        tester.clickButton("comment_assign_submit");
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

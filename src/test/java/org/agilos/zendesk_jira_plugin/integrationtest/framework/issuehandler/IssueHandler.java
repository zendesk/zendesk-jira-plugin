package org.agilos.zendesk_jira_plugin.integrationtest.framework.issuehandler;

import org.agilos.zendesk_jira_plugin.testframework.JIRAClient;
import org.openqa.selenium.WebDriver;


import com.thoughtworks.selenium.Selenium;

/**
 * The default IssueHandler. Methods may be overloaded in version specific implementations
 */
public class IssueHandler {
    protected Selenium selenium = JIRAClient.selenium;

    public void update() {
        selenium.click("Update");
    }

    public void resolve() {
    	selenium.click("link=Resolve Issue");
    	selenium.waitForPageToLoad("3000");
    	selenium.isTextPresent("Resolve issue");
    	selenium.click("issue-workflow-transition-submit");
    	selenium.waitForPageToLoad("3000");
		selenium.isTextPresent("Resolved");
    }
    public void close() {
    	selenium.click("link=Close Issue");
    	selenium.waitForPageToLoad("3000");
    	selenium.isTextPresent("Close Issue");
    	selenium.click("issue-workflow-transition-submit");
    	selenium.waitForPageToLoad("3000");
    	selenium.isTextPresent("Closed");
    }
    public void reopen() {
		selenium.click("link=Reopen Issue");
    	selenium.waitForPageToLoad("3000");
    	selenium.isTextPresent("Reopen Issue");
    	selenium.click("issue-workflow-transition-submit");
    	selenium.waitForPageToLoad("3000");
		selenium.isTextPresent("Reopened");
    }

    public void attachFile() {
		selenium.click("//a[@id='opsbar-operations_more']/span");
		selenium.click("attach-file");
    }

    public void editIssue() {
		selenium.click("editIssue");
    	selenium.waitForPageToLoad("3000");
    }
}

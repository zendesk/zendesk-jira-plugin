package it.org.agilos.zendesk_jira_plugin.integrationtest.testcases;

import static org.testng.AssertJUnit.assertEquals;
import it.org.agilos.zendesk_jira_plugin.integrationtest.AbstractNotificationTest;

import org.agilos.zendesk_jira_plugin.testframework.TestDataFactory;
import org.testng.annotations.Test;

public class WorkflowNotificationTest extends AbstractNotificationTest {

	@Test (groups = {"regressionTests"} )
	public void testDefaultWorkflow() {
		selenium.open("browse/"+issueKey);
		selenium.isTextPresent("Open");
		selenium.click("assign-to-me");  //Need to assign the issue to current user to gain access to all workflow actions
		selenium.waitForPageToLoad("3000");
		
		selenium.click("link=Start Progress");
		selenium.waitForPageToLoad("3000");
		selenium.isTextPresent("In Progress");
		assertEquals("Wrong response received setting issue 'In progress'", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.started"), 
				fixture.getNextRequest().getEntityAsText());

		selenium.click("link=Stop Progress");
		selenium.waitForPageToLoad("3000");
		selenium.isTextPresent("Open");
		assertEquals("Wrong response received setting stopping progress on issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.stopped"), 
				fixture.getNextRequest().getEntityAsText());

		issueHandler.resolve();
		assertEquals("Wrong response received after resolving issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.resolved"), 
				fixture.getNextRequest().getEntityAsText());

		issueHandler.close();
		assertEquals("Wrong response received after closing issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.closed"), 
				fixture.getNextRequest().getEntityAsText());

		issueHandler.reopen();
		assertEquals("Wrong response received after reopening issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.reopened"), 
				fixture.getNextRequest().getEntityAsText());
	}
	
	//@Test (groups = {"regressionTests"} )
	public void testCustomWorkflow() {
		//ToDo Implement assignment of custom workflow to project, see http://jira.agilos.org/browse/ZEN-68
		
		selenium.open("browse/"+issueKey);
		selenium.isTextPresent("Open");
		selenium.click("link=to me"); //Need to assign the issue to current user to gain access to all workflow actions
		
		selenium.click("link=Investigate");
		selenium.isTextPresent("Investigate");
		assertEquals("Wrong response received after setting issue to investigating", 
				TestDataFactory.getSoapResponse("testCustomWorkflow.investigating"), 
				fixture.getNextRequest().getEntityAsText());
	}
}

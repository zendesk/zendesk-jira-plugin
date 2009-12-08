package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

public class WorkflowNotificationTest extends AbstractNotificationTest {

	@Test (groups = {"regressionTests"} )
	public void testDefaultWorkflow() {
		fixture.webTester.gotoPage("browse/"+issueKey);
		fixture.webTester.assertTextPresent("Open");
		fixture.webTester.clickLinkWithText("to me"); //Need to assign the issue to current user to gain access to all workflow actions

		fixture.webTester.clickLinkWithText("Start Progress");
		fixture.webTester.assertTextPresent("In Progress");
		assertEquals("Wrong response received setting issue 'In progress'", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.started"), 
				fixture.getNextRequest().getEntityAsText());

		fixture.webTester.clickLinkWithText("Stop Progress");
		fixture.webTester.assertTextPresent("Open");
		assertEquals("Wrong response received setting stopping progress on issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.stopped"), 
				fixture.getNextRequest().getEntityAsText());

		fixture.webTester.clickLinkWithText("Resolve Issue");
		fixture.webTester.setWorkingForm("jiraform");
		fixture.webTester.assertTextPresent("Resolve Issue");
		fixture.webTester.clickButton("Resolve");
		fixture.webTester.assertTextPresent("Resolved");
		assertEquals("Wrong response received after resolving issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.resolved"), 
				fixture.getNextRequest().getEntityAsText());

		fixture.webTester.clickLinkWithText("Close Issue");
		fixture.webTester.setWorkingForm("jiraform");
		fixture.webTester.assertTextPresent("Close Issue");
		fixture.webTester.submit();
		fixture.webTester.assertTextPresent("Closed");
		assertEquals("Wrong response received after closing issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.closed"), 
				fixture.getNextRequest().getEntityAsText());

		fixture.webTester.clickLinkWithText("Reopen Issue");
		fixture.webTester.setWorkingForm("jiraform");
		fixture.webTester.assertButtonPresent("Reopen Issue");
		fixture.webTester.submit();
		fixture.webTester.assertTextPresent("Reopened");
		assertEquals("Wrong response received after reopening issue", 
				TestDataFactory.getSoapResponse("testDefaultWorkflow.reopened"), 
				fixture.getNextRequest().getEntityAsText());
	}
	
	//@Test (groups = {"regressionTests"} )
	public void testCustomWorkflow() {
		//ToDo Implement assignment of custom workflow to project, see http://jira.agilos.org/browse/ZEN-68
		
		fixture.webTester.gotoPage("browse/"+issueKey);
		fixture.webTester.assertTextPresent("Open");
		fixture.webTester.clickLinkWithText("to me"); //Need to assign the issue to current user to gain access to all workflow actions
		
		fixture.webTester.clickLinkWithText("Investigate");
		fixture.webTester.assertTextPresent("Investigate");
		assertEquals("Wrong response received after setting issue to investigating", 
				TestDataFactory.getSoapResponse("testCustomWorkflow.investigating"), 
				fixture.getNextRequest().getEntityAsText());
	}
}

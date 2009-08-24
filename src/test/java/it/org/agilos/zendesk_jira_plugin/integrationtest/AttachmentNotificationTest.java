package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;

import org.restlet.data.Request;
import org.testng.annotations.Test;

public class AttachmentNotificationTest extends AbstractNotificationTest {
	
	@Test (groups = {"regressionTests"} )
	public void testUploadAttachment() throws Exception  {		
		fixture.updateIssueWithAttachment(
				issueKey, 
				new String[] { "pom.xml" }, 
				new File[] { loadFile("pom.xml")});
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testUploadAttachment.1"), request.getEntityAsText());		
	}
	
	private File loadFile(String fileName) {
		return new File(fileName);		
	}
}

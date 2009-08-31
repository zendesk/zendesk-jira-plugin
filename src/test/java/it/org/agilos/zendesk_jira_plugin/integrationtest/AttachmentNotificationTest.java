package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;

import org.agilos.zendesk_jira_plugin.integrationtest.AttachmentComparator;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.AttachmentReceiver;
import org.restlet.data.Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AttachmentNotificationTest extends AbstractNotificationTest {	
	public static final String attachmentUploadDir = "src"+File.separator+"test"+File.separator+"attachments";
	
	@BeforeMethod (alwaysRun = true)  
	@Override
	void setUpTest() throws Exception {
		super.setUpTest();
		if (AttachmentReceiver.ATTACHMENT_DIRECTORY.exists()) {
			File[] oldAttachmentFiles= AttachmentReceiver.ATTACHMENT_DIRECTORY.listFiles();
			for (int i=0;i<oldAttachmentFiles.length;i++) {
				oldAttachmentFiles[i].delete();
			}
		} else {
			AttachmentReceiver.ATTACHMENT_DIRECTORY.mkdir();
		}
	}
	
	@Test (groups = {"regressionTests"} )
	public void testUploadAttachment() throws Exception  {		
		String attachmentName = "pom-example.xml";
		File uploadFile = loadFile(attachmentUploadDir+File.separator+attachmentName);
		fixture.updateIssueWithAttachment(
				issueKey, 
				new String[] { attachmentName }, 
				new File[] { uploadFile });
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testUploadAttachment.1"), request.getEntityAsText());	
		
		File receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
		
		assert(AttachmentComparator.fileContentsEquals(uploadFile, receivedFile));
	}
	
	@Test (groups = {"regressionTests"} )
	public void testUploadBinaryAttachment() throws Exception  {	
		String attachmentName = "mikis.jpg";
		File uploadFile = loadFile(attachmentUploadDir+File.separator+attachmentName);	
		fixture.updateIssueWithAttachment(
				issueKey, 
				new String[] { attachmentName }, 
				new File[] { uploadFile });
		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testUploadBinaryAttachment.1"), 
				fixture.getNextRequest().getEntityAsText());	
	
		File receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
		
		assert(AttachmentComparator.fileContentsEquals(uploadFile, receivedFile));
	
	}
	
	@Test (groups = {"regressionTests"} )
	public void testUploadAttachmentHttps() throws Exception  {	
		getFixture().getJiraClient().setZendeskUrl("https://localhost:8443");
		
		String attachmentName = "https.jpg";
		File uploadFile = loadFile(attachmentUploadDir+File.separator+"mikis.jpg");	
		fixture.updateIssueWithAttachment(
				issueKey, 
				new String[] { attachmentName }, 
				new File[] { uploadFile });
		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testUploadAttachmentHttps.1"), 
				fixture.getNextHttpsRequest().getEntityAsText());	
	
		File receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
		
		assert(AttachmentComparator.fileContentsEquals(uploadFile, receivedFile));
	
	}
	
	private File loadFile(String fileName) {
		return new File(fileName);		
	}
}
	
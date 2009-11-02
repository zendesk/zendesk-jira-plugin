package it.org.agilos.zendesk_jira_plugin.integrationtest;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;

import org.agilos.zendesk_jira_plugin.integrationtest.AttachmentComparator;
import org.agilos.zendesk_jira_plugin.integrationtest.notifications.AttachmentReceiver;
import org.restlet.data.Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ZEN-47 Upload of attachments to Zendesk, http://jira.agilos.org/browse/ZEN-47
 */
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
	
	@Test (groups = {"regressionTests"} )
	public void testDisableUploadAttachment() throws Exception  {	
		getFixture().getJiraClient().setUploadAttachments("false");
		
		String attachmentName = "mikis.jpg";
		File uploadFile = loadFile(attachmentUploadDir+File.separator+"mikis.jpg");	
		fixture.updateIssueWithAttachment(
				issueKey, 
				new String[] { attachmentName }, 
				new File[] { uploadFile });
		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testDisableUploadAttachment.1"), 
				fixture.getNextRequest().getEntityAsText());	
	
		File receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
		
		assert(!receivedFile.exists());
		
		getFixture().getJiraClient().setUploadAttachments("true");
		
		attachmentName = "https.jpg";
		uploadFile = loadFile(attachmentUploadDir+File.separator+"mikis.jpg");	
		fixture.updateIssueWithAttachment(
				issueKey, 
				new String[] { attachmentName }, 
				new File[] { uploadFile });
		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testDisableUploadAttachment.2"), 
				fixture.getNextRequest().getEntityAsText());	
	
		receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
		
		assert(receivedFile.exists());
	
	}
	/**
	 * http://jira.agilos.org/browse/ZEN-64 Attachment names with spaces are corrupt in notification links 
	 */
	@Test (groups = {"regressionTests"} )
	public void testAttachmentNameWithSpaces() throws Exception  {		
		String attachmentName = "favicon with spaces in name.ico";
		File uploadFile = loadFile(attachmentUploadDir+File.separator+attachmentName);
		fixture.updateIssueWithAttachment(
				issueKey, 
				new String[] { attachmentName }, 
				new File[] { uploadFile });
		Request request = fixture.getNextRequest(); 
		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testAttachmentNameWithSpaces.1"), request.getEntityAsText());	
		
		File receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
		
		assert(AttachmentComparator.fileContentsEquals(uploadFile, receivedFile));
	}
	
//	@Test (groups = {"regressionTests"} )
//	public void testAttachmentDeleted() throws Exception  {	
//		String attachmentName = "mikis.jpg";
//		File uploadFile = loadFile(attachmentUploadDir+File.separator+attachmentName);	
//		fixture.updateIssueWithAttachment(
//				issueKey, 
//				new String[] { attachmentName }, 
//				new File[] { uploadFile });
//		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testAttachmentDeleted.1"), 
//				fixture.getNextRequest().getEntityAsText());	
//	
//		File receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
//		
//		assert(AttachmentComparator.fileContentsEquals(uploadFile, receivedFile));
//	}
	
	/**
	 * Attachments larger than 7 MB should be uploaded to Zendesk
	 * @throws Exception
	 */
//	Doesn't currentl work, cause Out of memory in client axis stubs
//	@Test (groups = {"regressionTests"} )
//	public void testUploadBigAttachment() throws Exception  {	
//		getFixture().getJiraClient().setZendeskUrl("https://localhost:8443");
//		
//		String attachmentName = "large_file.bin";
//		File uploadFile = loadFile(attachmentUploadDir+File.separator+attachmentName);	
//		fixture.updateIssueWithAttachment(
//				issueKey, 
//				new String[] { attachmentName }, 
//				new File[] { uploadFile });
//		assertEquals("Wrong response received after adding attachment", TestDataFactory.getSoapResponse("testUploadBigAttachment.1"), 
//				fixture.getNextHttpsRequest().getEntityAsText());	
//	
//		File receivedFile = loadFile(AttachmentReceiver.ATTACHMENT_DIRECTORY+File.separator+attachmentName);
//		
//		assert(!receivedFile.exists());
//	}
	
	private File loadFile(String fileName) {
		return new File(fileName);		
	}
}
	
package org.agilos.jira.zendesk;

import java.io.FileInputStream;
import java.io.IOException;

import org.agilos.jira.zendesk.notifications.ChangeMessage;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.log4j.Logger;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.attachment.Attachment;

public class AttachmentHandler {
	private static Logger log = Logger.getLogger(AttachmentHandler.class.getName());
	
	public static String uploadServerUrl = null;
	
	public static void handleAttachment(ChangeMessage changeMessage, Long attachmentId, IssueEvent changeEvent) throws HttpException, IOException {
		Attachment attachment = ManagerFactory.getAttachmentManager().getAttachment(attachmentId);
		
		//changeMessage.attachmentAdded("<a href=\""+NotificationDispatcher.getBaseUrl()+"/secure/attachment/"+attachmentId+"/"+attachment.getFilename()+"\">"+attachment.getFilename()+"</a>");
		
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(uploadServerUrl+"/uploads.xml?filename="+attachment.getFilename());

		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

		RequestEntity requestEntity = new InputStreamRequestEntity(new FileInputStream(attachment.getFilename()));
		postMethod.setRequestEntity(requestEntity);
		postMethod.setRequestHeader("Content-type",	"text/xml; charset=ISO-8859-1");

		if (client.executeMethod(postMethod) != 0) {
			log.info("Failed to upload attachment "+attachment.getFilename()+ ", response was "+postMethod.getResponseBodyAsString());
		} else if (log.isInfoEnabled()) {
			log.info("Attachment "+attachment.getId()+ " posted, response was "+postMethod.getResponseBodyAsString());
		}
		postMethod.releaseConnection();
	}
}

package org.agilos.jira.zendesk;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.attachment.Attachment;

public class AttachmentHandler {
	private static Logger log = Logger.getLogger(AttachmentHandler.class.getName());
	
	public static String uploadServerUrl = null;
	
	public static void handleAttachment(Document document, Node commentRoot, Long attachmentId, IssueEvent changeEvent) throws HttpException, IOException {
		Attachment attachment = ManagerFactory.getAttachmentManager().getAttachment(attachmentId);
		
		commentRoot.appendChild(ChangeMessageBuilder.createComment(commentRoot.getOwnerDocument(), 
				changeEvent.getRemoteUser().getFullName()+" added attachment <a href=\""+
				NotificationDispatcher.getBaseUrl()+"/secure/attachment/"+attachmentId+"/"+attachment.getFilename()+"\">"+attachment.getFilename()+"</a></value>"));
		
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

package org.agilos.jira.zendesk;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.agilos.jira.zendesk.notifications.ChangeMessage;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.attachment.Attachment;

public class AttachmentHandler {
	private static Logger log = Logger.getLogger(AttachmentHandler.class.getName());
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
	public static String handleAttachment(ChangeMessage changeMessage, Long attachmentId, IssueEvent changeEvent) throws HttpException, IOException, SAXException, ParserConfigurationException {
		Attachment attachment = ManagerFactory.getAttachmentManager().getAttachment(attachmentId);
		
		HttpClient client = new HttpClient();
		client.getParams().setAuthenticationPreemptive(true);
		
		Credentials defaultcreds = new UsernamePasswordCredentials("username", "password");
		client.getState().setCredentials(new AuthScope(
				ZendeskNotifier.getZendeskserverConfiguration().getUrl().getHost(), 
				ZendeskNotifier.getZendeskserverConfiguration().getUrl().getPort(),
				AuthScope.ANY_REALM), defaultcreds);
		
		PostMethod postMethod = new PostMethod(ZendeskNotifier.getZendeskserverConfiguration().getUrl()+"/uploads.xml?filename="+attachment.getFilename());

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
		
		Document document = factory.newDocumentBuilder().parse(postMethod.getResponseBodyAsStream());
		return document.getFirstChild().getAttributes().getNamedItem("token").getTextContent();
	}
}

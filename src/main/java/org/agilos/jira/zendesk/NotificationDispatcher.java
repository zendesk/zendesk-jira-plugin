package org.agilos.jira.zendesk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.fields.CustomField;

public class NotificationDispatcher {	
	private Logger log = Logger.getLogger(NotificationDispatcher.class.getName());
	private String zendeskServerURL;
	private String suppressNotificationFor;
	private CustomField ticketField;
	private ChallengeResponse authentication;

	void setZendeskServerURL(String url) {
		zendeskServerURL = url;
		log.info("Zendesk url set to "+url);
	}
	
	/**
	 * No notification will be sent for issue changes made by the indicated user.
	 * @param supressNotificationFor
	 */
	public void setSuppressNotificationFor(String suppressNotificationFor) {
		this.suppressNotificationFor = suppressNotificationFor;
		log.info("Zendesk application login set to "+suppressNotificationFor);
	}

	void setAuthentication(String user, String password) {
		log.info("Using HTTP basic authentication for Zendesk access with user "+user+" and password "+password);
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		authentication = new ChallengeResponse(scheme,
				user, password);
	}

	public void sendIssueChangeNotification(IssueEvent issueEvent) {
		if (zendeskServerURL == null) {
			log.warn("The Zendesk server URL hasn't been defined, no notification of the change will be sent to Zendesk. Please specify a " +
					"Zendesk server URL in JIRA's administrativ interface under 'Listeners' -> ZendeskNotifier");
			return;
		}
		if (authentication == null) {
			log.warn("The Zendesk server URL hasn't been defined, no notification of the change will be sent to Zendesk. Please specify a " +
					"valid Zendesk user and password in JIRA's administrativ interface under 'Listeners' -> ZendeskNotifier");
			return;
		}
		if (suppressNotificationFor == null) {
			log.warn("The Zendesk application login user hasn't been defined, no notification of the change will be sent to Zendesk (As this would " +
					"cause a notification loop). Please specify the Zendesk application used to login into JIRA. This can be done in JIRA's " +
					"administrativ interface under 'Listeners' -> ZendeskNotifier");
			return;
		} else if (issueEvent.getRemoteUser().getName().equals(suppressNotificationFor)) {
			log.debug("Received notification for change made by zendesk application "+suppressNotificationFor+", supressing notification");
			return;
		}
		ClientResource resource = new ClientResource(zendeskServerURL+"/tickets/"+getTicketID(issueEvent.getIssue().getKey())+".xml");
		resource.setReferrerRef(getBaseUrl());
		resource.setChallengeResponse(authentication);

		try {
			Representation representation = getRepresentation(issueEvent);
			setSize(representation);
			log.debug("Dispatching: put "+representation.getText());
			resource.put(representation);
			if (resource.getStatus().isSuccess()
					&& resource.getResponseEntity().isAvailable()) {
				log.debug("Received response"+resource.getResponseEntity());
			} else if (resource.getResponseEntity() != null && resource.getResponseEntity().isAvailable()){
				log.warn("No success in sending notification, response was:\n"+resource.getResponseEntity().getText());
			} else {
				log.warn("No response received");
			}

		} catch (ResourceException e) {
			log.error("Failed to send issue change notification", e);
		} catch (IOException e) {
			log.warn("Failed to represent request as text", e);
		}
	}

	private String getBaseUrl() {
		final ApplicationProperties ap = ManagerFactory.getApplicationProperties();
		return ap.getString(APKeys.JIRA_BASEURL);
	}

	/**
	 * Generates a REST representation of the issue change
	 * @param issueEvent
	 * @return The REST representation of the issue change if any relevant changes are found, else null;
	 * @throws ParserConfigurationException 
	 * @throws ResourceException
	 * @throws IOException
	 */
	private Representation getRepresentation(IssueEvent issueEvent) {
		DomRepresentation representation = new DomRepresentation(MediaType.APPLICATION_XML, ChangeMessageBuilder.createChangeDocument(issueEvent));           
		representation.setCharacterSet(CharacterSet.UTF_8);	      	
		return representation;
	}

	public void setTicketFieldValue(String ticketFieldName) {
		ticketField = ManagerFactory.getCustomFieldManager().getCustomFieldObjectByName(ticketFieldName);		
	}

	private String getTicketID(String issueKey) {
		return (String)ManagerFactory.getIssueManager().getIssueObject(issueKey).getCustomFieldValue(ticketField);
	}

	private void setSize(Representation representation) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		representation.write(out);
		representation.setSize(out.size());
	}
}

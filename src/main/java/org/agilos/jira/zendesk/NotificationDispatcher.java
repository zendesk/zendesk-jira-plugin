package org.agilos.jira.zendesk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.agilos.jira.zendesk.notifications.ChangeMessage;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.representation.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.fields.CustomField;

public class NotificationDispatcher {	
	private Logger log = Logger.getLogger(NotificationDispatcher.class.getName());
	private String suppressNotificationFor;
	private CustomField ticketField;
	private ChangeMessageBuilder messageBuilder = new ChangeMessageBuilder();
	private Context context;

	public NotificationDispatcher(Context context) {
		this.context = context;
	}

	/**
	 * No notification will be sent for issue changes made by the indicated user.
	 * @param supressNotificationFor
	 */
	public void setSuppressNotificationFor(String suppressNotificationFor) {
		this.suppressNotificationFor = suppressNotificationFor;
		log.info("Zendesk application login set to "+suppressNotificationFor);
	}

	public void setPublicComments(boolean publicComments) {
		messageBuilder.setPublicComments(publicComments);
	}

	public void sendIssueChangeNotification(IssueEvent issueEvent) {
		//Only handle issues with a defined ZendeskID, see http://jira.agilos.org/browse/ZEN-58.
		if (getTicketID(issueEvent.getIssue().getKey()) == null || getTicketID(issueEvent.getIssue().getKey()).equals("")) return; 
		
		if (ZendeskNotifier.getZendeskserverConfiguration().getUrl() == null) {
			log.warn("The Zendesk server URL hasn't been defined, no notification of the change will be sent to Zendesk. Please specify a " +
			"Zendesk server URL in JIRA's administrativ interface under 'Listeners' -> ZendeskNotifier");
			return;
		}
		if (ZendeskNotifier.getZendeskserverConfiguration().getUser() == null || 
				ZendeskNotifier.getZendeskserverConfiguration().getPassword() == null) {
			log.warn("The Zendesk server username or password hasn't been defined, no notification of the change will be sent to Zendesk. Please specify a " +
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

		try {
			ClientResource resource = new ClientResource(context, 
					new Reference(Protocol.valueOf(ZendeskNotifier.getZendeskserverConfiguration().getUrl().getProtocol()), 
							ZendeskNotifier.getZendeskserverConfiguration().getUrl().getAuthority()+"/tickets/"+getTicketID(issueEvent.getIssue().getKey())+".xml"));

			resource.setReferrerRef(getBaseUrl());
			resource.setChallengeResponse(ZendeskNotifier.getZendeskserverConfiguration().getAuthentication());

			ChangeMessage.MessageParts messageParts = messageBuilder.createChangeRepresentation(issueEvent);

			if ( messageParts.getTicketChanges() != null ) { // Contains changes
				Representation changeMessage = getRepresentation(messageParts.getTicketChanges());
				setSize(changeMessage);
				if (log.isDebugEnabled()) {
					log.debug("Dispatching change message: put "+changeMessage.getText() + 
							ZendeskNotifier.getZendeskserverConfiguration().getUrl() + "/tickets/"+getTicketID(issueEvent.getIssue().getKey())+".xml") ;	
				}
				resource.put(changeMessage);
				
				if (resource.getStatus().isSuccess()
						&& resource.getResponseEntity().isAvailable()) {
					log.debug("Received response"+resource.getResponseEntity());
				} else if (resource.getResponseEntity() != null && resource.getResponseEntity().isAvailable()){
					log.warn("No success in sending notification, response was:\n"+resource.getResponseEntity().getText());
				} else {
					log.warn("No response received");
				}
			}

			if ( messageParts.getComment() != null ) { // Contains changes
				Representation commentMessage = getRepresentation(messageParts.getComment());
				setSize(commentMessage);
				if (log.isDebugEnabled()) {
					log.debug("Dispatching comment message: put "+commentMessage.getText() + 
							ZendeskNotifier.getZendeskserverConfiguration().getUrl() + "/tickets/"+getTicketID(issueEvent.getIssue().getKey())+".xml");	
				}
				resource.put(commentMessage);
				
				if (resource.getStatus().isSuccess()
						&& resource.getResponseEntity().isAvailable()) {
					log.debug("Received response"+resource.getResponseEntity());
				} else if (resource.getResponseEntity() != null && resource.getResponseEntity().isAvailable()){
					log.warn("No success in sending notification, response was:\n"+resource.getResponseEntity().getText());
				} else {
					log.warn("No response received");
				}
			}

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
			log.error("Failed to send issue change notification", e);
		} catch (NoSuchFieldException e) {
			// The event has already been debug logged by the mMessageBuilder
		} catch (GenericEntityException e) {
			log.warn("Unable to detect changes", e);
		} catch (ParserConfigurationException e) {
			log.warn("Unable to intantiate DOM object for notification XML", e);
		} 
	}

	public static String getBaseUrl() {
		final ApplicationProperties ap = ManagerFactory.getApplicationProperties();
		return ap.getString(APKeys.JIRA_BASEURL);
	}

	/**
	 * Generates a REST representation of the issue change
	 * @param issueEvent
	 * @return The REST representation of the issue change if any relevant changes are found, else null;
	 * @throws IOException 
	 * @throws GenericEntityException 
	 * @throws ParserConfigurationException 
	 */
	private Representation getRepresentation(Document document) throws IOException, NoSuchFieldException, GenericEntityException, ParserConfigurationException {
		DomRepresentation representation = new DomRepresentation(MediaType.APPLICATION_XML, document);           
		representation.setCharacterSet(CharacterSet.UTF_8);	      	
		return representation;
	}

	public void setTicketFieldValue(String ticketFieldName) {
			ticketField = ManagerFactory.getCustomFieldManager().getCustomFieldObjectByName(ticketFieldName);		
			
			if (ticketField == null) {
				log.error("Zendesk TicketID customfield not defined correctly. Please check that the indicated TicketIDField '"+ticketFieldName+
						"' corresponds to the name of the customfield used to store the Zendesk Ticket ID");
			}
	}

	private String getTicketID(String issueKey) {
		if (ticketField == null) {
			log.warn("Unable to check Zendesk TicketID for issue "+issueKey+", no update will be sent to Zendesk for the change to this issue. " +
					"Please check that the TicketIDField defined for the Zendesk plug corresponds to the name of the customfield used to store " +
					"the Zendesk Ticket ID");
			return null;
		}
		
		return (String)ManagerFactory.getIssueManager().getIssueObject(issueKey).getCustomFieldValue(ticketField);
	}

	private void setSize(Representation representation) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		representation.write(out);
		representation.setSize(out.size());
	}
}

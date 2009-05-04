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
	private CustomField ticketField;
	private ChallengeResponse authentication;

	void setZendeskServerURL(String url) {
		zendeskServerURL = url;
		log.info("Zendesk url set to "+url);
	}

	void setAuthentication(String user, String password) {
		log.info("Using HTTP basic authentication for Zendesk access with user "+user+" and password "+password);
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		authentication = new ChallengeResponse(scheme,
				user, password);
	}

	public void sendIssueChangeNotification(IssueEvent issueEvent) {
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
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error(e);
			return null;
		}
		DOMImplementation impl = builder.getDOMImplementation();

		Document document = impl.createDocument(null,null,null);

		Node commentRoot = document;

		if (issueEvent.getEventTypeId() == EventType.ISSUE_UPDATED_ID ||
				issueEvent.getEventTypeId() == EventType.ISSUE_MOVED_ID) {
			Element ticket = document.createElement("ticket");
			document.appendChild(ticket);

			if (issueEvent.getIssue().getSummary() != null) {
				Element subject = document.createElement("subject");
				subject.setTextContent(issueEvent.getIssue().getSummary());
				ticket.appendChild(subject);
			}

			if (issueEvent.getIssue().getDescription() != null) {
				Element description = document.createElement("description");
				description.setTextContent(issueEvent.getIssue().getDescription());
				ticket.appendChild(description);
			}

			if (issueEvent.getEventTypeId() == EventType.ISSUE_MOVED_ID) {
				Element newIssueKey = document.createElement("external-id");
				newIssueKey.setTextContent(issueEvent.getIssue().getKey());
				ticket.appendChild(newIssueKey);
			}
			
			if (issueEvent.getComment() != null) {
				Element comments = document.createElement("comments");
				comments.setAttribute("type", "array");
				ticket.appendChild(comments);
				commentRoot = comments;
			}
			// If no relevant changes have been added to the ticket root node, exit.
			if (ticket.getChildNodes().getLength() == 0 ) return null;
		}

		else if (issueEvent.getEventTypeId() == EventType.ISSUE_COMMENTED_ID) {
			Element comment = document.createElement("comment");
			commentRoot.appendChild(comment);

			Element isPublic = document.createElement("is-public");
			isPublic.setTextContent("true");
			comment.appendChild(isPublic);

			Element value = document.createElement("value");
			value.setTextContent(issueEvent.getComment().getBody());
			comment.appendChild(value);   	
		}
		
		else {
			return null; //Unknown event type
		}

		DomRepresentation representation = new DomRepresentation(MediaType.APPLICATION_XML, document);           
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

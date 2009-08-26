package org.agilos.jira.zendesk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.restlet.resource.ResourceException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;

public class ChangeMessageBuilder {
	private static Logger log = Logger.getLogger(ChangeMessageBuilder.class.getName());	

	private static boolean publicComments = true;

	/**
	 * Generates a REST representation of the issue change
	 * @param issueEvent
	 * @return The REST representation of the issue change if any relevant changes are found, else null;
	 * @throws ParserConfigurationException 
	 * @throws ResourceException
	 * @throws IOException
	 * @throws NoSuchFieldException Throw in case of a changeEvent state, which the ChangeMessageBuilder is unable to handle.  
	 * @throws GenericEntityException 
	 */
	public Document createChangeRepresentation(IssueEvent changeEvent) throws IOException, NoSuchFieldException, GenericEntityException {
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

		if (changeEvent.getEventTypeId() == EventType.ISSUE_UPDATED_ID ||
				changeEvent.getEventTypeId() == EventType.ISSUE_MOVED_ID) {
			Element ticket = document.createElement("ticket");
			document.appendChild(ticket);
			
			List changes = changeEvent.getChangeLog().getRelated("ChildChangeItem");
			Map<String, GenericValue> changeMap = new HashMap<String, GenericValue>();
			
			Iterator changeIterator = changes.iterator();
			while (changeIterator.hasNext()) {
				GenericValue gv = (GenericValue)changeIterator.next();
				changeMap.put((String)gv.get("field"), gv);
			}
			
			if (log.isDebugEnabled()) log.debug("Issue change received for "+changeEvent.getIssue().getId()+", " +
					"The following attributes have changed: "+changeMap.keySet());

			if (changeEvent.getIssue().getSummary() != null) {
				Element subject = document.createElement("subject");
				subject.setTextContent(changeEvent.getIssue().getSummary());
				ticket.appendChild(subject);
			}

			if (changeEvent.getIssue().getDescription() != null) {
				Element description = document.createElement("description");
				description.setTextContent(changeEvent.getIssue().getDescription());
				ticket.appendChild(description);
			}

			if (changeEvent.getEventTypeId() == EventType.ISSUE_MOVED_ID) {
				Element newIssueKey = document.createElement("external-id");
				newIssueKey.setTextContent(changeEvent.getIssue().getKey());
				ticket.appendChild(newIssueKey);
			}

			if (changeEvent.getComment() != null) {
				Element comments = document.createElement("comments");
				comments.setAttribute("type", "array");
				ticket.appendChild(comments);
				commentRoot = comments;
				commentRoot.appendChild(createComment(document, 
						changeEvent.getRemoteUser().getFullName()+" added a comment:\n"+changeEvent.getComment().getBody()));
			}
			
			if (changeMap.get("Attachment") != null) {
				AttachmentHandler.handleAttachment(document, commentRoot,
						Long.valueOf(changeMap.get("Attachment").getString("newvalue")), changeEvent);
			}
			// If no relevant changes have been added to the ticket root node, exit.
			if (ticket.getChildNodes().getLength() == 0 ) return null;
		}

		else if (changeEvent.getEventTypeId() == EventType.ISSUE_COMMENTED_ID) {
			commentRoot.appendChild(createComment(document, 
					changeEvent.getRemoteUser().getFullName()+" added a comment:\n"+changeEvent.getComment().getBody()));
		} 
		
		else if (changeEvent.getIssue().getStatusObject() != null &&
				 !changeEvent.getEventTypeId().equals(EventType.ISSUE_CREATED_ID)) { // Disregard newly created issues, this is not considered a status change
			commentRoot.appendChild(createComment(document, 
					changeEvent.getRemoteUser().getFullName()+" changed status to "+changeEvent.getIssue().getStatusObject().getName()));
		}	
		
		else {
			//The logging done here, instead of at the exception handling to avoid composing the log message when debug logging is disabled.
			if (log.isDebugEnabled()) {
				StringBuffer logMessage = new StringBuffer();
				logMessage.append("No notification handle defined for event with ID "+changeEvent.getEventTypeId());
				if (changeEvent.getIssue().getStatusObject() != null) {
					logMessage.append(" and status"+changeEvent.getIssue().getStatusObject().getName()); 
				}
				log.debug(logMessage.toString());
			}
			throw new NoSuchFieldException();
		}
		return document;
	}
	

	public void setPublicComments(boolean areCommentsPublic) {
		publicComments = areCommentsPublic;
	}
	
	public static Element createComment(Document document, String message) {
		Element comment = document.createElement("comment");

		Element isPublic = document.createElement("is-public");
		if (publicComments) {
			isPublic.setTextContent("true");
		} else {
			isPublic.setTextContent("false");
		}
		comment.appendChild(isPublic);

		Element value = document.createElement("value");
		
		value.setTextContent(message);
		comment.appendChild(value);   
		
		return comment;
	}	
}

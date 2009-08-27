package org.agilos.jira.zendesk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.agilos.jira.zendesk.notifications.ChangeMessage;
import org.agilos.jira.zendesk.notifications.JIRAZendeskFieldMapping;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.w3c.dom.Document;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.attachment.Attachment;

public class ChangeMessageBuilder {
	private static Logger log = Logger.getLogger(ChangeMessageBuilder.class.getName());	

	/**
	 * Generates a REST representation of the issue change
	 * @param issueEvent
	 * @return The REST representation of the issue change if any relevant changes are found, else null;
	 * @throws IOException
	 * @throws NoSuchFieldException Throw in case of a changeEvent state, which the ChangeMessageBuilder is unable to handle.  
	 * @throws GenericEntityException 
	 * @throws ParserConfigurationException 
	 */
	public Document createChangeRepresentation(IssueEvent changeEvent) throws IOException, NoSuchFieldException, GenericEntityException, ParserConfigurationException {
		ChangeMessage changeMessage = new ChangeMessage(changeEvent.getRemoteUser().getFullName(), 
				changeEvent.getIssue().getKey()+" "+changeEvent.getIssue().getSummary());

		if (changeEvent.getEventTypeId() == EventType.ISSUE_UPDATED_ID ||
				changeEvent.getEventTypeId() == EventType.ISSUE_MOVED_ID) {

			List changes = changeEvent.getChangeLog().getRelated("ChildChangeItem");
			Map<String, GenericValue> changeMap = new HashMap<String, GenericValue>();

			Iterator changeIterator = changes.iterator();
			while (changeIterator.hasNext()) {
				GenericValue gv = (GenericValue)changeIterator.next();
				String jiraFieldName = (String)gv.get("field");
				changeMap.put(jiraFieldName, gv); 

				//Special cases first
				if (jiraFieldName.equals("Attachment")) {
					Long attachmentId = Long.valueOf(changeMap.get("Attachment").getString("newvalue"));
					Attachment attachment = ManagerFactory.getAttachmentManager().getAttachment(attachmentId);
					changeMessage.addChange("Attachment", attachment.getFilename(), null);
					AttachmentHandler.handleAttachment(changeMessage,
							Long.valueOf(changeMap.get("Attachment").getString("newvalue")), changeEvent);
				} else { changeMessage.addChange(JIRAZendeskFieldMapping.getMappedZendeskFieldName(jiraFieldName), 
						jiraFieldName, gv.getString("newstring"), gv.getString("oldstring"));
				}

			}

			if (log.isDebugEnabled()) log.debug("Issue change received for "+changeEvent.getIssue().getId()+", " +
					"The following attributes have changed: "+changeMap.keySet());

//			if (changeEvent.getIssue().getSummary() != null) {
//				changeMessage.addSummeryChange(changeEvent.getIssue().getSummary(), null);
//			}
//
//			if (changeEvent.getIssue().getDescription() != null) {
//				changeMessage.addDescriptionChange( changeEvent.getIssue().getDescription(), null);
//			}
//
//			if (changeEvent.getEventTypeId() == EventType.ISSUE_MOVED_ID) {
//				changeMessage.addKeyChange( changeEvent.getIssue().getKey(), null);
//			}
//
//			if (changeEvent.getComment() != null) {
//				changeMessage.addComment(changeEvent.getComment().getBody());
//			}
//
//			if (changeMap.get("Attachment") != null) {
//				Long attachmentId = Long.valueOf(changeMap.get("Attachment").getString("newvalue"));
//				Attachment attachment = ManagerFactory.getAttachmentManager().getAttachment(attachmentId);
//				changeMessage.addChange("Attachment", attachment.getFilename(), null);
//				AttachmentHandler.handleAttachment(changeMessage,
//						Long.valueOf(changeMap.get("Attachment").getString("newvalue")), changeEvent);
//			}

		}

		else if (changeEvent.getEventTypeId() == EventType.ISSUE_COMMENTED_ID) {
			changeMessage.addComment(changeEvent.getComment().getBody());
		} 

		else if (changeEvent.getIssue().getStatusObject() != null &&
				!changeEvent.getEventTypeId().equals(EventType.ISSUE_CREATED_ID)) { // Disregard newly created issues, this is not considered a status change
			changeMessage.addChange( "Info", changeEvent.getIssue().getStatusObject().getName(), null);
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
		if (changeMessage.isEmpty()) return null;
		else return changeMessage.getDocument();
	}

	public void setPublicComments(boolean areCommentsPublic) {
		ChangeMessage.publicComments = areCommentsPublic;
	}
}

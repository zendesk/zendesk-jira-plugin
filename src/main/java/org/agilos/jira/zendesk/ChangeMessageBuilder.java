package org.agilos.jira.zendesk;

import com.atlassian.jira.event.issue.IssueEvent;
import org.agilos.jira.zendesk.notifications.ChangeMessage;
import org.agilos.jira.zendesk.notifications.JIRAZendeskFieldMapping;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Responsible for the building of the change message containing the updates to Zendesk. The details are handled by the <code>ChangeMessage</code> and
 * <code>AttachmentHandler</code> classes.
 *
 * @author mikis
 */
public class ChangeMessageBuilder {
    private static Logger log = Logger.getLogger(ChangeMessageBuilder.class.getName());

    /**
     * Generates a REST representation of the issue change
     *
     * @param changeEvent
     * @return The REST representation of the issue change if any relevant changes are found, else null;
     * @throws IOException
     * @throws NoSuchFieldException         Thrown in case of a changeEvent state, which the ChangeMessageBuilder is unable to handle.
     * @throws GenericEntityException
     * @throws ParserConfigurationException
     */
    public ChangeMessage.MessageParts createChangeRepresentation(IssueEvent changeEvent) throws IOException, NoSuchFieldException, GenericEntityException, ParserConfigurationException {
        ChangeMessage changeMessage = new ChangeMessage(changeEvent.getRemoteUser().getFullName(),
                changeEvent.getIssue().getKey() + " " + changeEvent.getIssue().getSummary());

        if (changeEvent.getChangeLog() != null) {
            List changes = changeEvent.getChangeLog().getRelated("ChildChangeItem");
            Map<String, GenericValue> changeMap = new HashMap<String, GenericValue>();

            Iterator changeIterator = changes.iterator();
            while (changeIterator.hasNext()) {
                GenericValue gv = (GenericValue) changeIterator.next();
                String jiraFieldName = (String) gv.get("field");
                changeMap.put(jiraFieldName, gv);

                //Special cases first
                if (jiraFieldName.equals("Attachment")) {
                    String id = changeMap.get("Attachment").getString("newvalue");
                    if (id != null) { //Attachment added
                        AttachmentHandler.handleAttachment(changeMessage, Long.valueOf(changeMap.get("Attachment").getString("newvalue")));
                    } else { //Attachment deleted
                        changeMessage.addChangeComment("Attachment " + changeMap.get("Attachment").getString("oldstring") + " deleted.");
                    }
                } else {
                    changeMessage.addChange(JIRAZendeskFieldMapping.getMappedZendeskFieldName(jiraFieldName),
                            jiraFieldName, gv.getString("newstring"), gv.getString("oldstring"));
                }
            }

            if (log.isDebugEnabled()) log.debug("Issue change received for " + changeEvent.getIssue().getId() + ", " +
                    "The following attributes have changed: " + changeMap.keySet());
        }

        if (changeEvent.getComment() != null && changeEvent.getComment().getRoleLevel() == null) {
            changeMessage.addComment(changeEvent.getComment().getBody());
        }


        return changeMessage.createMessageParts();
    }

    public void setPublicComments(boolean areCommentsPublic) {
        ChangeMessage.publicComments = areCommentsPublic;
	}
}

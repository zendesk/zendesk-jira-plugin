package org.agilos.jira.zendesk;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;

public class ZendeskNotifier extends AbstractIssueEventListener {
	public static final String ZENDESK_URL_PARAMETER = "ZendeskUrl";
	public static final String ZENDESK_LOGIN_NAME_PARAMETER = "LoginName";
	public static final String ZENDESK_LOGIN_PASSWORD_PARAMETER = "LoginPassword";
	public static final String ZENDESK_TICKET_CUSTOMFIELD = "TicketIDField";
	/**
	 * No notification will be sent for issue changes made by the indicated user. This is done to avoid Zendesk<-> Jira notification loops.
	 */
	public static final String ZENDESK_APPLICATION_LOGIN = "ZendeskApplicationLogin";
	
	private Logger log = Logger.getLogger(ZendeskNotifier.class.getName());
	private final static NotificationDispatcher dispatcher = new NotificationDispatcher();
	private static String ticketFieldName = "Zendesk TicketID";
	
	public static String getTicketFieldName() {
		return ticketFieldName;
	}

	/**
	 * When detecting a <code>ISSUE_COMMENTED</code>, <code>ISSUE_COMMENTED</code> or <code>ISSUE_COMMENT_EDITED_ID</code> calls the <code>@link NotificationDispatcher</code>. 
	 */
	public void workflowEvent(IssueEvent issueEvent) {
		if (issueEvent.getEventTypeId() == EventType.ISSUE_UPDATED_ID ||
				issueEvent.getEventTypeId() == EventType.ISSUE_COMMENTED_ID||
				issueEvent.getEventTypeId() == EventType.ISSUE_COMMENT_EDITED_ID ||
				issueEvent.getEventTypeId() == EventType.ISSUE_MOVED_ID ||
				issueEvent.getIssue().getStatusObject() != null) {
			dispatcher.sendIssueChangeNotification(issueEvent);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(Map params) {
		log.info("Received new parameters:"+params);
		if(params.containsKey(ZENDESK_URL_PARAMETER)) dispatcher.setZendeskServerURL((String)params.get(ZENDESK_URL_PARAMETER));
		if(params.containsKey(ZENDESK_LOGIN_NAME_PARAMETER) && params.containsKey(ZENDESK_LOGIN_PASSWORD_PARAMETER)) {
			dispatcher.setAuthentication(
					(String)params.get(ZENDESK_LOGIN_NAME_PARAMETER), 
					(String)params.get(ZENDESK_LOGIN_PASSWORD_PARAMETER));
		}
		if(params.containsKey(ZENDESK_TICKET_CUSTOMFIELD)) dispatcher.setTicketFieldValue((String)params.get(ZENDESK_TICKET_CUSTOMFIELD));
		if(params.containsKey(ZENDESK_APPLICATION_LOGIN)) dispatcher.setSuppressNotificationFor((String)params.get(ZENDESK_APPLICATION_LOGIN));
    }
	
	@Override
    public String[] getAcceptedParams() {
        return new String[] {ZENDESK_URL_PARAMETER,ZENDESK_LOGIN_NAME_PARAMETER,ZENDESK_LOGIN_PASSWORD_PARAMETER,ZENDESK_TICKET_CUSTOMFIELD, ZENDESK_APPLICATION_LOGIN};
    }

	@Override
	public String getDescription() {
		return "Listens for issue changed and sends a update notification to the configured Zendesk application, when issue changes are detected.";
	}

	@Override
	public boolean isUnique() {		
		return true;
	}
    
    
}

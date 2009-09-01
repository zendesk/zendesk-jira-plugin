package org.agilos.jira.zendesk;

import java.net.MalformedURLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;

public class ZendeskNotifier extends AbstractIssueEventListener {
	public static final String ZENDESK_URL_PARAMETER = "ZendeskUrl";
	public static final String ZENDESK_LOGIN_NAME_PARAMETER = "LoginName";
	public static final String ZENDESK_LOGIN_PASSWORD_PARAMETER = "LoginPassword";
	public static final String ZENDESK_TICKET_CUSTOMFIELD = "TicketIDField";
	public static final String ZENDESK_PUBLIC_COMMENTS = "Public comments";
	public static final String ZENDESK_KEYSTORE_PASSORD = "Keystore password(For https)";	
	public static final String UPLOAD_ATTACHMENTS = "Upload attachments";
	
	/**
	 * No notification will be sent for issue changes made by the indicated user. This is done to avoid Zendesk <-> Jira notification loops.
	 */
	public static final String ZENDESK_APPLICATION_LOGIN = "ZendeskApplicationLogin";
	
	private Logger log = Logger.getLogger(ZendeskNotifier.class.getName());
	private static Context context = new Context();
	private static final ZendeskServerConfiguration zendeskServerConfiguration = new ZendeskServerConfiguration();
	public static ZendeskServerConfiguration getZendeskserverConfiguration() {
		return zendeskServerConfiguration;
	}

	static {
		context.getParameters().add("keystorePassword", "changeit");
	}
	private final static NotificationDispatcher dispatcher = new NotificationDispatcher(context);
	private static String ticketFieldName = "Zendesk TicketID";
	
	public ZendeskNotifier() {
		SLF4JBridgeHandler.install();
	}
	
	public static String getTicketFieldName() {
		return ticketFieldName;
	}

	/**
	 * Sends the event to  the <code>@link NotificationDispatcher</code>. 
	 */
	public void workflowEvent(IssueEvent issueEvent) {
		dispatcher.sendIssueChangeNotification(issueEvent);
	}
	
	@Override
	public void init(Map params) {
		log.info("Received new parameters:"+params);
		if(params.containsKey(ZENDESK_URL_PARAMETER)) {
			try {
				zendeskServerConfiguration.setUrl((String)params.get(ZENDESK_URL_PARAMETER));
			} catch (MalformedURLException e) {
				throw new RuntimeException("Failed to parse the provided Zendesk server URL, "+e.getMessage());
			}

			log.info("Zendesk server url updated: " +zendeskServerConfiguration);
		}
		if(params.containsKey(ZENDESK_LOGIN_NAME_PARAMETER) && params.containsKey(ZENDESK_LOGIN_PASSWORD_PARAMETER)) {
			zendeskServerConfiguration.setAuthentication(
					(String)params.get(ZENDESK_LOGIN_NAME_PARAMETER), 
					(String)params.get(ZENDESK_LOGIN_PASSWORD_PARAMETER));
					log.info("Zendesk server login information updated: " +zendeskServerConfiguration);
		}
		if(params.containsKey(ZENDESK_TICKET_CUSTOMFIELD)) dispatcher.setTicketFieldValue((String)params.get(ZENDESK_TICKET_CUSTOMFIELD));
		if(params.containsKey(ZENDESK_APPLICATION_LOGIN)) dispatcher.setSuppressNotificationFor((String)params.get(ZENDESK_APPLICATION_LOGIN));
		if(true){ // Always do this as undefined value means true
			String value = (String)params.get(ZENDESK_PUBLIC_COMMENTS);
			if (value != null && value.equals("false")) {
				dispatcher.setPublicComments(false);
			} else {
				dispatcher.setPublicComments(true);
			}
		}
		if(true){ // Always do this as undefined value means true
			String value = (String)params.get(UPLOAD_ATTACHMENTS);
			if (value != null && value.equals("false")) {
				AttachmentHandler.uploadAttachments = false;
			} else {
				AttachmentHandler.uploadAttachments = true;
			}
		}
		if (params.containsKey(ZENDESK_KEYSTORE_PASSORD)) {
			context.getParameters().clear(); // Hack, should just set the keystorePassword element, but all attempts to modify parameter set causes a UnsupportedOperationException 
			context.getParameters().add("keystorePassword", (String)params.get(ZENDESK_KEYSTORE_PASSORD));
		}		
    }
	
	@Override
    public String[] getAcceptedParams() {
        return new String[] {
        		ZENDESK_URL_PARAMETER,
        		ZENDESK_LOGIN_NAME_PARAMETER,
        		ZENDESK_LOGIN_PASSWORD_PARAMETER,
        		ZENDESK_TICKET_CUSTOMFIELD, 
        		ZENDESK_APPLICATION_LOGIN, 
        		ZENDESK_PUBLIC_COMMENTS,
        		UPLOAD_ATTACHMENTS,
        		ZENDESK_KEYSTORE_PASSORD};
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

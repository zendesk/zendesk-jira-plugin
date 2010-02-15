package org.agilos.jira.zendesk;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.fields.CustomField;
import org.agilos.jira.zendesk.notifications.ChangeMessage;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Handles the sending of the Zendesk notifications. The construction of the notification messages are delegated to the <code>ChangeMessageBuilder</code>.
 * <p/>
 * Further filtering is implemented here, including checks for incomplete configuration of the plugin.
 *
 * @author mikis
 */
public class NotificationDispatcher {
    private Logger log = Logger.getLogger(NotificationDispatcher.class.getName());
    private String suppressNotificationFor;
    private CustomField ticketField;
    private ChangeMessageBuilder messageBuilder = new ChangeMessageBuilder();

    public NotificationDispatcher() {
    }

    /**
     * No notification will be sent for issue changes made by the indicated user.
     *
     * @param suppressNotificationFor
     */
    public void setSuppressNotificationFor(String suppressNotificationFor) {
        this.suppressNotificationFor = suppressNotificationFor;
        log.info("Zendesk application login set to " + suppressNotificationFor);
    }

    public void setPublicComments(boolean publicComments) {
        messageBuilder.setPublicComments(publicComments);
    }

    public void sendIssueChangeNotification(IssueEvent issueEvent) {
        //Only handle issues with a defined ZendeskID, see http://jira.agilos.org/browse/ZEN-58.
        if (getTicketID(issueEvent.getIssue().getKey()) == null || getTicketID(issueEvent.getIssue().getKey()).equals(""))
            return;

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
            log.debug("Received notification for change made by zendesk application " + suppressNotificationFor + ", supressing notification");
            return;
        }

        HttpClient client = new HttpClient();
        client.getState().setCredentials(new AuthScope(
                ZendeskNotifier.getZendeskserverConfiguration().getUrl().getHost(),
                ZendeskNotifier.getZendeskserverConfiguration().getUrl().getPort(),
                AuthScope.ANY_REALM),
                ZendeskNotifier.getZendeskserverConfiguration().getCredentials());
        client.getParams().setAuthenticationPreemptive(true);

        try {
            ChangeMessage.MessageParts messageParts = messageBuilder.createChangeRepresentation(issueEvent);
            String ticketID = getTicketID(issueEvent.getIssue().getKey());

            if (messageParts.hasTicketChanges()) {
                sendNotification(client, ticketID, messageParts.getTicketChanges());
            }
            if (messageParts.hasComment()) {
                sendNotification(client, ticketID, messageParts.getComment());
            }
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

    private void sendNotification(HttpClient client, String ticketID, String message) throws IOException {
        PutMethod method = new PutMethod(ZendeskNotifier.getZendeskserverConfiguration().getUrl() + "/tickets/" + ticketID + ".xml");
        method.setDoAuthentication(true);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        StringRequestEntity request = new StringRequestEntity(message);
        method.setRequestEntity(request);
        if (log.isDebugEnabled()) {
            log.debug("Dispatching change message: put " + request.getContent() + " to URL: " +
                    ZendeskNotifier.getZendeskserverConfiguration().getUrl() + "/tickets/" + ticketID + ".xml");
        }
        client.executeMethod(method);

        if (method.getStatusCode() != HttpStatus.SC_OK) {
            log.debug("Received unexpected response code " + method.getStatusCode() +", response was: "+ method.getResponseBodyAsString());
        } else if (method.getResponseBody() != null) {
            log.warn("No success in sending notification, response was:\n" + method.getResponseBodyAsString());
        } else {
            log.warn("No response received");
        }
    }

    public static String getBaseUrl() {
        final ApplicationProperties ap = ManagerFactory.getApplicationProperties();
        return ap.getString(APKeys.JIRA_BASEURL);
    }

    public void setTicketFieldValue(String ticketFieldName) {
        ticketField = ManagerFactory.getCustomFieldManager().getCustomFieldObjectByName(ticketFieldName);

        if (ticketField == null) {
            log.error("Zendesk TicketID customfield not defined correctly. Please check that the indicated TicketIDField '" + ticketFieldName +
                    "' corresponds to the name of the customfield used to store the Zendesk Ticket ID");
        }
    }

    private String getTicketID(String issueKey) {
        if (ticketField == null) {
            log.warn("Unable to check Zendesk TicketID for issue " + issueKey + ", no update will be sent to Zendesk for the change to this issue. " +
                    "Please check that the TicketIDField defined for the Zendesk plug corresponds to the name of the customfield used to store " +
                    "the Zendesk Ticket ID");
            return null;
        }

        return (String) ManagerFactory.getIssueManager().getIssueObject(issueKey).getCustomFieldValue(ticketField);
    }

//	private void setSize(Representation representation) throws IOException {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		representation.write(out);
//		representation.setSize(out.size());
//	}
}

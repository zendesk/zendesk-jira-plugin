package org.agilos.jira.zendesk;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.event.issue.IssueEvent;

public class NotificationDispatcher {	
	private Logger log = Logger.getLogger(NotificationDispatcher.class.getName());
	// Create the client resource
	private ClientResource resource;
	
	void setZendeskServerURL(String url) {
		log.info("Zendesk url set to "+url);
		resource = new ClientResource(url);
		resource.setReferrerRef(getBaseUrl());
	}
	
	void setAuthentication(String user, String password) {
		log.info("Using HTTP basic authentication for Zendesk access with user "+user+" and password "+password);
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme,
				user, password);
		resource.setChallengeResponse(authentication);
	}
	
	public void sendIssueChangeNotification(IssueEvent issueEvent) {
		try {
			Representation representation = getRepresentation(issueEvent);
			log.debug("Dispatching: put "+representation.getText());
			resource.put(getRepresentation(issueEvent));
			if (resource.getStatus().isSuccess()
	                && resource.getResponseEntity().isAvailable()) {
				log.debug("Received response"+resource.getResponseEntity());
	        } else {
	        	log.debug("No success in sending notification");
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
     * Returns the Representation of an item.
     * 
     * @param item
     *            the item.
     * 
     * @return The Representation of the item.
     */
    private Representation getRepresentation(IssueEvent issueEvent) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("key", issueEvent.getIssue().getKey());
        form.add("description",issueEvent.getIssue().getSummary());
        return form.getWebRepresentation();
    }

}

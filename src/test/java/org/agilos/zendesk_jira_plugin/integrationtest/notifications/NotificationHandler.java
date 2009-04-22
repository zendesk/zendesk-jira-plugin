package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import org.apache.log4j.Logger;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class NotificationHandler extends ServerResource {
	
	private Logger log = Logger.getLogger(NotificationHandler.class.getName());

	@Get
	public String represent() {
		log.info("Received request from "+getReference()+" with method: " + getRequest().getMethod() +" and entity "+getRequest().getEntityAsText());
		try {
			NotificationListener.messageQueue.put(getRequest());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Request received by test NotificationHandler";
	}
}

package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.restlet.data.Request;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class HttpsNotificationHandler extends ServerResource {
	
	private Logger log = Logger.getLogger(NotificationHandler.class.getName());
	private final static LinkedBlockingQueue<Request> messageQueue = new LinkedBlockingQueue<Request>();

	@Get
	public String represent() {
		log.info("Received request from "+getReference()+" with method: " + getRequest().getMethod() +" and entity "+getRequest().getEntityAsText());
		try {
			messageQueue.put(getRequest());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Request received by test NotificationHandler";
	}
	
	public static LinkedBlockingQueue<Request> getMessageQueue() {
		return messageQueue;
	}
}

package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import java.util.LinkedList;

import javax.management.NotificationListener;

import org.apache.log4j.Logger;
import org.restlet.data.Request;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class NotificationHandler extends ServerResource {
	
	private Logger log = Logger.getLogger(NotificationHandler.class.getName());
	public static final String MESSAGELIST = "Messagelist";	

	@SuppressWarnings("unchecked")
	@Get
	public String represent() {
		log.info("Received request from "+getReference()+" with method: " + getRequest().getMethod() +" and entity "+getRequest().getEntityAsText());
		if (getContext().getAttributes().get(MESSAGELIST)==null) {
		    getContext().getAttributes().put(MESSAGELIST, new LinkedList<Request>());
		}
		((LinkedList<Request>)getContext().getAttributes().get(MESSAGELIST)).add(getRequest());
		return "Request received by test NotificationHandler";
	}
}

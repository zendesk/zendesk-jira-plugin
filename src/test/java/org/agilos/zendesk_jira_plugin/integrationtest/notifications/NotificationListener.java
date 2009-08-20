package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.Guard;

public class NotificationListener extends Application {
	private Logger log = Logger.getLogger(NotificationListener.class.getName());
	private Class handler;
	
	public NotificationListener(Class handler) {
		this.handler = handler;
		createRoot();
	}
	
	
	@Override
    public synchronized Restlet createRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of the NotificationHandler.
		
        Router router = new Router(getContext());

        // Defines only one route
        log.debug("Attaching handler "+handler);
        router.attach("/tickets",handler);
        	
        Guard guard = new Guard(getContext(), ChallengeScheme.HTTP_BASIC, "Tutorial");
		guard.getSecrets().put("jira", "jira".toCharArray());		
		guard.setNext(router);
        
        return guard;
    }
}

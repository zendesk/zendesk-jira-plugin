package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Request;
import org.restlet.routing.Router;
import org.restlet.security.Guard;

public class NotificationListener extends Application {
	public static final LinkedBlockingQueue<Request> messageQueue = new LinkedBlockingQueue<Request>();
	private Logger log = Logger.getLogger(NotificationListener.class.getName());
	
	public NotificationListener() {
		createRoot();
	}
	
	@Override
    public synchronized Restlet createRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of the NotificationHandler.
		
        Router router = new Router(getContext());

        // Defines only one route
        router.attachDefault(NotificationHandler.class);
        
        Guard guard = new Guard(getContext(), ChallengeScheme.HTTP_BASIC, "Tutorial");
		guard.getSecrets().put("jira", "jira".toCharArray());		
		guard.setNext(router);
        
        return guard;
    }
	
	public Request getNextRequest() {
		Request request = null;
		try {
			request = messageQueue.poll(10l, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("No response received in 10 seconds");
		}
		return request; 
	}
}

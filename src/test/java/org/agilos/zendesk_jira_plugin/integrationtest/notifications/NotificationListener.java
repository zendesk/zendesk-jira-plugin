package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.routing.Router;
import org.restlet.security.Guard;

public class NotificationListener extends Application {
	private Logger log = Logger.getLogger(NotificationListener.class.getName());
	private final LinkedBlockingQueue<Request> notificationQueue;
	
	public NotificationListener(LinkedBlockingQueue<Request> notificationQueuecontext) {
		super();
		this.notificationQueue = notificationQueuecontext;
		createRoot();
	}	
	
	@Override
    public synchronized Restlet createRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of the NotificationHandler.
		
        Router router = new Router(getContext());

        // Defines only one route
        log.debug("Attaching handler ");
        router.attachDefault(new NotificationHandler());
        	
        Guard guard = new Guard(getContext(), ChallengeScheme.HTTP_BASIC, "Tutorial");
		guard.getSecrets().put("jira", "jira".toCharArray());		
		guard.setNext(router);
        
        return guard;
    }
	
	public class NotificationHandler extends Restlet {
		@Override
        public void handle(Request request, Response response) {
			log.info("Received request to "+request.getResourceRef()+" from " +request.getHostRef()+ " with method: " + request.getMethod() +" and entity "+request.getEntityAsText());
			try {
				notificationQueue.put(request);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			response.setStatus(Status.SUCCESS_OK);
		}	
	}
}

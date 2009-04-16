package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import java.util.LinkedList;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.Request;
import org.restlet.routing.Router;

public class NotificationListener extends Application {
	
	@Override
    public synchronized Restlet createRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of the NotificationHandler.
        Router router = new Router(getContext());

        // Defines only one route
        router.attachDefault(NotificationHandler.class);

        return router;
    }
	
	@SuppressWarnings("unchecked")
	public String getNextRequest() {
		return ((LinkedList<Request>)getContext().getAttributes().get(NotificationHandler.MESSAGELIST)).getFirst().toString();
	}
}

package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationListener;
import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class RestServer {
	private static final RestServer instance = new RestServer();
	public static RestServer getInstance() {
		return instance;
	}

	private Logger log = Logger.getLogger(RestServer.class.getName());
	private Component component;

	private RestServer() {

	}

	public synchronized void setListener(NotificationListener notificationListener) {
		if (component == null) {
			try {
				// Create a new Component.
				component = new Component();

				// Add a new HTTP server listening on port 8182.
				log.info("Adding HTTP rest server on port 8182");
				component.getServers().add(Protocol.HTTP, 8182);

				// Attach the sample application.
				log.info("Adding Notification listener");
				component.getDefaultHost().attach(notificationListener);

				// Start the component.
				log.info("Starting rest server");
				component.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

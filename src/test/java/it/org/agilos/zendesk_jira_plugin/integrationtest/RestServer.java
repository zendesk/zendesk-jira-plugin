package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationListener;
import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class RestServer {

	private Logger log = Logger.getLogger(RestServer.class.getName());
	private Component component;

	public synchronized void setListener(NotificationListener notificationListener, Protocol protocol, int port) {
		if (component == null) {

			// Create a new Component.
			component = new Component();
		}

		// Add a new HTTP server listening on the specific port. The https termination needs to be handled by another application. f.ex an Apache server
		// redirecting to the indicated port.
		log.info("Adding "+Protocol.HTTP+" server on port "+port);
		component.getServers().add(Protocol.HTTP, port);

		component.getDefaultHost().attach(notificationListener);

		// Start the component.
		log.info("Starting rest server on port "+port);
		try {
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

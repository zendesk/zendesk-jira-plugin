package org.agilos.zendesk_jira_plugin.testframework;

import java.util.concurrent.LinkedBlockingQueue;

import org.agilos.zendesk_jira_plugin.integrationtest.notifications.NotificationListener;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.data.Request;

public class ZendeskServerStub extends Component {

	//private Logger log = Logger.getLogger(ZendeskServerStub.class.getName());

	public ZendeskServerStub(LinkedBlockingQueue<Request> notificationQueue, int port) {
		super();
		// Add a new HTTP server listening on the specific port. 
		getServers().add(Protocol.HTTP, port);

		getDefaultHost().attach("/tickets", new NotificationListener(notificationQueue));
		getDefaultHost().attach("/uploads.xml", new AttachmentListener());
	}
}

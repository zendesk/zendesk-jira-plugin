package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.agilos.zendesk_jira_plugin.integrationtest.notifications.AttachmentReceiver;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class AttachmentListener extends Application {
	public AttachmentListener(Context context) {
		super(context);
	}

	/**
	 * Creates a unique route for all URIs to the sample resource
	 */
	@Override
	public Restlet createRoot() {
		Router router = new Router(getContext());
		// All URIs are routed to a new instance of MyResource class.
		router.attachDefault(AttachmentReceiver.class);

		return router;
	}

}

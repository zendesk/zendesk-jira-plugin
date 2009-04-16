package org.agilos.jira.zendesk;

import org.apache.log4j.Logger;

import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;

public class ZendeskNotifier extends AbstractIssueEventListener {

	private Logger log = Logger.getLogger(ZendeskNotifier.class.getName());
	
	public void workflowEvent(IssueEvent issueEvent) {
		log.info("Received issue change notification:"+issueEvent);
	}
}

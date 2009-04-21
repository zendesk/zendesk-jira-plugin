package org.agilos.jira.zendesk;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;

public class ZendeskNotifier extends AbstractIssueEventListener {
	private String zendeskURL;

	private Logger log = Logger.getLogger(ZendeskNotifier.class.getName());
	
	public void workflowEvent(IssueEvent issueEvent) {
		log.info("Received issue change notification:"+issueEvent);
	}
	
	public void init(Map params)
    {
		log.info("Received new parameters:"+params);
    }

    public String[] getAcceptedParams()
    {
        return new String[] {"ZendeskUrl"};
    }
}

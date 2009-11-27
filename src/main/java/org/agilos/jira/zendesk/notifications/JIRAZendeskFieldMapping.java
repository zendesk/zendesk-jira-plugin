package org.agilos.jira.zendesk.notifications;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.issue.IssueFieldConstants;

public class JIRAZendeskFieldMapping {
	private static final Map<String, String> fieldMap = new HashMap<String, String>();
		
	static {
		fieldMap.put(IssueFieldConstants.SUMMARY, "subject");
		fieldMap.put(IssueFieldConstants.DESCRIPTION, "description");
		fieldMap.put("Key", "external-id");
	}
	
	public static String getMappedZendeskFieldName(String jiraFieldName) {
		return fieldMap.get(jiraFieldName);
	}
}

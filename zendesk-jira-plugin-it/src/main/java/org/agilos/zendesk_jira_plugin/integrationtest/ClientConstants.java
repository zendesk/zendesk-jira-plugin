package org.agilos.zendesk_jira_plugin.integrationtest;

import java.util.Date;

interface ClientConstants
{
    // Login details
    static final String LOGIN_NAME = "bamboo";
    static final String LOGIN_PASSWORD = "bamboo2997";

    static final String PROJECT_KEY = "TST";
    static final String PROJECT_NAME = "Dee Project";
    static final String PROJECT_DESCRIPTION = "This is a project created by soap on: " + new Date();
    static final String ISSUE_TYPE_ID = "1";
    static final String SUMMARY_NAME = "This is a new SOAP issue " + new Date();
    static final String PRIORITY_ID = "4";
    static final String COMPONENT_ID = "10240";
    static final String VERSION_ID = "10330";
    static final String NEW_COMMENT_BODY = "This is a new comment";
}

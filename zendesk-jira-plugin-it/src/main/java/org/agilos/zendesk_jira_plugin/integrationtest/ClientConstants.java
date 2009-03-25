package org.agilos.zendesk_jira_plugin.integrationtest;

import java.util.Date;

interface ClientConstants
{
    // Login details
    static final String LOGIN_NAME = "soaptester";
    static final String LOGIN_PASSWORD = "soaptester";

    // Constants for issue creation
    static final String PROJECT_KEY = "TST";
    static final String ISSUE_TYPE_ID = "1";
    static final String SUMMARY_NAME = "This is a new SOAP issue " + new Date();
    static final String PRIORITY_ID = "4";
    static final String COMPONENT_ID = "10240";
    static final String VERSION_ID = "10330";

    // Constants for issue update
    static final String NEW_SUMMARY = "New summary";
    static final String CUSTOM_FIELD_KEY_1 = "customfield_10061";
    static final String CUSTOM_FIELD_VALUE_1 = "10098";
    static final String CUSTOM_FIELD_KEY_2 = "customfield_10061:1";
    static final String CUSTOM_FIELD_VALUE_2 = "10105";

    // Constant for add comment
    static final String NEW_COMMENT_BODY = "This is a new comment";

    // Constant for get filter
    static final String FILTER_ID = "12355";
    static final String SEARCH_TERM = "remote";

    // Constants for project creation
    static final String CREATE_PROJECT_KEY = "DUD";
    static final String PROJECT_NAME = "Dee Project";
    static final String PROJECT_DESCRIPTION = "This is a project created by soap on: " + new Date();

}

package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.util.Date;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.JIRAFixture;

public abstract class JIRATest {

//	protected static final String JIRA_URL = "http://192.168.0.100:8080";
//	protected static final String LOGIN_NAME = "bamboo";
//	protected static final String LOGIN_PASSWORD = "bamboo2997";
	
    protected static final String PROJECT_KEY = "WST";
    protected static final String USER_ID = "integration";
	protected static final String PROJECT_DESCRIPTION = "This is a Zendesk JIRA plugin integrationtest project " + new Date();	 
	
	protected abstract JIRAFixture getFixture();
}
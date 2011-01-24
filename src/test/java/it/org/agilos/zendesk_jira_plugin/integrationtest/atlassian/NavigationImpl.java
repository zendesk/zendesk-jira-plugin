// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 05-05-2009 11:07:27
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   NavigationImpl.java

package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian;

import com.atlassian.jira.webtests.util.JIRAEnvironmentData;
import net.sourceforge.jwebunit.WebTester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.net.URLEncoder;

public class NavigationImpl implements Navigation {
	private Logger log = Logger.getLogger(NavigationImpl.class.getName());
    protected JIRAEnvironmentData environmentData;
    protected WebTester tester;

    public NavigationImpl(WebTester tester, JIRAEnvironmentData environmentData) {

        this.environmentData = environmentData;
        this.tester = tester;
    }

    public void login(String userName) {
        login(userName, userName);
    }

    public void login(String userName, String userPassword) {
        login(userName, userPassword, false);
    }

    public void login(String userName, String userPassword, boolean useCookie) {
        log.info("Logging in as '" + userName + "'");
        tester.beginAt("/login.jsp");
        tester.setFormElement("os_username", userName);
        tester.setFormElement("os_password", userPassword);
        if (useCookie)
            tester.checkCheckbox("os_cookie", "true");
        tester.clickButton("login-form-submit");
    }

    public void logout() {
        log.info("Logging out");
        tester.beginAt("/secure/Logout!default.jspa");
    }

    public String getCurrentPage() {
        String urlString = tester.getDialog().getResponse().getURL().toString();
        String ctx = environmentData.getContext();
        if (ctx.length() > 0) {
            return urlString.substring(urlString.indexOf(ctx) + ctx.length());
        } else {
            String base = environmentData.getBaseUrl().toString();
            return urlString.substring(base.length());
        }
    }

    public void jiraLog(String logMessage) {
        tester.gotoPage("/secure/admin/debug/logMessage.jsp?message=" + URLEncoder.encode(logMessage));
    }

    public void gotoAdmin() {
        com.meterware.httpunit.HTMLElement element = null;
        try {
            element = tester.getDialog().getResponse().getElementWithID("adminMenu");
        }
        catch (SAXException e) {
            log.info("problem trying to find admin menu div, mustn't be on the admin menu");
        }
        if (element == null) {
            log.debug("going to admin page");
            tester.clickLink("admin_link");
        } else {
            log.debug("already at admin");
        }
    }

    public void gotoAdminSection(String linkId) {
        gotoAdmin();
        tester.clickLink(linkId);
    }

    public void gotoCustomFields() {
        tester.gotoPage("/secure/admin/ViewCustomFields.jspa");
    }

    public void gotoWorkflows() {
        tester.gotoPage("/secure/admin/workflows/ListWorkflows.jspa");
    }

    public void browseProject(String projectKey) {
        tester.gotoPage("/browse/" + projectKey);
    }
}
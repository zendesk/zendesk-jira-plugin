package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian;

public interface Navigation {

    public abstract void login(String s);

    public abstract void login(String s, String s1);

    public abstract void login(String s, String s1, boolean flag);

    public abstract void logout();

    public abstract void gotoAdmin();

    public abstract void gotoAdminSection(String s);

}
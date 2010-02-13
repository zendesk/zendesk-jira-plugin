package org.agilos.jira.zendesk;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import java.net.MalformedURLException;
import java.net.URL;

public class ZendeskServerConfiguration {
    private URL url;
    private String user;
    private String password;

    public URL getUrl() {
        return url;
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Credentials getCredentials() {
        return new UsernamePasswordCredentials(user, password);
    }

    public void setCredentials(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public String toString() {
        return "ZendeskServerConfiguration [password=" + password + ", url="
                + url + ", user=" + user + "]";
    }
}

package org.agilos.jira.zendesk;

import java.net.MalformedURLException;
import java.net.URL;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;

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
	
	public ChallengeResponse getAuthentication() {
		return new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
	}
	
	public void setAuthentication(String user, String password) {
		this.user = user;
		this.password = password;
	}
	@Override
	public String toString() {
		return "ZendeskServerConfiguration [password=" + password + ", url="
				+ url + ", user=" + user + "]";
	}
}

package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.net.URL;

import javax.xml.ws.WebServiceException;

import net.sourceforge.jwebunit.WebTester;

import org.agilos.jira.soapclient.JiraSoapService;
import org.agilos.jira.soapclient.JiraSoapServiceService;
import org.agilos.jira.soapclient.JiraSoapServiceServiceLocator;
import org.apache.log4j.Logger;

import com.atlassian.jira.functest.framework.FuncTestHelperFactory;
import com.atlassian.jira.functest.framework.navigation.IssueNavigation;
import com.atlassian.jira.webtests.util.JIRAEnvironmentData;
import com.atlassian.jira.webtests.util.LocalTestEnvironmentData;
import com.meterware.httpunit.HttpUnitOptions;

/**
 * Wraps the implementation details of how to connect to JIRA and login.
 * 
 * This is a Singleton, so use the  {@link #instance} method to get an instance to the concrete instance.
 * 
 * The following system properties define the behavior of the <code>JIRAClient</code>:
 * <ul>
 * <li> zendesk.jira.url The url of the JIRA instance the client should connect to
 * <li> zendesk.jira.login.name The Username to login with
 * <li> zendesk.jira.login.password The password to login to jira with
 * </ul>
 */
public class JIRAClient {
	private static JIRAClient instance = new JIRAClient();
	
	protected JiraSoapService jiraSoapService;
	protected String jiraSoapToken;
	
	public static final String jiraUrl = System.getProperty("zendesk.jira.url","http://localhost:1990/jira");
	public static final String loginName = System.getProperty("zendesk.jira.login.name", "bamboo");
	public static final String loginPassword = System.getProperty("zendesk.jira.login.password","bamboo2997");

	private Logger log = Logger.getLogger(JIRAClient.class.getName());
	
	private static FuncTestHelperFactory fthFatory;
	
	public static FuncTestHelperFactory getFuncTestHelperFactory() {
		return fthFatory;
	}
	
	public static IssueEditor getIssueEditor(String issuekey) {
		return new IssueEditor(issuekey, fthFatory);
	}

	private JIRAClient() {
		WebTester tester = new WebTester();
		LocalTestEnvironmentData environmentData = new LocalTestEnvironmentData();

		initWebTester(tester, environmentData);
		
		fthFatory = new FuncTestHelperFactory(tester, environmentData);
	}
	
	public void login() throws Exception {		
		login(loginName, loginPassword);
	}
	
	public void login(String user, String password) throws Exception {	
			URL jiraSOAPServiceUrl = new URL(jiraUrl+"/rpc/soap/jirasoapservice-v2");
			JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
			log.debug("Retriving jira soap service from "+jiraSOAPServiceUrl);
			jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2(jiraSOAPServiceUrl);
			log.debug("Logging in with user: "+user+" and password: "+password);
			jiraSoapToken = jiraSoapService.login(user, password); //Soap login
			fthFatory.getNavigation().login(user, password);// GUI login
	}
	
	public static JIRAClient instance() throws WebServiceException {
		return instance;
	}
	
	/**
	 * Returns the JIRA service instance the client uses to connect to JIRA.
	 */
	public JiraSoapService getService() {
		if (jiraSoapService == null) throw new WebServiceException("Haven't been able to connect to the JIRA server, see log for further details");
		return jiraSoapService;
	}
	
	/**
	 * The login token needed in all SOAP calls to JIRA
	 */
	public String getToken() {
		if (jiraSoapToken == null) throw new WebServiceException("Haven't been able to login to the JIRA server, see log for further details");
		return jiraSoapToken;
	}
	
	private void initWebTester(WebTester tester, JIRAEnvironmentData environmentData) { 
        tester.getTestContext().setBaseUrl(environmentData.getBaseUrl().toExternalForm());
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
        tester.beginAt("/");
        HttpUnitOptions.setScriptingEnabled(false);
    }
	
	public void setZendeskUrl(String zendeskURL) {
		gotoListenerConfiguration();
		log.info("Updating Zendesk URL to "+zendeskURL);
		fthFatory.getTester().setFormElement("ZendeskUrl", zendeskURL);
		fthFatory.getTester().clickButton("Update");
		fthFatory.getTester().assertTextPresent(zendeskURL);
	}
	
	public void setZendeskCredentials(String zendeskLogin, String zendeskPW) {
		gotoListenerConfiguration();
		log.info("Updating Zendesk log to "+zendeskLogin+" and zendesk PW to "+zendeskPW);
		fthFatory.getTester().setFormElement("LoginName", zendeskLogin);
		fthFatory.getTester().setFormElement("LoginPassword", zendeskPW);
		fthFatory.getTester().clickButton("Update");
		fthFatory.getTester().assertTextPresent(zendeskLogin);
		fthFatory.getTester().assertTextPresent(zendeskPW);
	}
	
	public void setCommentsPublic(String publicComments) {
		gotoListenerConfiguration();
		log.info("Setting public comment value to "+publicComments);
		fthFatory.getTester().setFormElement("Public comments", publicComments);
		fthFatory.getTester().clickButton("Update");
		fthFatory.getTester().assertTextPresent(publicComments);
	}	
	
	private void gotoListenerConfiguration() {
		fthFatory.getNavigation().gotoAdminSection("listeners");
		fthFatory.getTester().clickLinkWithText("Edit");
		fthFatory.getTester().assertTextPresent("ZendeskUrl");
	}
}

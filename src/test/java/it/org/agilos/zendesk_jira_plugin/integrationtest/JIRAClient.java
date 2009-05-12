package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.net.URL;

import javax.xml.ws.WebServiceException;

import net.sourceforge.jwebunit.WebTester;

import org.agilos.jira.soapclient.JiraSoapService;
import org.agilos.jira.soapclient.JiraSoapServiceService;
import org.agilos.jira.soapclient.JiraSoapServiceServiceLocator;
import org.apache.log4j.Logger;

import com.atlassian.jira.functest.framework.FuncTestHelperFactory;
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

	private Logger log = Logger.getLogger(JIRAClient.class.getName());
	
	private static FuncTestHelperFactory fthFatory;
	
	public FuncTestHelperFactory getFuncTestHelperFactory() {
		return fthFatory;
	}

	private JIRAClient() {
		WebTester tester = new WebTester();
		LocalTestEnvironmentData environmentData = new LocalTestEnvironmentData();

		initWebTester(tester, environmentData);
		
		fthFatory = new FuncTestHelperFactory(tester, environmentData);
	}
	
	public void login() {
		String jiraUrl = System.getProperty("zendesk.jira.url","http://localhost:1990/jira");
		String loginName = System.getProperty("zendesk.jira.login.name", "bamboo");
		String loginPassword = System.getProperty("zendesk.jira.login.password","bamboo2997");
		try {
			URL jiraSOAPServiceUrl = new URL(jiraUrl+"/rpc/soap/jirasoapservice-v2");
			JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
			log.debug("Retriving jira soap service from "+jiraSOAPServiceUrl);
			jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2(jiraSOAPServiceUrl);
			log.debug("Logging in with user: "+loginName+" and password: "+loginPassword);
			jiraSoapToken = jiraSoapService.login(loginName, loginPassword); //Soap login
			fthFatory.getNavigation().login(loginName, loginPassword);// GUI login
		} catch (Exception e) {
			log.error("Unable login to JIRA SOAP RPC service", e);
		}
	}
	
	public static JIRAClient instance() throws WebServiceException {
		return instance;
	}
	
	/**
	 * Returns the JIRA service instance the client uses to connect to JIRA.
	 * @return
	 */
	public JiraSoapService getService() {
		if (jiraSoapService == null) throw new WebServiceException("Haven't been able to connect to the JIRA server, see log for further details");
		return jiraSoapService;
	}
	
	/**
	 * The login token needed in all SOAP calls to JIRA
	 * @return
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
}

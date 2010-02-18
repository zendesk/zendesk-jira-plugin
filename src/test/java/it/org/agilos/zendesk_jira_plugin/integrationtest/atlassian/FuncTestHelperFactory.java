package it.org.agilos.zendesk_jira_plugin.integrationtest.atlassian;

import com.atlassian.jira.functest.framework.AdministrationImpl;
import com.atlassian.jira.webtests.util.JIRAEnvironmentData;
import com.meterware.httpunit.HttpUnitOptions;
import net.sourceforge.jwebunit.WebTester;

public class FuncTestHelperFactory {
    private final JIRAEnvironmentData environmentData;
    private WebTester tester;
    private NavigationImpl navigation;
    private AdministrationImpl administration;

    public FuncTestHelperFactory(WebTester tester, JIRAEnvironmentData environmentData) {
        navigation = null;
        administration = null;
        this.tester = tester;
        this.environmentData = environmentData;
    }

    private void initWebTester(JIRAEnvironmentData environmentData) {
        tester.getTestContext().setBaseUrl(environmentData.getBaseUrl().toExternalForm());
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
        tester.beginAt("/");
        HttpUnitOptions.setScriptingEnabled(false);
    }

    public WebTester getTester() {
        return tester;
    }

    public Navigation getNavigation() {
        if (navigation == null)
            navigation = new NavigationImpl(getTester(), getEnvironmentData());
        return navigation;
    }

    public JIRAEnvironmentData getEnvironmentData() {
        return environmentData;
    }
}

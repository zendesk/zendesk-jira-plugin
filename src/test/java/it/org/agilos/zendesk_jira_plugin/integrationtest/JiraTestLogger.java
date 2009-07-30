package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class JiraTestLogger implements ITestListener {

	private Logger log = Logger.getLogger(JiraTestLogger.class.getName());

	public void onFinish(ITestContext arg0) {}
	public void onStart(ITestContext arg0) {}
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {}

	public void onTestFailure(ITestResult arg0) {
		log.info("Failed test "+arg0.getName());
	}

	public void onTestSkipped(ITestResult arg0) {
		log.info("Skipped test "+arg0.getName());		
	}

	public void onTestStart(ITestResult arg0) {
		log.info("Starting test "+arg0.getName());		
	}

	public void onTestSuccess(ITestResult arg0) {
		log.info("Failed test "+arg0.getName());		
	}

}

package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JIRA {
	private static final String TEST_JIRAS_DIR = System.getProperty("test-jiras.dir", 
			System.getProperty("user.home") + "/products/test-jiras");
	public static final String VERSION = System.getProperty("jira.deploy.version", "4.3.2");
	public static final String INSTALL_DIR = TEST_JIRAS_DIR + "/JIRA-" + VERSION;
	public static final String HOME_DIR = INSTALL_DIR + "/data";
	public static final String URL = System.getProperty("jira.url" , "http://localhost:1990");
	public static final String LOGIN_NAME = System.getProperty("jira.login.name", "bamboo");
	public static final String LOGIN_PASSWORD = System.getProperty("jira.login.password","bamboopw");
	
	public static final Properties GUI_ELEMENT_NAMES = new Properties();
	
	static {
		loadProperties();
	}
	
	private static void loadProperties() {
		ClassLoader loader = ClassLoader.getSystemClassLoader ();
		String guiElementNameDefinitionFile = "webelementnaming/" + "jira-" + VERSION + "-elements.properties";
		try {
			GUI_ELEMENT_NAMES.load(loader.getResourceAsStream (guiElementNameDefinitionFile));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to load JIRA Gui element definitions from " + guiElementNameDefinitionFile);
		}
	}
}

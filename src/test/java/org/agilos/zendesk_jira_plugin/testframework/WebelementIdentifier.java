package org.agilos.zendesk_jira_plugin.testframework;

import java.net.URL;
import java.util.Properties;

/**
 * Supplies names and ID for the different components of the JIRA web GUI. The values are loaded from version 
 * dependent property files.
 */
public class WebelementIdentifier {
	private static final Properties properties = new Properties();
	
		public static String getElementName(String key) {
			if (properties == null) {
				try {
					load("jira-" + JIRAVersion.getJiraVersion() + "elements.properties");
				} catch (Exception e) {
					throw new RuntimeException("Unable to load web element naming properties", e);
				}
			}
			return properties.getProperty(key);
		}

		/**
		 * Load a properties file from the classpath
		 */
		private static void load(String propsName) throws Exception {
		URL url = ClassLoader.getSystemResource(propsName);
		properties.load(url.openStream());
	}	
}

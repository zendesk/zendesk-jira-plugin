package org.agilos.zendesk_jira_plugin.testframework;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public abstract class TestDataFactory {
	private static final Properties properties = loadProperties("testdata");;
	
	public static String getSoapResponse(String ID) {
		return (String)properties.get("soap_response."+ID);
	}

	private static Properties loadProperties(String name) {
		if (name == null)
			throw new IllegalArgumentException("null input: name");

		if (name.startsWith("/"))
			name = name.substring(1);

		if (name.endsWith(SUFFIX))
			name = name.substring(0, name.length() - SUFFIX.length());

		Properties result = null;

		InputStream in = null;

		try {
			name = name.replace('/', '.');
			// Throws MissingResourceException on lookup failures:
			final ResourceBundle rb = ResourceBundle.getBundle(name, Locale
					.getDefault(), ClassLoader.getSystemClassLoader());

			result = new Properties();
			for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
				final String key = (String) keys.nextElement();
				final String value = rb.getString(key);

				result.put(key, value);
			}
		} catch (Exception e) {
			result = null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Throwable ignore) {
				}
		}

		if (THROW_ON_LOAD_FAILURE && (result == null)) {
			throw new IllegalArgumentException("could not load ["+ name +"]");
		}

		return result;
	}

	private static final boolean THROW_ON_LOAD_FAILURE = true;
	private static final String SUFFIX = ".properties";
}

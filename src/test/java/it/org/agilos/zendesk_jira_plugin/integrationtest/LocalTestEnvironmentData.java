package it.org.agilos.zendesk_jira_plugin.integrationtest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class LocalTestEnvironmentData {
    private final String context;
    private final URL url;
    private final File xmlDataLocation;
    private final String edition;

    public LocalTestEnvironmentData() {
        this(loadProperties("test.server.properties", "localtest.properties"), null);
    }

    public LocalTestEnvironmentData(String xmlDataLocation) {
        this(loadProperties("test.server.properties", "localtest.properties"), xmlDataLocation);
    }

    public LocalTestEnvironmentData(Properties properties, String xmlDataLocation) {
        String protocol = properties.getProperty("jira.protocol", "http");
        String host = properties.getProperty("jira.host", "localhost");
        String port = properties.getProperty("jira.port", "8080");
        File unresolvedFileLocation = new File(xmlDataLocation == null ? properties.getProperty("jira.xml.data.location", "./xml") : xmlDataLocation);
        try {
            this.xmlDataLocation = unresolvedFileLocation.getCanonicalFile();
        }
        catch(IOException e) {
            throw new RuntimeException("IOException trying to resolve file " + unresolvedFileLocation);
        }
        String baseUrl = protocol + "://" + host + ":" + port + properties.getProperty("jira.context", "");
        context = properties.getProperty("jira.context", "");
        try {
            url = new URL(baseUrl);
        }
        catch(MalformedURLException e) {
            throw new RuntimeException("Malformed URL " + baseUrl);
        }
        edition = properties.getProperty("jira.edition", "standard");
    }

    public static Properties loadProperties(String key, String def) {
        Properties properties = new Properties();
        String propertiesFileName = "";
        try {
            propertiesFileName = System.getProperty(key, def);
            java.io.InputStream propStream = ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName);
            properties.load(propStream);
            return properties;
        }
        catch(IOException e) {
            System.out.println("Cannot load file " + propertiesFileName + " from CLASSPATH.");
            e.printStackTrace();
            throw new IllegalArgumentException("Could not load properties file " + propertiesFileName + " from classpath");
        }
    }

    public String getContext() {
        return context;
    }

    public URL getBaseUrl() {
        return url;
    }

    public File getXMLDataLocation() {
        return xmlDataLocation;
    }

    public File getJIRAHomeLocation() {
        File file = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "jira_autotest");
        try
        {
            return file.getCanonicalFile();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        throw new RuntimeException("Could not create JIRA home dir " + file);
    }

    protected String getEdition() {
        return edition;
    }
}

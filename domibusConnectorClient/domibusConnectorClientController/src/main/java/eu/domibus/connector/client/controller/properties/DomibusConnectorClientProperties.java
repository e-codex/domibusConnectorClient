package eu.domibus.connector.client.controller.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomibusConnectorClientProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientProperties.class);

    public static final String CONNECTOR_CLIENT_PROPERTIES_NAME = "connector-client.properties";

    public static String CONNECTOR_PROPERTIES_DIR_PATH = System.getProperty("user.dir") + File.separator + "conf";
    public static File CONNECTOR_PROPERTIES_DIR = new File(CONNECTOR_PROPERTIES_DIR_PATH);
    public static String CONNECTOR_PROPERTIES_FILE_PATH = CONNECTOR_PROPERTIES_DIR + File.separator + CONNECTOR_CLIENT_PROPERTIES_NAME;
    public static File CONNECTOR_PROPERTIES_FILE = new File(CONNECTOR_PROPERTIES_FILE_PATH);

    public static String LOG4J_CONFIG_FILE_PATH = CONNECTOR_PROPERTIES_DIR + File.separator + "log4j.properties";


//	public static final String CONNECTOR_CLIENT_NAME_KEY = Messages.getString("connector.client.name.key");
//	public static final String CONNECTOR_CLIENT_NAME_LABEL = Messages.getString("connector.client.label");

    //defines the address of the connector web service
    public static final String CONNECTOR_BACKEND_SERVICE_ADDRESS_KEY = "connector.backend.service.address";
    
  //defines the alias of the certificate of the connector backend webservice
    public static final String CONNECTOR_BACKEND_CERT_ALIAS_KEY = "connector.backend.cert.alias";


    public static final String GATEWAY_NAME_KEY = "gateway.name";
    public static final String GATEWAY_ROLE_KEY = "gateway.role";

    public static final String KEYSTORE_TYPE_KEY = "connector.client.keystore.type";
    public static final String KEYSTORE_PATH_KEY = "connector.client.keystore.path";
    public static final String KEYSTORE_PW_KEY = "connector.client.keystore.password";
    public static final String KEY_ALIAS_KEY = "connector.client.key.alias";
    public static final String KEY_PW_KEY = "connector.client.key.password";

    public static final String CHECK_OUTGOING_MESSAGES_PERIOD_KEY = "check.outgoing.messages.period";
    public static final String CHECK_INCOMING_MESSAGES_PERIOD_KEY = "check.incoming.messages.period";

    public static final String PROXY_ACTIVE_KEY = "http.proxy.enabled";
    public static final String PROXY_HOST_KEY = "http.proxy.host";
    public static final String PROXY_PORT_KEY = "http.proxy.port";
    public static final String PROXY_USERNAME_KEY = "http.proxy.user";
    public static final String PROXY_PASSWORD_KEY = "http.proxy.password";

    public static final String CONTENT_MAPPER_ACTIVE_KEY = "content.mapper.active";
    public static final String CONTENT_MAPPER_IMPL_CLASSNAME_KEY = "content.mapper.impl.classname";

    public static final String INCOMING_MSG_DIR_KEY = "incoming.msg.dir";
    public static final String CREATE_INCOMING_MSG_DIR_KEY = "create.incoming.msg.dir";

    public static final String OUTGOING_MSG_DIR_KEY = "outgoing.msg.dir";
    public static final String CREATE_OUTGOING_MSG_DIR_KEY = "create.outgoing.msg.dir";

    public static final String MSG_PROPERTY_FILE_NAME_KEY = "msg.property.file.name";


//	public static String connectorClientNameValue;

    public static String connectorBackendServiceAddressValue;
    public static String connectorBackendCertAliasValue;

    public static String gatewayNameValue;
    public static String gatewayRoleValue;

    public static String keystoreTypeValue = "JKS";
    public static String keystoreFileValue;
    public static String keystorePasswordValue;
    public static String keyAlias;
    public static String keyPassword;

    public static long checkIncomingPeriodValue;
    public static long checkOutgoingPeriodValue;

    public static boolean proxyEnabled;
    public static String proxyHost;
    public static String proxyPort;
    public static String proxyUser;
    public static String proxyPassword;

    public static String incomingMessagesDirectory;
    public static boolean createIncomingMessagesDirectory;
    public static String outgoingMessagesDirectory;
    public static boolean createOutgoingMessagesDirectory;
    public static String messagePropertiesFileName = "message.properties";

    public static boolean useContentMapper;
    public static String contentMapperImplementaitonClassName;

    public final static Properties properties = new Properties();

    public static void loadConnectorProperties() throws Exception {
        String connectorProperties = System.getProperty(CONNECTOR_CLIENT_PROPERTIES_NAME);
        if (connectorProperties != null && connectorProperties.length() > 0) {
            try {
                CONNECTOR_PROPERTIES_DIR_PATH = connectorProperties.substring(0, connectorProperties.lastIndexOf(File.separator));
                CONNECTOR_PROPERTIES_DIR = new File(CONNECTOR_PROPERTIES_DIR_PATH);
            } catch (Exception e) {
                LOGGER.error("Exception....!", e);
            }
            CONNECTOR_PROPERTIES_FILE_PATH = connectorProperties;
            CONNECTOR_PROPERTIES_FILE = new File(CONNECTOR_PROPERTIES_FILE_PATH);
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(CONNECTOR_PROPERTIES_FILE);
        } catch (FileNotFoundException e1) {
            String error = String.format("Properties file [%s] not found!", CONNECTOR_PROPERTIES_FILE);
            LOGGER.error(error, e1);
            e1.printStackTrace();
            throw e1;
        }
        try {
            properties.load(fileInputStream);
        } catch (Exception e1) {
            LOGGER.error(String.format("Exception while loading properties from file [%s]", CONNECTOR_PROPERTIES_FILE), e1);
            e1.printStackTrace();
            throw e1;
        }

        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LOGGER.trace("Loaded Properties are [{}]", properties);


        setPropertyValues();

    }


    private static void setPropertyValues() {

//		connectorClientNameValue = properties.getProperty(CONNECTOR_CLIENT_NAME_KEY);

        connectorBackendServiceAddressValue = properties.getProperty(CONNECTOR_BACKEND_SERVICE_ADDRESS_KEY);
        connectorBackendCertAliasValue = properties.getProperty(CONNECTOR_BACKEND_CERT_ALIAS_KEY);

        gatewayNameValue = properties.getProperty(GATEWAY_NAME_KEY);
        gatewayRoleValue = properties.getProperty(GATEWAY_ROLE_KEY);

        keystoreTypeValue = properties.getProperty(KEYSTORE_TYPE_KEY);
        keystoreFileValue = properties.getProperty(KEYSTORE_PATH_KEY);
        keystorePasswordValue = properties.getProperty(KEYSTORE_PW_KEY);
        keyAlias = properties.getProperty(KEY_ALIAS_KEY);
        keyPassword = properties.getProperty(KEY_PW_KEY);

        checkIncomingPeriodValue = Long.parseLong(properties.getProperty(CHECK_INCOMING_MESSAGES_PERIOD_KEY));
        checkOutgoingPeriodValue = Long.parseLong(properties.getProperty(CHECK_OUTGOING_MESSAGES_PERIOD_KEY));

        proxyEnabled = Boolean.parseBoolean(properties.getProperty(PROXY_ACTIVE_KEY));
        proxyHost = properties.getProperty(PROXY_HOST_KEY);
        proxyPort = properties.getProperty(PROXY_PORT_KEY);
        proxyUser = properties.getProperty(PROXY_USERNAME_KEY);
        proxyPassword = properties.getProperty(PROXY_PASSWORD_KEY);


        useContentMapper = Boolean.parseBoolean(properties.getProperty(CONTENT_MAPPER_ACTIVE_KEY));
        contentMapperImplementaitonClassName = properties.getProperty(CONTENT_MAPPER_IMPL_CLASSNAME_KEY);

        incomingMessagesDirectory = properties.getProperty(INCOMING_MSG_DIR_KEY) != null && !properties.getProperty(INCOMING_MSG_DIR_KEY).isEmpty() ? properties.getProperty(INCOMING_MSG_DIR_KEY) : null;
        createIncomingMessagesDirectory = Boolean.parseBoolean(properties.getProperty(CREATE_INCOMING_MSG_DIR_KEY));
        outgoingMessagesDirectory = properties.getProperty(OUTGOING_MSG_DIR_KEY) != null && !properties.getProperty(OUTGOING_MSG_DIR_KEY).isEmpty() ? properties.getProperty(OUTGOING_MSG_DIR_KEY) : null;
        createOutgoingMessagesDirectory = Boolean.parseBoolean(properties.getProperty(CREATE_OUTGOING_MSG_DIR_KEY));
        messagePropertiesFileName = properties.getProperty(MSG_PROPERTY_FILE_NAME_KEY) != null && !properties.getProperty(MSG_PROPERTY_FILE_NAME_KEY).isEmpty() ? properties.getProperty(MSG_PROPERTY_FILE_NAME_KEY) : messagePropertiesFileName;

    }

    private static void putPropertyValues() {
//		properties.put(CONNECTOR_CLIENT_NAME_KEY, connectorClientNameValue!=null?connectorClientNameValue:"");

        properties.put(CONNECTOR_BACKEND_SERVICE_ADDRESS_KEY, connectorBackendServiceAddressValue != null ? connectorBackendServiceAddressValue : "");
        properties.put(CONNECTOR_BACKEND_CERT_ALIAS_KEY, connectorBackendCertAliasValue != null ? connectorBackendCertAliasValue : "");

        properties.put(GATEWAY_NAME_KEY, gatewayNameValue != null ? gatewayNameValue : "");
        properties.put(GATEWAY_ROLE_KEY, gatewayRoleValue != null ? gatewayRoleValue : "");

        properties.put(KEYSTORE_TYPE_KEY, keystoreTypeValue != null ? keystoreTypeValue : "");
        properties.put(KEYSTORE_PATH_KEY, keystoreFileValue != null ? keystoreFileValue : "");
        properties.put(KEYSTORE_PW_KEY, keystorePasswordValue != null ? keystorePasswordValue : "");
        properties.put(KEY_ALIAS_KEY, keyAlias != null ? keyAlias : "");
        properties.put(KEY_PW_KEY, keyPassword != null ? keyPassword : "");

        properties.put(CHECK_INCOMING_MESSAGES_PERIOD_KEY, Long.toString(checkIncomingPeriodValue));
        properties.put(CHECK_OUTGOING_MESSAGES_PERIOD_KEY, Long.toString(checkOutgoingPeriodValue));

        properties.put(PROXY_ACTIVE_KEY, Boolean.toString(proxyEnabled));
        properties.put(PROXY_HOST_KEY, proxyHost != null ? proxyHost : "");
        properties.put(PROXY_PORT_KEY, proxyPort != null ? proxyPort : "");
        properties.put(PROXY_USERNAME_KEY, proxyUser != null ? proxyUser : "");
        properties.put(PROXY_PASSWORD_KEY, proxyPassword != null ? proxyPassword : "");

        properties.put(CONTENT_MAPPER_ACTIVE_KEY, Boolean.toString(useContentMapper));
        properties.put(CONTENT_MAPPER_IMPL_CLASSNAME_KEY, contentMapperImplementaitonClassName != null ? contentMapperImplementaitonClassName : "");

        properties.put(INCOMING_MSG_DIR_KEY, incomingMessagesDirectory != null ? incomingMessagesDirectory : "");
        properties.put(CREATE_INCOMING_MSG_DIR_KEY, Boolean.toString(createIncomingMessagesDirectory));
        properties.put(OUTGOING_MSG_DIR_KEY, outgoingMessagesDirectory != null ? outgoingMessagesDirectory : "");
        properties.put(CREATE_OUTGOING_MSG_DIR_KEY, Boolean.toString(createOutgoingMessagesDirectory));
        properties.put(MSG_PROPERTY_FILE_NAME_KEY, messagePropertiesFileName != null ? messagePropertiesFileName : "");
    }

    public static boolean storeConnectorProperties() {
        if (!CONNECTOR_PROPERTIES_DIR.exists()) {
            CONNECTOR_PROPERTIES_DIR.mkdirs();
        }
        if (!CONNECTOR_PROPERTIES_FILE.exists()) {
            try {
                CONNECTOR_PROPERTIES_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        putPropertyValues();
        Set<String> keys = properties.stringPropertyNames();

        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(CONNECTOR_PROPERTIES_FILE));
            String line;
            String input = "";
            while ((line = file.readLine()) != null) {
                if (line.contains("=") && !line.startsWith("*") && !line.startsWith("/")) {
                    String key = line.substring(0, line.indexOf("="));
                    String value = properties.getProperty(key);
                    input += key + "=" + value + '\n';
                    keys.remove(key);
                } else {
                    input += line + '\n';
                }
            }
            file.close();

            if (!keys.isEmpty()) {
                for (String key : keys) {
                    String value = properties.getProperty(key);
                    input += key + "=" + value + '\n';
                }
            }

            FileOutputStream fileOut = new FileOutputStream(CONNECTOR_PROPERTIES_FILE);
            fileOut.write(input.getBytes());
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    public static boolean storeOld() {
        if (!CONNECTOR_PROPERTIES_DIR.exists()) {
            CONNECTOR_PROPERTIES_DIR.mkdirs();
        }
        if (!CONNECTOR_PROPERTIES_FILE.exists()) {
            try {
                CONNECTOR_PROPERTIES_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(CONNECTOR_PROPERTIES_FILE);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return false;
        }

        putPropertyValues();

        try {
            properties.store(fos, null);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }

        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


}

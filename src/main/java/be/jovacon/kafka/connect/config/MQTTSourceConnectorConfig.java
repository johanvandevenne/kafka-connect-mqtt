package be.jovacon.kafka.connect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef.Type;

public class MQTTSourceConnectorConfig extends AbstractConfig {

    public static final String BROKER = "mqtt.broker";
    public static final String BROKER_DOC = "Host and port of the MQTT broker, eg: tcp://192.168.1.1:1883";

    public static final String CLIENTID = "mqtt.clientID";
    public static final String CLIENTID_DOC = "clientID";

    public static final String MQTT_TOPIC = "mqtt.topic";
    public static final String MQTT_TOPIC_DOC = "List of topic names to subscribe to";

    public static final String MQTT_QOS = "mqtt.qos";
    public static final String MQTT_QOS_DOC = "Quality of service MQTT messaging, default is 1 (at least once)";

    public static final String MQTT_ARC = "mqtt.automaticReconnect";
    public static final String MQTT_ARC_DOC = "set Automatic reconnect, default true";

    public static final String MQTT_KEEPALIVEINTERVAL = "mqtt.keepAliveInterval";
    public static final String MQTT_KEEPALIVEINTERVAL_DOC = "set the keepalive interval, default is 60 seconds";

    public static final String MQTT_CLEANSESSION = "mqtt.cleanSession";
    public static final String MQTT_CLEANSESSION_DOC = "Sets whether the client and server should remember state across restarts and reconnects, default is true";

    public static final String MQTT_CONNECTIONTIMEOUT = "mqtt.connectionTimeout";
    public static final String MQTT_CONNECTIONTIMEOUT_DOC = "Sets the connection timeout, default is 30";

    public static final String MQTT_USERNAME = "mqtt.userName";
    public static final String MQTT_USERNAME_DOC = "Sets the username for the MQTT connection timeout, default is \"\"";

    public static final String MQTT_PASSWORD = "mqtt.password";
    public static final String MQTT_PASSWORD_DOC = "Sets the password for the MQTT connection timeout, default is \"\"";

    public static final String MQTT_CA_CERT = "mqtt.ssl.caCert";
    public static final String MQTT_CA_CERT_DOC = "Sets the PEM encoded CA certificate to verify server identity with, default is \"\"";

    public static final String MQTT_CLIENT_CERT = "mqtt.ssl.clientCert";
    public static final String MQTT_CLIENT_CERT_DOC = "Sets the PEM encoded client certificate to authenticate against the server with, default is \"\"";

    public static final String MQTT_CLIENT_KEY = "mqtt.ssl.clientKey";
    public static final String MQTT_CLIENT_KEY_DOC = "Sets the PEM encoded private key of the client, default is \"\"";

    public static final String MQTT_TLS_VERSION = "mqtt.ssl.tlsVersion";
    public static final String MQTT_TLS_VERSION_DOC = "Sets the TLS version to use, default is \"TLSV1.2\"";

    public static final String KAFKA_TOPIC = "kafka.topic";
    public static final String KAFKA_TOPIC_DOC = "List of kafka topics to publish to";

    public MQTTSourceConnectorConfig(Map<?, ?> originals) {
        super(configDef(), originals);
    }

    public static ConfigDef configDef() {
        return new ConfigDef()
                .define(BROKER,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        BROKER_DOC)
                .define(CLIENTID,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        CLIENTID_DOC)
                .define(MQTT_TOPIC,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        MQTT_TOPIC_DOC)
                .define(KAFKA_TOPIC,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        KAFKA_TOPIC_DOC)
                .define(MQTT_QOS,
                        ConfigDef.Type.INT,
                        1,
                        ConfigDef.Range.between(1,3),
                        ConfigDef.Importance.MEDIUM,
                        MQTT_QOS_DOC)
                .define(MQTT_ARC,
                        ConfigDef.Type.BOOLEAN,
                        true,
                        ConfigDef.Importance.MEDIUM,
                        MQTT_ARC_DOC)
                .define(MQTT_KEEPALIVEINTERVAL,
                        ConfigDef.Type.INT,
                        60,
                        ConfigDef.Importance.LOW,
                        MQTT_KEEPALIVEINTERVAL_DOC)
                .define(MQTT_CLEANSESSION,
                        ConfigDef.Type.BOOLEAN,
                        true,
                        ConfigDef.Importance.LOW,
                        MQTT_CLEANSESSION_DOC)
                .define(MQTT_CONNECTIONTIMEOUT,
                        ConfigDef.Type.INT,
                        30,
                        ConfigDef.Importance.LOW,
                        MQTT_CONNECTIONTIMEOUT_DOC)
                .define(MQTT_USERNAME,
                        ConfigDef.Type.STRING,
                        "",
                        ConfigDef.Importance.LOW,
                        MQTT_USERNAME_DOC)
                .define(MQTT_PASSWORD,
                        ConfigDef.Type.PASSWORD,
                        "",
                        ConfigDef.Importance.LOW,
                        MQTT_PASSWORD_DOC)
                .define(MQTT_CA_CERT,
                        Type.STRING,
                        "",
                        ConfigDef.Importance.LOW,
                        MQTT_CA_CERT_DOC)
                .define(MQTT_CLIENT_CERT,
                        Type.STRING,
                        "",
                        ConfigDef.Importance.LOW,
                        MQTT_CLIENT_CERT_DOC)
                .define(MQTT_CLIENT_KEY,
                        Type.STRING,
                        "",
                        ConfigDef.Importance.LOW,
                        MQTT_CLIENT_KEY_DOC)
                .define(MQTT_TLS_VERSION,
                        Type.STRING,
                        "TLSV1.2",
                        ConfigDef.Importance.LOW,
                        MQTT_TLS_VERSION_DOC)
                ;
    }
}

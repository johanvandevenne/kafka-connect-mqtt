package nl.nedcar.kafka.connect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class MQTTSinkConnectorConfig extends AbstractConfig {

    public static final String BROKER = "mqtt.broker";
    public static final String BROKER_DOC = "Host and port of the MQTT broker, eg: tcp://192.168.1.1:1883";

    public static final String CLIENTID = "mqtt.clientID";
    public static final String CLIENTID_DOC = "clientID";

    public static final String MQTT_TOPIC = "mqtt.topic";
    public static final String MQTT_TOPIC_DOC = "List of topic names to subscribe to";

    public static final String MQTT_ARC = "mqtt.automaticReconnect";
    public static final String MQTT_ARC_DOC = "set Automatic reconnect, default true";

    public static final String MQTT_KEEPALIVEINTERVAL = "mqtt.keepAliveInterval";
    public static final String MQTT_KEEPALIVEINTERVAL_DOC = "set the keepalive interval, default is 60 seconds";

    public static final String MQTT_CLEANSESSION = "mqtt.cleanSession";
    public static final String MQTT_CLEANSESSION_DOC = "Sets whether the client and server should remember state across restarts and reconnects, default is true";

    public static final String MQTT_CONNECTIONTIMEOUT = "mqtt.connectionTimeout";
    public static final String MQTT_CONNECTIONTIMEOUT_DOC = "Sets the connection timeout, default is 30";

    public static final String KAFKA_TOPIC = "topics";
    public static final String KAFKA_TOPIC_DOC = "List of kafka topics to consume from";

    public static final String MQTT_QOS = "mqtt.qos";
    public static final String MQTT_QOS_DOC = "Quality of service MQTT messaging, default is 1 (at least once)";

    public static final String MQTT_USERNAME = "mqtt.userName";
    public static final String MQTT_USERNAME_DOC = "Sets the username for the MQTT connection timeout, default is \"\"";

    public static final String MQTT_PASSWORD = "mqtt.password";
    public static final String MQTT_PASSWORD_DOC = "Sets the password for the MQTT connection timeout, default is \"\"";

    public MQTTSinkConnectorConfig(Map<?, ?> originals) {
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
                        ConfigDef.Type.LIST,
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
                        MQTT_PASSWORD_DOC);
    }
}

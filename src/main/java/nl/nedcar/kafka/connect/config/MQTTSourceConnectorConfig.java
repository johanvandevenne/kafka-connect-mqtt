package nl.nedcar.kafka.connect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class MQTTSourceConnectorConfig extends AbstractConfig {

    public static final String BROKER = "broker";
    public static final String BROKER_DOC = "Host and port of the MQTT broker, eg: tcp://192.168.1.1:1883";

    public static final String CLIENTID = "clientID";
    public static final String CLIENTID_DOC = "clientID";

    public static final String MQTT_TOPIC = "mqtt.topic";
    public static final String MQTT_TOPIC_DOC = "List of topic names to subscribe to";

    public static final String KAFKA_TOPIC = "kafka.topic";
    public static final String KAFKA_TOPIC_DOC = "List of kafka topics to publish to";

    public MQTTSourceConnectorConfig(Map<?, ?> originals) {
        super(configDef(), originals);
    }

    protected static ConfigDef configDef() {
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
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        MQTT_TOPIC_DOC)
                .define(KAFKA_TOPIC,
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        KAFKA_TOPIC_DOC);
    }
}

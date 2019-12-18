package nl.nedcar.kafka.connect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class MQTTSourceConnectorConfig extends AbstractConfig {

    public MQTTSourceConnectorConfig(Map<?, ?> originals) {
        super(configDef(), originals);
    }

    protected static ConfigDef configDef() {
        return new ConfigDef()
                .define("broker",
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        "host and port of the MQTT broker")
                .define("clientID",
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        "ClientID")
                .define("mqtt.topic",
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        "Names of Topics to subscribe to")
                .define("kafka.topic",
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        "Names of Kafka topics to send to");
    }
}

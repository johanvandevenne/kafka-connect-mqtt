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
                .define("bucket",
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        "Name of the bucket to import objects from")
                .define("prefix.whitelist",
                        ConfigDef.Type.LIST,
                        ConfigDef.Importance.HIGH,
                        "Whitelist of object key prefixes")
                .define("topic",
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        "Name of Kafka topic to produce to");
    }
}

package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Converts a MQTT message to a Kafka message
 */
public class MQTTSourceConverter {

    private MQTTSourceConnectorConfig mqttSourceConnectorConfig;

    private Logger log = LoggerFactory.getLogger(MQTTSourceConverter.class);

    public MQTTSourceConverter(MQTTSourceConnectorConfig mqttSourceConnectorConfig) {
        this.mqttSourceConnectorConfig = mqttSourceConnectorConfig;
    }

    protected SourceRecord convert(String topic, MqttMessage mqttMessage) {
        log.trace("Converting MQTT message: " + mqttMessage);
        ConnectHeaders headers = new ConnectHeaders();
        headers.addInt("mqtt.message.id", mqttMessage.getId());
        headers.addInt("mqtt.message.qos", mqttMessage.getQos());
        headers.addBoolean("mqtt.message.duplicate", mqttMessage.isDuplicate());

        SourceRecord sourceRecord = new SourceRecord(new HashMap<>(),
                new HashMap<>(),
                this.mqttSourceConnectorConfig.getString(MQTTSourceConnectorConfig.KAFKA_TOPIC),
                (Integer) null,
                Schema.STRING_SCHEMA,
                topic,
                Schema.BYTES_SCHEMA,
                mqttMessage.getPayload(),
                System.currentTimeMillis(),
                headers);
        log.trace("Converted MQTT Message: " + sourceRecord);
        return sourceRecord;
    }
}
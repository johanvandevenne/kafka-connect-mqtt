package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;

public class MQTTSourceConverter {

    private MQTTSourceConnectorConfig mqttSourceConnectorConfig;

    public MQTTSourceConverter(MQTTSourceConnectorConfig mqttSourceConnectorConfig) {
        this.mqttSourceConnectorConfig = mqttSourceConnectorConfig;
    }

    protected SourceRecord convert(MqttMessage mqttMessage) {
        ConnectHeaders headers = new ConnectHeaders();
        headers.addInt("mqtt.message.id", mqttMessage.getId());
        headers.addInt("mqtt.message.qos", mqttMessage.getQos());
        headers.addBoolean("mqtt.message.duplicate", mqttMessage.isDuplicate());

        return new SourceRecord(new HashMap<>(),
                new HashMap<>(),
                mqttSourceConnectorConfig.getString(MQTTSourceConnectorConfig.KAFKA_TOPIC),
                (Integer) null,
                Schema.STRING_SCHEMA,
                mqttSourceConnectorConfig.getString(MQTTSourceConnectorConfig.MQTT_TOPIC),
                Schema.BYTES_SCHEMA,
                mqttMessage.getPayload(),
                null,
                headers);
    }
}

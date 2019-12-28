package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSinkConnectorConfig;
import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Converts a MQTT message to a Kafka message
 */
public class MQTTSinkConverter {

    private MQTTSinkConnectorConfig mqttSinkConnectorConfig;

    private Logger log = LoggerFactory.getLogger(MQTTSinkConverter.class);

    public MQTTSinkConverter(MQTTSinkConnectorConfig mqttSinkConnectorConfig) {
        this.mqttSinkConnectorConfig = mqttSinkConnectorConfig;
    }

    protected MqttMessage convert(SinkRecord sinkRecord) {
        log.trace("Converting Kafka message");

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(((String)sinkRecord.value()).getBytes());
        mqttMessage.setQos(this.mqttSinkConnectorConfig.getInt(MQTTSourceConnectorConfig.MQTT_QOS));
        log.trace("Result MQTTMessage: " + mqttMessage);
        return mqttMessage;
    }
}

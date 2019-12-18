package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static java.util.Collections.EMPTY_MAP;

public class MQTTSourceConverter {

    private MQTTSourceConnectorConfig mqttSourceConnectorConfig;

    private Logger log = LoggerFactory.getLogger(MQTTSourceConverter.class);

    public MQTTSourceConverter(MQTTSourceConnectorConfig mqttSourceConnectorConfig) {
        this.mqttSourceConnectorConfig = mqttSourceConnectorConfig;
    }

    protected SourceRecord convert(String topic, MqttMessage mqttMessage) {
        log.info("Converting message: " + mqttMessage);
        ConnectHeaders headers = new ConnectHeaders();
        headers.addInt("mqtt.message.id", mqttMessage.getId());
        headers.addInt("mqtt.message.qos", mqttMessage.getQos());
        headers.addBoolean("mqtt.message.duplicate", mqttMessage.isDuplicate());

        log.info("Converting record");

        //SourceRecord result = new SourceRecord(EMPTY_MAP, EMPTY_MAP, this.config.kafkaTopic, (Integer)null, Schema.STRING_SCHEMA, mqttTopic, Schema.BYTES_SCHEMA, message.getPayload(), this.time.milliseconds(), headers);
        //log.info(mqttSourceConnectorConfig.getString(MQTTSourceConnectorConfig.KAFKA_TOPIC));
        //log.info(mqttSourceConnectorConfig.getString(MQTTSourceConnectorConfig.MQTT_TOPIC));

        SourceRecord sourceRecord = new SourceRecord(new HashMap<>(),
                new HashMap<>(),
                "TempTopic",
                (Integer) null,
                Schema.STRING_SCHEMA,
                topic,
                Schema.BYTES_SCHEMA,
                mqttMessage.getPayload(),
                System.currentTimeMillis(),
                headers);

        log.info("Record1: " + sourceRecord);
        return sourceRecord;
    }
}

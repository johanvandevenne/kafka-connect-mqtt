package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSinkConnectorConfig;
import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class MQTTSinkTask extends SinkTask {

    private Logger log = LoggerFactory.getLogger(MQTTSinkTask.class);
    private MQTTSinkConnectorConfig config;
    private MQTTSinkConverter mqttSinkConverter;

    private IMqttClient mqttClient;

    @Override
    public String version() {
        return Version.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        config = new MQTTSinkConnectorConfig(map);
        mqttSinkConverter = new MQTTSinkConverter(config);
        try {
            mqttClient = new MqttClient(config.getString(MQTTSinkConnectorConfig.BROKER), config.getString(MQTTSinkConnectorConfig.CLIENTID), new MemoryPersistence());

            log.info("Connecting to MQTT Broker " + config.getString(MQTTSourceConnectorConfig.BROKER));
            mqttClient.connect();
            log.info("Connected to MQTT Broker");

        }
        catch (MqttException e) {
            throw new ConnectException(e);
        }
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        try {
            for (Iterator<SinkRecord> iterator = collection.iterator(); iterator.hasNext(); ) {
                SinkRecord sinkRecord = iterator.next();
                MqttMessage mqttMessage = mqttSinkConverter.convert(sinkRecord);
                if (!mqttClient.isConnected()) mqttClient.connect();
                mqttClient.publish(this.config.getString(MQTTSinkConnectorConfig.MQTT_TOPIC), mqttMessage);
            }
        } catch (MqttException e) {
            throw new ConnectException(e);
        }

    }

    @Override
    public void stop() {
        if (mqttClient.isConnected()) {
            try {
                log.debug("Disconnecting from MQTT Broker " + config.getString(MQTTSourceConnectorConfig.BROKER));
                mqttClient.disconnect();
            } catch (MqttException mqttException) {
                log.error("Exception thrown while disconnecting client.", mqttException);
            }
        }
    }
}

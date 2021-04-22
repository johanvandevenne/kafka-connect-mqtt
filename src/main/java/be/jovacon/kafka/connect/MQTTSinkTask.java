package be.jovacon.kafka.connect;

import be.jovacon.kafka.connect.config.MQTTSinkConnectorConfig;
import be.jovacon.kafka.connect.config.MQTTSourceConnectorConfig;
import be.jovacon.kafka.connect.util.SslKit;
import java.io.File;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of the Kafka Connect Sink task
 */
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
            connect(mqttClient);
            log.info("Connected to MQTT Broker. This connector publishes to the " + this.config.getString(MQTTSinkConnectorConfig.MQTT_TOPIC) + " topic");

        }
        catch (Exception e) {
            throw new ConnectException(e);
        }
    }

    private void connect(IMqttClient mqttClient) throws Exception{
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(config.getBoolean(MQTTSinkConnectorConfig.MQTT_CLEANSESSION));
        connOpts.setKeepAliveInterval(config.getInt(MQTTSinkConnectorConfig.MQTT_KEEPALIVEINTERVAL));
        connOpts.setConnectionTimeout(config.getInt(MQTTSinkConnectorConfig.MQTT_CONNECTIONTIMEOUT));
        connOpts.setAutomaticReconnect(config.getBoolean(MQTTSinkConnectorConfig.MQTT_ARC));

        if (!config.getString(MQTTSinkConnectorConfig.MQTT_USERNAME).equals("") && !config.getPassword(MQTTSinkConnectorConfig.MQTT_PASSWORD).equals("")) {
            connOpts.setUserName(config.getString(MQTTSinkConnectorConfig.MQTT_USERNAME));
            connOpts.setPassword(config.getPassword(MQTTSinkConnectorConfig.MQTT_PASSWORD).value().toCharArray());
        }

        if (!config.getString(MQTTSinkConnectorConfig.MQTT_CA_CERT).equals("") && !config.getString(MQTTSourceConnectorConfig.MQTT_CLIENT_CERT).equals("")
            && !config.getString(MQTTSinkConnectorConfig.MQTT_CLIENT_KEY).equals("")) {
            connOpts.setSocketFactory(
                SslKit.getSocketFactory(
                    new File(config.getString(MQTTSinkConnectorConfig.MQTT_CA_CERT)),
                    new File(config.getString(MQTTSinkConnectorConfig.MQTT_CLIENT_CERT)),
                    new File(config.getString(MQTTSinkConnectorConfig.MQTT_CLIENT_KEY)),
                    config.getString(MQTTSinkConnectorConfig.MQTT_TLS_VERSION)));
        } else if (!config.getString(MQTTSinkConnectorConfig.MQTT_CA_CERT).equals("")) {
            connOpts.setSocketFactory(
                SslKit.getSocketFactory(
                    new File(config.getString(MQTTSinkConnectorConfig.MQTT_CA_CERT)),
                    config.getString(MQTTSinkConnectorConfig.MQTT_TLS_VERSION)));
        }

        log.info("MQTT Connection properties: " + connOpts);
        mqttClient.connect(connOpts);
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        try {
            for (Iterator<SinkRecord> iterator = collection.iterator(); iterator.hasNext(); ) {
                SinkRecord sinkRecord = iterator.next();
                log.debug("Received message with offset " + sinkRecord.kafkaOffset());
                MqttMessage mqttMessage = mqttSinkConverter.convert(sinkRecord);
                if (!mqttClient.isConnected()) mqttClient.connect();
                log.debug("Publishing message to topic " + this.config.getString(MQTTSinkConnectorConfig.MQTT_TOPIC) + " with payload " + new String(mqttMessage.getPayload()));
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
                log.debug("Disconnecting from MQTT Broker " + config.getString(MQTTSinkConnectorConfig.BROKER));
                mqttClient.disconnect();
            } catch (MqttException mqttException) {
                log.error("Exception thrown while disconnecting client.", mqttException);
            }
        }
    }
}

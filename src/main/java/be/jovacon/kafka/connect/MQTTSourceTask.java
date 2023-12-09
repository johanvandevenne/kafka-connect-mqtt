package be.jovacon.kafka.connect;

import be.jovacon.kafka.connect.config.MQTTSourceConnectorConfig;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDeque;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDequeBuilder;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Actual implementation of the Kafka Connect MQTT Source Task
 */
public class MQTTSourceTask extends SourceTask implements IMqttMessageListener {

    private Logger log = LoggerFactory.getLogger(MQTTSourceConnector.class);
    private MQTTSourceConnectorConfig config;
    private MQTTSourceConverter mqttSourceConverter;
    private SourceRecordDeque sourceRecordDeque;

    private IMqttClient mqttClient;

    public void start(Map<String, String> props) {
        config = new MQTTSourceConnectorConfig(props);
        mqttSourceConverter = new MQTTSourceConverter(config);
        this.sourceRecordDeque = SourceRecordDequeBuilder.of().batchSize(4096).emptyWaitMs(100).maximumCapacityTimeoutMs(60000).maximumCapacity(50000).build();
        try {
            String clientId = config.getString(MQTTSourceConnectorConfig.CLIENTID);
            log.info("Connecting with clientID=" + clientId);
            mqttClient = new MqttClient(config.getString(MQTTSourceConnectorConfig.BROKER), clientId, new MemoryPersistence());
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.error("MQTT Connection Lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    onMessageRecieved("messageArrivedCallback", topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.info("MQTT Delivery Complete"+ token);
                }
            });
            log.info("Connecting to MQTT Broker " + config.getString(MQTTSourceConnectorConfig.BROKER));
            connect(mqttClient);
            log.info("Connected to MQTT Broker");

            String topicSubscription = this.config.getString(MQTTSourceConnectorConfig.MQTT_TOPIC);
            int qosLevel = this.config.getInt(MQTTSourceConnectorConfig.MQTT_QOS);

            log.info("Subscribing to " + topicSubscription + " with QOS " + qosLevel);
            mqttClient.subscribe(topicSubscription, qosLevel, (topic, message) -> {
                onMessageRecieved("subscribeCallback", topic, message);
            });
            log.info("Subscribed to " + topicSubscription + " with QOS " + qosLevel);
        }
        catch (MqttException e) {
            throw new ConnectException(e);
        }
    }

    public void onMessageRecieved(String source, String topic, MqttMessage message) throws Exception {
        log.info(String.format("Message arrived in connector from topic %s from source %s", topic, source));
        SourceRecord record = mqttSourceConverter.convert(topic, message);
        log.info(String.format("Converted record: %s, from source: %s", record, source));
        sourceRecordDeque.add(record);
    }

    private void connect(IMqttClient mqttClient) throws MqttException{
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(config.getBoolean(MQTTSourceConnectorConfig.MQTT_CLEANSESSION));
        connOpts.setKeepAliveInterval(config.getInt(MQTTSourceConnectorConfig.MQTT_KEEPALIVEINTERVAL));
        connOpts.setConnectionTimeout(config.getInt(MQTTSourceConnectorConfig.MQTT_CONNECTIONTIMEOUT));
        connOpts.setAutomaticReconnect(config.getBoolean(MQTTSourceConnectorConfig.MQTT_ARC));

        if (!config.getString(MQTTSourceConnectorConfig.MQTT_USERNAME).equals("") && !config.getPassword(MQTTSourceConnectorConfig.MQTT_PASSWORD).equals("")) {
            connOpts.setUserName(config.getString(MQTTSourceConnectorConfig.MQTT_USERNAME));
            connOpts.setPassword(config.getPassword(MQTTSourceConnectorConfig.MQTT_PASSWORD).value().toCharArray());
        }

        log.info("MQTT Connection properties: " + connOpts);

        mqttClient.connect(connOpts);
    }

    /**
     * method is called periodically by the Connect framework
     *
     * @return
     * @throws InterruptedException
     */
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = sourceRecordDeque.getBatch();
        log.trace("Records returning to poll(): " + records);
        return records;
    }

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

    public String version() {
        return Version.getVersion();
    }


    /**
     * Callback method when a MQTT message arrives at the Topic
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("Message arrived in connector from topic " + topic);
        SourceRecord record = mqttSourceConverter.convert(topic, mqttMessage);
        log.debug("Converted record: " + record);
        sourceRecordDeque.add(record);
    }
}

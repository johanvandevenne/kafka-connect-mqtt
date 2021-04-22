package be.jovacon.kafka.connect;

import be.jovacon.kafka.connect.config.MQTTSourceConnectorConfig;
import be.jovacon.kafka.connect.util.SslKit;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDeque;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDequeBuilder;
import java.io.File;
import java.security.Security;
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
            mqttClient = new MqttClient(config.getString(MQTTSourceConnectorConfig.BROKER), config.getString(MQTTSourceConnectorConfig.CLIENTID), new MemoryPersistence());

            log.info("Connecting to MQTT Broker " + config.getString(MQTTSourceConnectorConfig.BROKER));
            connect(mqttClient);
            log.info("Connected to MQTT Broker");

            String topicSubscription = this.config.getString(MQTTSourceConnectorConfig.MQTT_TOPIC);
            int qosLevel = this.config.getInt(MQTTSourceConnectorConfig.MQTT_QOS);

            log.info("Subscribing to " + topicSubscription + " with QOS " + qosLevel);
            mqttClient.subscribe(topicSubscription, qosLevel, (topic, message) -> {
                log.debug("Message arrived in connector from topic " + topic);
                SourceRecord record = mqttSourceConverter.convert(topic, message);
                log.debug("Converted record: " + record);
                sourceRecordDeque.add(record);
            });
            log.info("Subscribed to " + topicSubscription + " with QOS " + qosLevel);
        }
        catch (Exception e) {
            throw new ConnectException(e);
        }
    }

    private void connect(IMqttClient mqttClient) throws Exception{
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(config.getBoolean(MQTTSourceConnectorConfig.MQTT_CLEANSESSION));
        connOpts.setKeepAliveInterval(config.getInt(MQTTSourceConnectorConfig.MQTT_KEEPALIVEINTERVAL));
        connOpts.setConnectionTimeout(config.getInt(MQTTSourceConnectorConfig.MQTT_CONNECTIONTIMEOUT));
        connOpts.setAutomaticReconnect(config.getBoolean(MQTTSourceConnectorConfig.MQTT_ARC));

        if (!config.getString(MQTTSourceConnectorConfig.MQTT_USERNAME).equals("") && !config.getPassword(MQTTSourceConnectorConfig.MQTT_PASSWORD).equals("")) {
            connOpts.setUserName(config.getString(MQTTSourceConnectorConfig.MQTT_USERNAME));
            connOpts.setPassword(config.getPassword(MQTTSourceConnectorConfig.MQTT_PASSWORD).value().toCharArray());
        }

        if (!config.getString(MQTTSourceConnectorConfig.MQTT_CA_CERT).equals("") && !config.getString(MQTTSourceConnectorConfig.MQTT_CLIENT_CERT).equals("")
            && !config.getString(MQTTSourceConnectorConfig.MQTT_CLIENT_KEY).equals("")) {
            connOpts.setSocketFactory(
                SslKit.getSocketFactory(
                    new File(config.getString(MQTTSourceConnectorConfig.MQTT_CA_CERT)),
                    new File(config.getString(MQTTSourceConnectorConfig.MQTT_CLIENT_CERT)),
                    new File(config.getString(MQTTSourceConnectorConfig.MQTT_CLIENT_KEY)),
                    config.getString(MQTTSourceConnectorConfig.MQTT_TLS_VERSION)));
        } else if (!config.getString(MQTTSourceConnectorConfig.MQTT_CA_CERT).equals("")) {
            connOpts.setSocketFactory(
                SslKit.getSocketFactory(
                    new File(config.getString(MQTTSourceConnectorConfig.MQTT_CA_CERT)),
                    config.getString(MQTTSourceConnectorConfig.MQTT_TLS_VERSION)));
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

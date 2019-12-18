package nl.nedcar.kafka.connect;

import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDeque;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDequeBuilder;
import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MQTTSourceTask extends SourceTask implements IMqttMessageListener {

    private Logger log = LogManager.getLogger(MQTTSourceTask.class);
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
            mqttClient.connect();
            log.info("Connected");

            String[] topicSubscriptions = (String[])this.config.getList(MQTTSourceConnectorConfig.MQTT_TOPIC).toArray(new String[0]);
            int[] qosLevels = new int[topicSubscriptions.length];
            IMqttMessageListener[] listeners = new IMqttMessageListener[topicSubscriptions.length];
            // TODO Arrays.fill(qosLevels, this.config.mqttQos);
            Arrays.fill(listeners, this);

            log.info("Subscribing to " + topicSubscriptions);
            mqttClient.subscribe(topicSubscriptions, listeners);
        }
        catch (MqttException e) {
            throw new ConnectException(e);
        }
    }

    public List<SourceRecord> poll() throws InterruptedException {
        return sourceRecordDeque.getBatch();
    }

    public void stop() {
        if (mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException mqttException) {
                log.error("Exception thrown while disconnecting client.", mqttException);
            }
        }
    }

    public String version() {
        return Version.getVersion();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("Message arrived in connector from topic " + topic);
        sourceRecordDeque.add(mqttSourceConverter.convert(mqttMessage));
    }
}

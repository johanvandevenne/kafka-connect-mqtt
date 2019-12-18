package nl.nedcar.kafka.connect;

import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDeque;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordDequeBuilder;
import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MQTTSourceTask extends SourceTask implements MqttCallback {

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
            mqttClient.setCallback(this);
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
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        sourceRecordDeque.add(mqttSourceConverter.convert(mqttMessage));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}

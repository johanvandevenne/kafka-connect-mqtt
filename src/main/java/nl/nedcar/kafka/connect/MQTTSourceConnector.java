package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MQTTSourceConnector extends SourceConnector {

    private static final Logger log = LoggerFactory.getLogger(MQTTSourceConnector.class);

    private MQTTSourceConnectorConfig mqttSourceConnectorConfig;
    private Map<String, String> configProps;

    public void start(Map<String, String> map) {
        this.mqttSourceConnectorConfig = new MQTTSourceConnectorConfig(map);
        this.configProps = Collections.unmodifiableMap(map);
    }

    public Class<? extends Task> taskClass() {
        return MQTTSourceTask.class;
    }

    public static void main(String[] args) {
        new MQTTSourceConnector().taskConfigs(1);
    }

    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<String> mqTTtopics = mqttSourceConnectorConfig.getList(MQTTSourceConnectorConfig.MQTT_TOPIC);
        List<String> kafkaTopics = mqttSourceConnectorConfig.getList(MQTTSourceConnectorConfig.KAFKA_TOPIC);
        int numTasks = Math.min(mqTTtopics.size(), maxTasks);
        List<List<String>> groupedMQTTTopics = ConnectorUtils.groupPartitions(mqTTtopics, numTasks);
        List<List<String>> groupedKafkaTopics = ConnectorUtils.groupPartitions(kafkaTopics, numTasks);
        List<Map<String, String>> taskConfigs = new ArrayList<>(groupedMQTTTopics.size());

        for (int i=0; i<groupedMQTTTopics.size(); i++) {
            List<String> groupConfig = groupedMQTTTopics.get(i);
            Map<String, String> taskProps = new HashMap<>(configProps);
            taskProps.put(MQTTSourceConnectorConfig.MQTT_TOPIC, String.join(",", groupConfig));
            taskProps.put(MQTTSourceConnectorConfig.KAFKA_TOPIC, String.join(",", groupedKafkaTopics.get(i)));
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    public void stop() {

    }

    public ConfigDef config() {
        return MQTTSourceConnectorConfig.configDef();
    }

    public String version() {
        return Version.getVersion();
    }
}

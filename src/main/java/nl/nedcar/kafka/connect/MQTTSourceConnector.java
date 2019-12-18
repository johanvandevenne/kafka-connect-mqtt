package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSourceConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.*;

public class MQTTSourceConnector extends SourceConnector {

    private MQTTSourceConnectorConfig mqttSourceConnectorConfig;
    private Map<String, String> configProps;

    public void start(Map<String, String> map) {
        this.mqttSourceConnectorConfig = new MQTTSourceConnectorConfig(map);
        this.configProps = Collections.unmodifiableMap(map);
    }

    public Class<? extends Task> taskClass() {
        return MQTTSourceTask.class;
    }

    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>();
        taskProps.putAll(configProps);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    public void stop() {

    }

    public ConfigDef config() {
        return null;
    }

    public String version() {
        return null;
    }
}

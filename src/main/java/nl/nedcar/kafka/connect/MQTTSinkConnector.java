package nl.nedcar.kafka.connect;

import nl.nedcar.kafka.connect.config.MQTTSinkConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MQTTSinkConnector extends SinkConnector {

    private static final Logger log = LoggerFactory.getLogger(MQTTSinkConnector.class);
    private MQTTSinkConnectorConfig mqttSinkConnectorConfig;
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> map) {
        this.mqttSinkConnectorConfig = new MQTTSinkConnectorConfig(map);
        this.configProps = Collections.unmodifiableMap(map);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MQTTSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        log.debug("Enter taskconfigs");
        if (maxTasks > 1) {
            log.info("maxTasks is " + maxTasks + ". MaxTasks > 1 is not supported in this connector.");
        }
        List<Map<String, String>> taskConfigs = new ArrayList<>(1);
        taskConfigs.add(new HashMap<>(configProps));

        log.debug("Taskconfigs: " + taskConfigs);
        return taskConfigs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return MQTTSinkConnectorConfig.configDef();
    }

    @Override
    public String version() {
        return Version.getVersion();
    }
}

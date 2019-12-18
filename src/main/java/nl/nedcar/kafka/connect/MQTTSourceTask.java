package nl.nedcar.kafka.connect;

import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.util.List;
import java.util.Map;

public class MQTTSourceTask extends SourceTask {

    public void start(Map<String, String> map) {

    }

    public List<SourceRecord> poll() throws InterruptedException {
        return null;
    }

    public void stop() {

    }

    public String version() {
        return null;
    }
}

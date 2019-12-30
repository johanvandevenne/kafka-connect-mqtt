curl -X POST \
  http://<kafkaconnect>>:8083/connectors \
  -H 'Content-Type: application/json' \
  -d '{ "name": "mqtt-sink-connector",
    "config":
    {
      "connector.class":"nl.nedcar.kafka.connect.MQTTSinkConnector",
      "mqtt.topic":"my_mqtt_topic",
      "topics":"my_kafka_topic",
      "mqtt.clientID":"my_client_id",
      "mqtt.broker":"tcp://127.0.0.1:1883",
      "key.converter":"org.apache.kafka.connect.storage.StringConverter",
      "key.converter.schemas.enable":"false",
      "value.converter":"org.apache.kafka.connect.storage.StringConverter",
      "value.converter.schemas.enable":"false"
    }
}'
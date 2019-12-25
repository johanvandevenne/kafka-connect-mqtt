curl -X POST \
  http://192.168.1.10:8083/connectors \
  -H 'Content-Type: application/json' \
  -d '{ "name": "testmqtt",
    "config":
    {
      "connector.class":"nl.nedcar.kafka.connect.MQTTSourceConnector",
      "mqtt.topic":"tempTopic",
      "kafka.topic":"loxone",
      "qos":"1",
      "clientID":"testjohan",
      "broker":"tcp://192.168.1.10:1883",
      "key.converter":"org.apache.kafka.connect.storage.StringConverter",
      "key.converter.schemas.enable":false,
      "value.converter":"org.apache.kafka.connect.storage.StringConverter",
      "value.converter.schemas.enable":false
    }
}'
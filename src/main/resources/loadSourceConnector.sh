curl -X POST \
  http://localhost:8083/connectors \
  -H 'Content-Type: application/json' \
  -d '{ "name": "testmqtt",
    "config":
    {
      "connector.class":"nl.nedcar.kafka.connect.MQTTSourceConnector",
      "mqtt.topic":"tempTopic",
      "kafka.topic":"loxone",
      "mqtt.qos":"1",
      "mqtt.clientID":"testjohan",
      "mqtt.broker":"tcp://127.0.0.1:1883",
      "mqtt.userName":"johan",
      "mqtt.password":"johantest",
      "key.converter":"org.apache.kafka.connect.storage.StringConverter",
      "key.converter.schemas.enable":false,
      "value.converter":"org.apache.kafka.connect.storage.StringConverter",
      "value.converter.schemas.enable":false
    }
}'
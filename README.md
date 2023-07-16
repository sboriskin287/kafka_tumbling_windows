### Features

- Script payments_producer.ps1 generates records in json with schema {price: $price, messageCount: $count}, where $price - random generated number from 1 to 1000, $count - orederd number in the loop.
- Kafka producer cli sent records to one-partitioned topic "payments".
- @KafkaListener listen this topic for new records and split theirs by one-minute intervals.
- By every interval there is calculated sum of prices and list of instants of every record from payments. That's all together consist one instance of tumbling window, writing to "tumbling-window" topic.
- In general, application performs one minute realtime tumbling windows on incoming stream of prices and publich this to out topic.

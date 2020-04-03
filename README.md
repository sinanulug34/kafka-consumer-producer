1.Download the Kafka 2.4.1
cd "kafka path"

2.Run the zookeeper & kafka server
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

3.If you want to create the topics manually using this command :
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic "topic name"
Check if the topics are created
bin/kafka-topics.sh --list --zookeeper localhost:2181

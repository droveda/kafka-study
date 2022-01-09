# Kafka Course

## install kafka
* kafka.apache.org
  * bin/zookeeper-server-start.sh /config/zookeeper.properties (2181)
  * bin/kafka-server-start.sh /config/server.properties (9092)
* or use docker-compose

## Kafka commands
* kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic LOJA_NOVO_PEDIDO
* kafka-topics --list --bootstrap-server localhost:9092
* kafka-console-producer --broker-list localhost:9092 --topic LOJA_NOVO_PEDIDO
* kafka-console-consumer --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO --from-beginning
* kafka-console-consumer --bootstrap-server localhost:9092 --topic ECOMMERCE_NEW_ORDER --from-beginning
* kafka-topics --describe --bootstrap-server localhost:9092
* kafka-topics --bootstrap-server localhost:9092 --alter --topic ECOMMERCE_NEW_ORDER --partitions 3
* kafka-consumer-groups --bootstrap-server localhost:9092 --describe --all-groups



## Notas
* O número de partições deve ser maior ou igual ao numero de consumidores com o mesmo GroupID para um tópico.
* Qual a importância das chaves na paralelização de tarefas?
  * Ela é peça fundamental para paralelizar o processamento de mensagens em um tópico dentro do mesmo consumer group.
  * A chave é usada para distribuir a mensagem entre as partições existentes e consequentemente entre as instâncias de um serviço dentro de um consumer group.
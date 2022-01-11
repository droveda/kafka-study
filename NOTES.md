# Kafka Course

## install kafka
* kafka.apache.org
  * bin/zookeeper-server-start.sh /config/zookeeper.properties (2181)
  * bin/kafka-server-start.sh /config/server.properties (9092)
* or use docker-compose

## Kafka commands
* kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic ECOMMERCE_NEW_ORDER
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
* Fast Delegate
  * Responder ao usuario o mais rápido possível, usar mensagens assíncronas.
* replication-factor -> útil quando utilizado um clsuter de brokers e é necessário replicar os dados para disponibilidade em caso de falha de algum broker.
* properties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); (Nivel mais forte the ack, garante que a mensagem foi entrege para o leader e deplicada para todos as replicas)
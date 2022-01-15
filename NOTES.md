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
* properties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); (Nivel mais forte the ack, garante que a mensagem foi entrege para o leader e replicada para todas as replicas)
* ver sobre **max.in.flight.requests.per.connection** pode ajudar a manter a ordem na produção asíncrona de mensagem
* properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); Indica de qual offset o consumer deverá iniciar
  * "What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted): <ul><li>earliest: automatically reset the offset to the earliest offset<li>latest: automatically reset the offset to the latest offset</li><li>none: throw exception to the consumer if no previous offset is found for the consumer's group</li><li>anything else: throw exception to the consumer.</li></ul>";
* Kafka Transaction (medium.com)
  * https://itnext.io/kafka-transaction-56f022af1b0c
* Idempotence
  * Informações que identificam uma mensagem deixando-a única
    * Tópico
    * Consumer Group (group_id)
    * Partição
    * Offset
  * Outro identificador único que eu possa utilizar, exemplo
    * Id do usuário
    * email
    * CPF
    * etc...
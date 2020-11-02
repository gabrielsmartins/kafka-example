package br.com.ecommerce.consumer;

import br.com.ecommerce.common.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {

    void parse(ConsumerRecord<String, Message<T>> record) throws Exception;
}

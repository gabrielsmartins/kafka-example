package br.com.ecommerce.common;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {

    void parse(ConsumerRecord<String, T> record) throws Exception;
}

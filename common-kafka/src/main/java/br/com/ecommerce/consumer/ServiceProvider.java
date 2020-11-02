package br.com.ecommerce.consumer;

import java.util.Map;
import java.util.concurrent.Callable;

public class ServiceProvider<T> implements Callable<Void> {

    private final ServiceFactory<T> factory;

    public ServiceProvider(ServiceFactory<T> factory){
        this.factory = factory;
    }

    @Override
    public Void call() throws Exception {
        var consumerService = factory.create();
        try (var kafkaService = new KafkaService<>(consumerService.getConsumerGroup(),
                consumerService.getTopic(),
                consumerService::parse,
                Map.of())){
            kafkaService.run();
        }
        return null;
    }


}

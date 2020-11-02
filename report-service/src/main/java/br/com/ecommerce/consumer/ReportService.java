package br.com.ecommerce.consumer;

import br.com.ecommerce.common.Message;
import br.com.ecommerce.message.User;
import br.com.ecommerce.producer.KafkaDispatcher;
import br.com.ecommerce.utils.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class ReportService implements ConsumerService<User> {

    private final KafkaDispatcher<User> reportKafkaDispatcher = new KafkaDispatcher<>();
    private final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner<>(ReportService::new).start(1);
    }

    @Override
    public String getConsumerGroup() {
        return ReportService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_NEW_REPORT";
    }

    @Override
    public void parse(ConsumerRecord<String, Message<User>> record) throws Exception{
        System.out.println("---------------------------------------------");
        System.out.println("Processing report ...");
        System.out.println("Key : " + record.key());
        System.out.println("Value : " + record.value());
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset : " + record.offset());
        System.out.println("Timestamp : " + record.timestamp());

        var user = record.value().getPayload();
        File target = new File(user.getReportPath());

        IOUtils.copyTo(SOURCE, target);
        IOUtils.append(target, user.getUuid());

        System.out.println("File created : " + target.getAbsolutePath());
    }

}

package ecommerce.consumer;

import br.com.ecommerce.common.ConsumerFunction;
import br.com.ecommerce.common.KafkaDispatcher;
import br.com.ecommerce.common.KafkaService;
import br.com.ecommerce.common.Message;
import ecommerce.message.User;
import ecommerce.utils.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReportService implements ConsumerFunction<User> {

    private final KafkaDispatcher<User> reportKafkaDispatcher = new KafkaDispatcher<>();
    private final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var reportService = new ReportService();
        try(var service = new KafkaService(ReportService.class.getSimpleName(),
                "ECOMMERCE_USER_NEW_REPORT", reportService::parse,
                Map.of())) {
            service.run();
        }
    }

    @Override
    public void parse(ConsumerRecord<String, Message<User>> record) throws ExecutionException, InterruptedException {
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

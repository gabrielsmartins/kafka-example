package br.com.ecommerce.servlet;

import br.com.ecommerce.common.CorrelationId;
import br.com.ecommerce.common.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;

public class NewReportServlet extends HttpServlet {

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            String topic = "ECOMMERCE_USER_NOTIFY_ALL_USERS";
            String key = "ECOMMERCE_USER_NEW_REPORT";
            CorrelationId correlationId = new CorrelationId(NewReportServlet.class.getSimpleName());
            String payload = "ECOMMERCE_USER_NEW_REPORT";
            batchDispatcher.send(topic, key, correlationId, payload);

            System.out.println("Sent generated report to all users");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter writer = resp.getWriter();
            writer.print("Report requests generated");
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }
}

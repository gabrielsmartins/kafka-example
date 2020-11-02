package br.com.ecommerce.servlet;

import br.com.ecommerce.common.CorrelationId;
import br.com.ecommerce.producer.KafkaDispatcher;
import br.com.ecommerce.message.Order;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String email = req.getParameter("email");

            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(req.getParameter("amount"));

            var order = new Order(orderId, amount, email);
            CorrelationId correlationId = new CorrelationId(NewOrderServlet.class.getSimpleName());

            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, correlationId, order);

            System.out.println("Order was sent successfully");

            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter writer = resp.getWriter();
            writer.print("Order was sent successfully");
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }
}

package br.com.ecommerce.servlet;

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

            batchDispatcher.send("USER_NOTIFY_ALL_USERS", "USER_NEW_REPORT", "USER_NEW_REPORT");

            System.out.println("Sent generated report to all users");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter writer = resp.getWriter();
            writer.print("Report requests generated");
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}

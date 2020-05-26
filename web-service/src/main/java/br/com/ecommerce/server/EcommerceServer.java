package br.com.ecommerce.server;

import br.com.ecommerce.servlet.NewOrderServlet;
import br.com.ecommerce.servlet.NewReportServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class EcommerceServer {

    public static void main(String[] args) throws Exception {
        var server = new Server(8080);

        var context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new NewOrderServlet()), "/order");
        context.addServlet(new ServletHolder(new NewReportServlet()), "/report");

        server.setHandler(context);

        server.start();
        server.join();
    }
}

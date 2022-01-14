package com.droveda;

import com.droveda.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GenerateReportsServlet extends HttpServlet {

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<String>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            batchDispatcher.send("ECOMMERCE_SEND_MESSAGE_ALL_USERS", "ECOMMERCE_USER_GENERATE_READING_REPORT",
                    "ECOMMERCE_USER_GENERATE_READING_REPORT",
                    new CorrelationId(GenerateReportsServlet.class.getSimpleName()));

            System.out.println("Sent Generate reports to all users");
            resp.getWriter().println("Reports request generated!");
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }
}

package com.droveda;

import com.droveda.model.Email;
import com.droveda.model.Order;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<Email>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var email = req.getParameter("email");
        var amount = new BigDecimal(req.getParameter("amount"));

        var orderId = UUID.randomUUID().toString();
        var order = new Order(orderId, amount, email);

        try {
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order, new CorrelationId(NewOrderServlet.class.getSimpleName()));

            var body = "Thank you for your order! we are processing your order!";
            var emailCode = new Email("Assunto do email", body);
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode, new CorrelationId(NewOrderServlet.class.getSimpleName()));

            System.out.println("New order successfully created!");

            resp.getWriter().println("New order successfully created!");
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

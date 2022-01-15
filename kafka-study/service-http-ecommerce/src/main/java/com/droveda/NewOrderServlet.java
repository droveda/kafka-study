package com.droveda;

import com.droveda.dispatcher.KafkaDispatcher;
import com.droveda.model.Order;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            var email = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));
            var orderId = req.getParameter("uuid");

//        var orderId = UUID.randomUUID().toString();
            var order = new Order(orderId, amount, email);

            try (var ordersDatabase = new OrdersDatabase()) {

                if (ordersDatabase.saveNew(order)) {
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order, new CorrelationId(NewOrderServlet.class.getSimpleName()));
                    System.out.println("New order successfully created!");
                    resp.getWriter().println("New order successfully created!");
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    System.out.println("Old order received!");
                    resp.getWriter().println("Old order received!");
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

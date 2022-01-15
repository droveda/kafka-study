package com.droveda;

import com.droveda.model.Order;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrdersDatabase implements Closeable {

    private final LocalDatabase localDatabase;

    public OrdersDatabase() throws SQLException {
        this.localDatabase = new LocalDatabase("orders_database");
        this.localDatabase.createIfNotExists("create table Orders (uuid varchar(200) primary key)");
    }

    public boolean saveNew(Order order) throws SQLException {
        if (wasProcessed(order)) {
            return false;
        }
        localDatabase.update("insert into Orders (uuid) values (?)", order.getOrderId());
        return true;
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = localDatabase.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    @Override
    public void close() throws IOException {
        try {
            localDatabase.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}

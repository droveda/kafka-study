package com.droveda;

import java.util.UUID;

public class CorrelationId {

    private final String id;

    public CorrelationId(String title) {
        this.id = title + "(" + UUID.randomUUID().toString() + ")";
    }

    @Override
    public String toString() {
        return "CorrelationId{" +
                "id='" + id + '\'' +
                '}';
    }

    public CorrelationId continueWith(CorrelationId corr) {
        return new CorrelationId(id + "-" + corr.id);
    }

    public CorrelationId continueWith(String s) {
        return new CorrelationId(this.id + "_" + s);
    }
}

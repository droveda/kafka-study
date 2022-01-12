package com.droveda.model;

public class User {

    private final String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getReportPath() {

        return "reports/" + uuid + "-report.txt";

    }
}

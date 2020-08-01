package br.com.ecommerce.message;


public class User {

    private final String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    public String getReportPath() {
        return "target/" + uuid + "-report.txt";
    }

    public String getUuid() {
        return uuid;
    }
}

package com.cryptospace.datacore;

public class DatabaseConfig {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig() {
        this.url = envOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:3306/cryptowallet_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
        );
        this.user = envOrDefault("DB_USER", "root");
        this.password = envOrDefault("DB_PASSWORD", "");
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}

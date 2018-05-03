package dev.dc.james.config;

public class EmailConfiguration {
    
    String host;
    String sender;
    String password;
    String displayName;

    public EmailConfiguration() {
    }

    public EmailConfiguration(String host, String sender, String password, String displayName) {
        this.host = host;
        this.sender = sender;
        this.password = password;
        this.displayName = displayName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

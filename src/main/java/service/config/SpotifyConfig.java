package service.config;


import org.hibernate.validator.constraints.NotEmpty;

public class SpotifyConfig {
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String secret;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

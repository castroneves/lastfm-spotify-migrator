package service.config;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Adam on 21/07/2015.
 */
public class LastFmConfig {

    @NotEmpty
    private String apiKey;
    @NotEmpty
    private String secret;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

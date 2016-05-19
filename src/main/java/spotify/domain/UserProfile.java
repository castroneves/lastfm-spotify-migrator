package spotify.domain;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by adam.heinke on 07/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

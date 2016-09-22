package spotify;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by castroneves on 16/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyCreatePlaylistResponse {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package spotify.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by castroneves on 16/04/2016.
 */
public class SpotifyCreatePlaylistRequest {
    private String name;
    @JsonProperty("public")
    private boolean isPublic;

    public SpotifyCreatePlaylistRequest(String name, boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public boolean isPublic() {
        return isPublic;
    }
}

package lastfm.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by Adam on 27/04/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private TopTracks toptracks;
    private TopTracks lovedtracks;
    private TopArtists topartists;

    private String error;
    private String message;
    private String something;

    public TopArtists getTopartists() {
        return topartists;
    }

    public void setTopartists(TopArtists topartists) {
        this.topartists = topartists;
    }

    public String getSomething() {
        return something;
    }

    public void setSomething(String something) {
        this.something = something;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TopTracks getToptracks() {
        return toptracks;
    }

    public void setToptracks(TopTracks toptracks) {
        this.toptracks = toptracks;
    }

    public TopTracks getLovedtracks() {
        return lovedtracks;
    }

    public void setLovedtracks(TopTracks lovedtracks) {
        this.lovedtracks = lovedtracks;
    }


}

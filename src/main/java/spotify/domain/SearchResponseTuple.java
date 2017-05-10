package spotify.domain;

import lastfm.domain.Track;

import javax.ws.rs.core.Response;
import java.util.concurrent.Future;

/**
 * Created by castroneves on 28/11/2016.
 */
public class SearchResponseTuple {
    private Future<Response> future;
    private Track track;

    public SearchResponseTuple(Future<Response> future, Track track) {
        this.future = future;
        this.track = track;
    }

    public Future<Response> getFuture() {
        return future;
    }

    public Track getTrack() {
        return track;
    }
}

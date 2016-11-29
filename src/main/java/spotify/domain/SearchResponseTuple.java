package spotify.domain;

import lastfm.domain.Track;

import java.util.concurrent.Future;

/**
 * Created by castroneves on 28/11/2016.
 */
public class SearchResponseTuple {
    private Future<SpotifyTrackSearchResponse> future;
    private Track track;

    public SearchResponseTuple(Future<SpotifyTrackSearchResponse> future, Track track) {
        this.future = future;
        this.track = track;
    }

    public Future<SpotifyTrackSearchResponse> getFuture() {
        return future;
    }

    public Track getTrack() {
        return track;
    }
}

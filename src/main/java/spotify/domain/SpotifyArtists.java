package spotify.domain;

import lastfm.domain.Artist;

import java.util.List;

/**
 * Created by Adam on 16/02/2016.
 */
public class SpotifyArtists {
    private List<Artist> artists;

    public SpotifyArtists() {
    }

    public SpotifyArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
}

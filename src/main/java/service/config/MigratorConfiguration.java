package service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Adam on 21/07/2015.
 */
public class MigratorConfiguration extends Configuration {

    @Valid
    @NotNull
    private JedisConfig jedis = new JedisConfig();

    @Valid
    @NotNull
    private LastFmConfig lastFm = new LastFmConfig();

    @Valid
    @NotNull
    private SpotifyConfig spotify = new SpotifyConfig();


    @JsonProperty("jedis")
    public JedisConfig getJedis() {
        return jedis;
    }

    @JsonProperty("jedis")
    public void setJedis(JedisConfig jedis) {
        this.jedis = jedis;
    }

    @JsonProperty
    public LastFmConfig getLastFm() {
        return lastFm;
    }

    @JsonProperty
    public void setLastFm(LastFmConfig lastFm) {
        this.lastFm = lastFm;
    }

    @JsonProperty
    public SpotifyConfig getSpotify() {
        return spotify;
    }

    @JsonProperty
    public void setSpotify(SpotifyConfig spotify) {
        this.spotify = spotify;
    }
}

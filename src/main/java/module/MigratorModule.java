package module;

import com.google.inject.AbstractModule;
import service.config.JedisConfig;
import service.config.LastFmConfig;
import service.config.MigratorConfiguration;
import service.config.SpotifyConfig;

/**
 * Created by castroneves on 03/04/2016.
 */
public class MigratorModule extends AbstractModule {

    private MigratorConfiguration config;

    public MigratorModule(MigratorConfiguration config) {
        this.config = config;
    }

    protected void configure() {
        bind(LastFmConfig.class).toInstance(config.getLastFm());
        bind(SpotifyConfig.class).toInstance(config.getSpotify());
        bind(JedisConfig.class).toInstance(config.getJedis());
    }
}

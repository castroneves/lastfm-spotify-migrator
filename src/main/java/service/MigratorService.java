package service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lastfm.LastFmSender;
import module.MigratorModule;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.MigratorResource;
import service.config.MigratorConfiguration;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Created by castroneves on 03/04/2016.
 */
public class MigratorService extends Application<MigratorConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(MigratorService.class);

    public static void main(String[] args) throws Exception {
        String[] calArgs = new String[]{"server", "migrator.yml"};
        new MigratorService().run(calArgs);
    }

    public void initialize(Bootstrap<MigratorConfiguration> bootstrap) {
    }

    public void run(MigratorConfiguration configuration, Environment environment) throws Exception {
        Injector injector = Guice.createInjector(new MigratorModule(configuration));
        MigratorResource migratorResource = injector.getInstance(MigratorResource.class);

        logger.info("Redis Host : {}", configuration.getJedis().getHost());
        logger.info("Redis Port : {}", configuration.getJedis().getPort());

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        environment.jersey().register(migratorResource);
    }
}

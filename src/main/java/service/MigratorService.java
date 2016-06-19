package service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import module.MigratorModule;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import resource.MigratorResource;
import service.config.MigratorConfiguration;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Created by castroneves on 03/04/2016.
 */
public class MigratorService extends Application<MigratorConfiguration> {

    public static void main(String[] args) throws Exception {
        String[] calArgs = new String[]{"server", "migrator.yml"};
        new MigratorService().run(calArgs);
    }

    public void initialize(Bootstrap<MigratorConfiguration> bootstrap) {
    }

    public void run(MigratorConfiguration configuration, Environment environment) throws Exception {
        Injector injector = Guice.createInjector(new MigratorModule(configuration));
        MigratorResource migratorResource = injector.getInstance(MigratorResource.class);


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

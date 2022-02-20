package fish.focus.uvms.usm.authentication.rest;

import fish.focus.uvms.commons.date.JsonBConfigurator;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(JsonBConfigurator.class);
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(fish.focus.uvms.usm.authentication.rest.AdministrationResource.class);
        resources.add(fish.focus.uvms.usm.authentication.rest.service.AuthenticationResource.class);
        resources.add(fish.focus.uvms.usm.session.rest.service.SessionTrackerResource.class);
    }

}

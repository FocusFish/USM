package fish.focus.uvms.usm.administration.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import fish.focus.uvms.usm.administration.service.JsonBConfiguratorExtended;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(JsonBConfiguratorExtended.class);
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(fish.focus.uvms.usm.administration.rest.service.application.ApplicationResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.authentication.AuthenticationResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.organisation.ChannelResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.organisation.EndpointContactResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.organisation.EndpointResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.organisation.OrganisationResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.person.PersonResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.policy.PolicyResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.role.RoleResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.scope.ScopeResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.user.LdapUserResource.class);
        resources.add(fish.focus.uvms.usm.administration.rest.service.user.UserResource.class);
    }

}

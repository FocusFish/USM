package fish.focus.uvms.usm.administration.service;

import fish.focus.uvms.usm.administration.common.DateParser;
import fish.focus.uvms.usm.administration.common.jdbc.BaseJdbcDao;
import fish.focus.uvms.usm.administration.domain.FindUsersQuery;
import fish.focus.uvms.usm.administration.service.application.ApplicationService;
import fish.focus.uvms.usm.administration.service.application.impl.ApplicationServiceBean;
import fish.focus.uvms.usm.administration.service.ldap.LdapUserInfoService;
import fish.focus.uvms.usm.administration.service.ldap.impl.LDAP;
import fish.focus.uvms.usm.administration.service.ldap.impl.LdapUserInfoServiceBean;
import fish.focus.uvms.usm.administration.service.organisation.OrganisationService;
import fish.focus.uvms.usm.administration.service.organisation.impl.OrganisationServiceBean;
import fish.focus.uvms.usm.administration.service.person.PersonService;
import fish.focus.uvms.usm.administration.service.person.impl.PersonServiceBean;
import fish.focus.uvms.usm.administration.service.policy.DefinitionService;
import fish.focus.uvms.usm.administration.service.policy.impl.PolicyValidator;
import fish.focus.uvms.usm.administration.service.role.RoleService;
import fish.focus.uvms.usm.administration.service.role.impl.RoleServiceBean;
import fish.focus.uvms.usm.administration.service.scope.ScopeService;
import fish.focus.uvms.usm.administration.service.scope.impl.ScopeServiceBean;
import fish.focus.uvms.usm.administration.service.user.ViewUsersService;
import fish.focus.uvms.usm.administration.service.user.impl.UserJdbcDao;
import fish.focus.uvms.usm.administration.service.user.impl.UserJpaDao;
import fish.focus.uvms.usm.administration.service.userContext.UserContextService;
import fish.focus.uvms.usm.administration.service.userContext.impl.UserContextServiceBean;
import fish.focus.uvms.usm.administration.service.userPreference.UserPreferenceService;
import fish.focus.uvms.usm.administration.service.userPreference.impl.UserPreferenceServiceBean;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

@ArquillianSuiteDeployment
public class DeploymentFactory {
    private static final String AUDIT_GROUP_ID = "eu.europa.ec.mare.auditing";
    private static final String USM_GROUP_ID = "eu.europa.ec.mare.usm";
    private static final String UVMS_AUDIT_GROUP_ID = "eu.europa.ec.fisheries.uvms.audit";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "ArquillianTest.war")
                .addPackage(OrganisationService.class.getPackage())
                .addPackage(OrganisationServiceBean.class.getPackage())
                .addPackage(RoleService.class.getPackage())
                .addPackage(RoleServiceBean.class.getPackage())
                .addPackage(UserContextService.class.getPackage())
                .addPackage(UserContextServiceBean.class.getPackage())
                .addPackage(ScopeService.class.getPackage())
                .addPackage(ScopeServiceBean.class.getPackage())
                .addPackage(ApplicationService.class.getPackage())
                .addPackage(ApplicationServiceBean.class.getPackage())
                .addPackage(ViewUsersService.class.getPackage())
                .addPackage(UserJdbcDao.class.getPackage())
                .addPackage(FindUsersQuery.class.getPackage())
                .addPackage(LDAP.class.getPackage())
                .addClass(PolicyValidator.class)
                .addPackage(UserJpaDao.class.getPackage())
                .addPackage(BaseJdbcDao.class.getPackage())
                .addPackage(DateParser.class.getPackage())
                .addPackages(true, DefinitionService.class.getPackage())
                .addPackage(LdapUserInfoService.class.getPackage())
                .addPackage(LdapUserInfoServiceBean.class.getPackage())
                .addPackage(PersonService.class.getPackage())
                .addPackage(PersonServiceBean.class.getPackage())
                .addPackage(UserPreferenceService.class.getPackage())
                .addPackage(UserPreferenceServiceBean.class.getPackage())
                .addPackage(DeploymentFactory.class.getPackage())

                .addAsResource("logback-test.xml").addAsResource("ApacheDS.properties")
                .addAsResource("password.properties")
                .addAsResource("notification.properties")
                .addAsWebInfResource("META-INF/beans.xml", "beans.xml");

        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve(
                        "fish.focus.uvms.usm:Information-Service",
                        "fish.focus.uvms.usm:Authentication-Service",
                        "fish.focus.uvms.usm:Information-Model",
                        "fish.focus.uvms.usm:Authentication-Model",
                        "eu.europa.ec.fisheries.uvms.audit:audit-model")
                .withoutTransitivity().asFile();
        war.addAsLibraries(files);
        return war;
    }

    public DeploymentFactory() {
    }

}

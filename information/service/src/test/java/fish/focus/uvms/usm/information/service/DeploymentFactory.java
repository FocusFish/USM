package fish.focus.uvms.usm.information.service;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import fish.focus.uvms.usm.information.domain.UserContext;
import fish.focus.uvms.usm.information.domain.deployment.Application;
import fish.focus.uvms.usm.authentication.domain.AuthenticationRequest;
import fish.focus.uvms.usm.information.entity.ApplicationEntity;
import fish.focus.uvms.usm.information.service.impl.InformationDao;
import fish.focus.uvms.usm.policy.service.impl.PolicyProvider;
import fish.focus.uvms.usm.session.domain.SessionInfo;

/**
 *
 */
@ArquillianSuiteDeployment
public class DeploymentFactory {

  @Deployment
  public static JavaArchive createDeployment() 
  {
    JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "ArquillianTest.jar")
    		.addPackage("fish.focus.uvms.usm.policy.service.impl")
    		.addPackage("fish.focus.uvms.usm.service.impl")
    		.addPackage(AuthenticationRequest.class.getPackage())
    		.addPackage(SessionInfo.class.getPackage())
            .addPackage(InformationService.class.getPackage())
            .addPackage(InformationDao.class.getPackage())
            .addPackage(UserContext.class.getPackage())
            .addPackage(ApplicationEntity.class.getPackage())
            .addPackage(DeploymentFactory.class.getPackage())
            .addPackage("fish.focus.uvms.usm.information.domain.deployment")
            .addAsResource("META-INF/persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    return jar;
  }

  public DeploymentFactory() {
  }
  
}

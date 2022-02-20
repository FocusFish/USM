package fish.focus.uvms.usm.administration.service.application;

import fish.focus.uvms.usm.administration.service.DeploymentFactory;
import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.service.application.ApplicationService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ApplicationServiceTest extends DeploymentFactory {

    @EJB
    private ApplicationService testSubject;

    @Test
    public void testGetApplicationNames() {
        // Setup
        ServiceRequest<ApplicationQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(new ApplicationQuery());
        String expected = "USM";

        // Execute
        List<String> response = testSubject.getApplicationNames(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertNotNull("Expected Application " + expected + " not found", getApplicationName(response, expected));
    }

    @Test
    public void testGetAllApplications() {
        // Setup
        ServiceRequest<FindApplicationQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        FindApplicationQuery appQuery = new FindApplicationQuery();
        appQuery.setPaginator(getDefaultPaginator());
        request.setBody(appQuery);
        String expected = "USM";

        // Execute
        PaginationResponse<Application> response = testSubject.findApplications(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertNotNull("Expected Application " + expected + " not found",
                getAppName(response.getResults(), expected));
    }

    @Test
    public void testGetApplicationProvidingName() {
        // Setup
        ServiceRequest<FindApplicationQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        FindApplicationQuery appQuery = new FindApplicationQuery();
        appQuery.setName("USM");
        appQuery.setPaginator(getDefaultPaginator());
        request.setBody(appQuery);
        String expected = "USM";

        // Execute
        PaginationResponse<Application> response = testSubject.findApplications(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertNotNull("Expected Application " + expected + " not found",
                getAppName(response.getResults(), expected));
    }

    @Test
    public void testGetParentApplicationNames() {
        // Setup
        ServiceRequest<FindApplicationQuery> req = new ServiceRequest<>();
        req.setRequester("vms_admin_com");
        FindApplicationQuery appQuery = new FindApplicationQuery();
        appQuery.setPaginator(getDefaultPaginator());
        req.setBody(appQuery);

        // Execute
        PaginationResponse<Application> res = testSubject.findApplications(req);

        // Setup
        ServiceRequest<GetParentApplicationQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        request.setBody(new GetParentApplicationQuery());
        String expected = null;
        // get the first application which having parent
        Application appWithParent = getAppWithParent(res.getResults());
        if (appWithParent != null) {
            expected = appWithParent.getParent();
        }

        // Execute
        List<String> response = testSubject.getParentApplicationNames(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertEquals("Expected Application " + expected + " not found",
                getApplicationName(response, expected), expected);
    }

    @Test
    public void testGetApplicationProvidingParentName() {
        // Setup
        ServiceRequest<FindApplicationQuery> req = new ServiceRequest<>();
        req.setRequester("vms_admin_com");
        FindApplicationQuery query = new FindApplicationQuery();
        query.setPaginator(getDefaultPaginator());
        req.setBody(query);

        // Execute
        PaginationResponse<Application> res = testSubject.findApplications(req);
        // get the first application which having parent
        Application app = getAppWithParent(res.getResults());

        // Setup
        ServiceRequest<FindApplicationQuery> request = new ServiceRequest<>();
        request.setRequester("vms_admin_com");
        FindApplicationQuery appQuery = new FindApplicationQuery();
        appQuery.setPaginator(getDefaultPaginator());
        String expected = null;
        // set query and expected values only if an application with parent has been found
        if (app != null) {
            appQuery.setParentName(app.getParent());
            expected = app.getName();
        }
        request.setBody(appQuery);

        // Execute
        PaginationResponse<Application> response = testSubject.findApplications(request);

        // Verify
        assertNotNull("Unexpected null response", response);
        assertEquals("Expected Application " + expected + " not found",
                getAppName(response.getResults(), expected), expected);
    }

    private String getApplicationName(List<String> names, String expected) {
        for (String applicationName : names) {
            if (applicationName.equals(expected)) {
                return applicationName;
            }
        }
        return null;
    }

    private String getAppName(List<Application> apps, String expected) {
        for (Application app : apps) {
            if (app.getName().equals(expected)) {
                return expected;
            }
        }
        return null;
    }

    private Paginator getDefaultPaginator() {
        Paginator paginator = new Paginator();
        paginator.setLimit(30);
        paginator.setOffset(0);
        paginator.setSortDirection("DESC");
        return paginator;
    }

    private Application getAppWithParent(List<Application> apps) {
        for (Application app : apps) {
            if (app.getParent() != null) {
                return app;
            }
        }
        return null;
    }
}

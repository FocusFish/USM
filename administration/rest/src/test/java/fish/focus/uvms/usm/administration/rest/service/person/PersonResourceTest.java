package fish.focus.uvms.usm.administration.rest.service.person;

import fish.focus.uvms.usm.administration.domain.AuthenticationJwtResponse;
import fish.focus.uvms.usm.administration.domain.ContactDetails;
import fish.focus.uvms.usm.administration.domain.PendingContactDetails;
import fish.focus.uvms.usm.administration.domain.Person;
import fish.focus.uvms.usm.administration.rest.ResponseWrapper;
import fish.focus.uvms.usm.administration.rest.ServiceArrayResponse;
import fish.focus.uvms.usm.administration.rest.service.AdministrationRestClient;
import fish.focus.uvms.usm.administration.rest.service.BuildAdministrationDeployment;
import fish.focus.uvms.usm.administration.rest.service.person.ContactDetailsRequest;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PersonResourceTest extends BuildAdministrationDeployment {

    private static final String QUOTA_USR_GRC = "quota_usr_grc";
    private static final String QUOTA_USR_FRA = "quota_usr_fra";
    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testGetPersons() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.getPersons(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());
        ServiceArrayResponse<Person> sar = response.readEntity(new GenericType<>() {});
        List<Person> people = sar.getResults();

        assertFalse("Unexpected empty response", people.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPerson() {
        Long personId = 4L;
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.getPerson(auth.getJwtoken(), String.valueOf(personId));
        assertEquals(OK.getStatusCode(), response.getStatus());

        Person person = response.readEntity(Person.class);
        assertEquals("Unexpected firstName value", "guest", person.getFirstName());
        assertEquals("Unexpected personId value", personId, person.getPersonId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testIsUpdateContactDetailsEnabled() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        Response response = restClient.isUpdateContactDetailsEnabled(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdateContactDetails() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);

        ContactDetailsRequest request = createRequest("vms_admin_fra@gouv.fre");
        Response response = restClient.updateContactDetails(auth.getJwtoken(), request);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ContactDetails contactDetails = response.readEntity(ContactDetails.class);
        assertNotNull("Unexpected null response", contactDetails);
        assertEquals("Unexpected email value", request.getEmail(), contactDetails.getEmail());

        ContactDetailsRequest revert = createRequest("vms_admin_fra@gouv.fr");
        response = restClient.updateContactDetails(auth.getJwtoken(), revert);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ContactDetails reverted = response.readEntity(ContactDetails.class);
        assertNotNull("Unexpected null response", reverted);
        assertEquals("Unexpected email value", revert.getEmail(), reverted.getEmail());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testIsReviewContactDetailsEnabled() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        setUp(auth);

        Response response = restClient.isReviewContactDetailsEnabled(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testFindPendingContactDetails() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        setUp(auth);

        Response response = restClient.findPendingContactDetails(auth.getJwtoken());
        assertEquals(OK.getStatusCode(), response.getStatus());

        ServiceArrayResponse<PendingContactDetails> sar = response.readEntity(new GenericType<>(){});
        List<PendingContactDetails> list = sar.getResults();
        assertFalse(list.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPendingContactDetails() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        setUp(auth);

        final String username = "quota_usr_com";
        Response response = restClient.getPendingContactDetails(auth.getJwtoken(), username);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PendingContactDetails result = response.readEntity(PendingContactDetails.class);

        assertEquals(username, result.getUserName());
        assertEquals("+32287654321", result.getPhoneNumber());
        assertEquals("quota_usr_com@mail.europa.ec", result.getEmail());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testRejectPendingContactDetails() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        setUp(auth);

        Response response = restClient.getPendingContactDetails(auth.getJwtoken(), QUOTA_USR_FRA);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PendingContactDetails expected = response.readEntity(PendingContactDetails.class);

        if (expected != null) {
            response = restClient.rejectPendingContactDetails(auth.getJwtoken(), QUOTA_USR_FRA);
            assertEquals(OK.getStatusCode(), response.getStatus());

            ContactDetails result = response.readEntity(ContactDetails.class);

            assertNull(result.getPhoneNumber());
            assertEquals("quota_usr_fra@gouv.fr", result.getEmail());

            response = restClient.getContactDetails(auth.getJwtoken(), QUOTA_USR_FRA);
            assertEquals(OK.getStatusCode(), response.getStatus());

            ContactDetails verify = response.readEntity(ContactDetails.class);
            assertNotNull(verify);
            assertNull(verify.getPhoneNumber());
            assertEquals("quota_usr_fra@gouv.fr", verify.getEmail());
        } else {
            System.out.println("WARNING: No pending update found to test rejectPendingContactDetails");
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAcceptPendingContactDetails() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        setUp(auth);

        Response response = restClient.getPendingContactDetails(auth.getJwtoken(), QUOTA_USR_GRC);
        assertEquals(OK.getStatusCode(), response.getStatus());

        PendingContactDetails expected = response.readEntity(PendingContactDetails.class);

        if (expected != null) {
            response = restClient.acceptPendingContactDetails(auth.getJwtoken(), QUOTA_USR_GRC);
            assertEquals(OK.getStatusCode(), response.getStatus());

            ContactDetails result = response.readEntity(ContactDetails.class);

            assertNotNull(result);
            assertEquals(expected.getEmail(), result.getEmail());
            assertEquals(expected.getPhoneNumber(), result.getPhoneNumber());

            response = restClient.getContactDetails(auth.getJwtoken(), QUOTA_USR_GRC);
            assertEquals(OK.getStatusCode(), response.getStatus());

            ContactDetails verify = response.readEntity(ContactDetails.class);

            assertNotNull(verify);
            assertEquals(expected.getEmail(), verify.getEmail());
            assertEquals(expected.getPhoneNumber(), verify.getPhoneNumber());
        } else {
            System.out.println("WARNING: No pending update to test acceptPendingContactDetails");
        }
    }

    private ContactDetailsRequest createRequest(String email) {
        ContactDetailsRequest requestBody = new ContactDetailsRequest();
        requestBody.setEmail(email);
        requestBody.setUserName("vms_user_fra");
        requestBody.setPassword("password");
        return requestBody;
    }

    private void setUp(AuthenticationJwtResponse auth) {
        Response response = restClient.isReviewContactDetailsEnabled(auth.getJwtoken());
        ResponseWrapper<Boolean> responseWrapper = response.readEntity(new GenericType<>(){});
        Boolean enabled = responseWrapper.getResult();

        if (enabled) {
            createPendingChange(QUOTA_USR_GRC);
            createPendingChange(QUOTA_USR_FRA);
        }
    }


    private void createPendingChange(String userName) {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Response response = restClient.findPendingContactDetails(auth.getJwtoken());
        ServiceArrayResponse<PendingContactDetails> result = response.readEntity(new GenericType<>() {
        });

        if (result == null) {
            ContactDetailsRequest request = new ContactDetailsRequest();
            request.setUserName(userName);
            request.setPassword("password");
            request.setFaxNumber("Fax" + System.currentTimeMillis());
            request.setMobileNumber("Mob" + System.currentTimeMillis());
            request.setPhoneNumber("Tel" + System.currentTimeMillis());
            request.setEmail("Mailto:" + userName + "@mail.world");
            restClient.updateContactDetails(auth.getJwtoken(), request);
        }
    }
}

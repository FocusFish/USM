package fish.focus.uvms.usm.administration.rest.security;

import fish.focus.uvms.usm.authentication.domain.AuthenticationRequest;
import fish.focus.uvms.usm.authentication.domain.AuthenticationResponse;
import fish.focus.uvms.usm.authentication.service.AuthenticationService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.DatatypeConverter;

@Stateless
public class BasicAuthenticationHandler {
    private static final String PREFIX = "Basic ";

    @EJB
    private AuthenticationService service;

    public String handleAuthorizationHeader(String header) {
        String ret = null;

        if (header != null && header.startsWith(PREFIX)) {
            String encoded = header.substring(PREFIX.length());
            byte[] bytes = DatatypeConverter.parseBase64Binary(encoded);
            String decoded = new String(bytes);
            String[] parts = decoded.split(":");

            if (parts.length == 2) {
                AuthenticationRequest request = new AuthenticationRequest();
                request.setUserName(parts[0]);
                request.setPassword(parts[1]);

                AuthenticationResponse r = service.authenticateUser(request);
                if (r != null && r.isAuthenticated()) {
                    ret = request.getUserName();
                }
            }
        }
        return ret;
    }
}

package fish.focus.uvms.usm.information.rest.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filters incoming REST requests, setting Access-Control-Allow headers
 * for allowing Cross-site HTTP requests.
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = {"/rest/*" })
public class CorsFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);

    /**
     * Creates a new instance
     */
    public CorsFilter() {
    }

    /**
     * Filters an incoming request, adding Access-Control-Allow headers
     *
     * @param request  The request we are processing
     * @param response The response we are creating
     * @param chain    The filter chain we are processing
     * @throws IOException if an input/output error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        LOGGER.debug("doFilter(" + httpRequest.getMethod() + ", " + httpRequest.getPathInfo() + ") - (ENTER)");

        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
        chain.doFilter(httpRequest, httpResponse);

        LOGGER.debug("doFilter() - (LEAVE)");
    }

    @Override
    public void init(FilterConfig fc) {
        // NOP
    }

    @Override
    public void destroy() {
        // NOP
    }

}

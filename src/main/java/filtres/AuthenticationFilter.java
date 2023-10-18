package filtres;

import io.jsonwebtoken.Jwts;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.Key;

public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "wass";

    ContainerRequestContext requestContext;
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("request filter invoked...");
        // Get the Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }
        // Extract the token from the Authorization header
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        try {
            // Validate the token
            validateToken(token);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(String token) {
        // Check if it was issued by the server and if it's not expired
// Throw an Exception if the token is invalid
        try {
// Validate the token
            String keyString = "simplekey";
            Key key = new SecretKeySpec(keyString.getBytes(), 0,
                    keyString.getBytes().length, "DES");
            System.out.println("the key is : " + key);
            System.out.println("test:" +
                    Jwts.parser().setSigningKey(key).parseClaimsJws(token));
            System.out.println("#### valid token : " + token);
        } catch (Exception e) {
            System.out.println("#### invalid token : " + token);
            (this.requestContext).abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }



    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME).build());
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null
                && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }
}


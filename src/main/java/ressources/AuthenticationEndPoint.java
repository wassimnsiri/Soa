package ressources;

import entities.Credentials;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jdk.nashorn.internal.parser.Token;
import metiers.AuthBusiness;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Path("authentification")
public class AuthenticationEndPoint {
    // ======================================
    // = Injection Points =
    // ======================================
    @Context
    private UriInfo uriInfo;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    // Cette annotation @POST spécifie que cette méthode répond aux requêtes HTTP POST.
    // @Produces indique que la méthode renvoie du JSON en réponse, et @Consumes indique qu'elle accepte le JSON en entrée.

    public Response authentificateUser(Credentials cred){
        try{
//// Cette méthode est appelée lorsqu'un utilisateur tente de s'authentifier
            authenficate(cred.getUsername(),cred.getPassword());
            // Appel à la méthode `authenticate` pour vérifier les informations d'identification de l'utilisateur.

            String token = issueToken(cred.getUsername());
            // Appel à la méthode `issueToken` pour générer un jeton d'authentification.

            return Response.ok(token).build();
//Si l'authentification réussit, renvoie le jeton d'authentification dans la réponse avec un code HTTP 200 (OK).
        }catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
            // En cas d'échec de l'authentification, renvoie une réponse HTTP 403 (FORBIDDEN).
        }
    }

    private String issueToken(String username) {
        // Cette méthode génère un jeton d'authentification JWT.
        String KeyString = "simplekey";
        Key key = new SecretKeySpec(KeyString.getBytes(),0,KeyString.getBytes().length,"DES");
        // Création d'une clé secrète pour signer le jeton.

        System.out.println("the key is : "+key.hashCode());
        System.out.println("uriInfo.getAbsolutePath().toString() :" +uriInfo.getAbsolutePath().toString());
        // Impression de la clé et de l'URI de la requête (peut être utile pour le débogage).


    System.out.println("Expirated Date: "+toDate(LocalDateTime.now().plusMinutes(15L)));
        // Calcul de la date d'expiration du jeton, 15 minutes à partir de maintenant.


    String jwtToken= Jwts.builder().setSubject(username)
            .setIssuer(uriInfo.getAbsolutePath().toString())
            .setIssuedAt(new Date())
            .setExpiration(toDate(LocalDateTime.now()
                    .plusMinutes(15L))).signWith(SignatureAlgorithm.HS512,key)
            .compact();
        // Création du jeton JWT avec des revendications (claims) comme le nom d'utilisateur, l'émetteur, la date d'émission et la date d'expiration.


        System.out.println("the returned tokin is :"+jwtToken);
        // Impression du jeton généré (peut être utile pour le débogage).

    return jwtToken;
    }

    private void authenficate(String username, String password) {
        // Cette méthode serait utilisée pour authentifier l'utilisateur.
        System.out.println("Authenticating user...");
        // Pour l'instant, cela ne fait que l'affichage d'un message de débogage.

    }
    private Date toDate(LocalDateTime localDateTime) {
        // Cette méthode convertit un objet LocalDateTime en un objet Date.
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

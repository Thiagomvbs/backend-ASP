package backend.ASP.config.security;

import backend.ASP.entity.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {
    private static final Duration VALIDADE = Duration.ofHours(2);

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("ASP")
                    .withSubject(user.getLogin())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException ex){
            throw new RuntimeException("Erro ao gerar token JWT", ex);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer("ASP")
                    .build()
                    .verify(token)
                    .getSubject();
            return subject == null ? "" : subject;
        }catch (JWTVerificationException ex){
            return "";
        }
    }

    private Instant generateExpirationDate(){
        return Instant.now().plus(VALIDADE);
    }
}
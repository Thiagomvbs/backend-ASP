package backend.ASP.controller;

import backend.ASP.config.ratelimit.RateLimiter;
import backend.ASP.config.security.TokenService;
import backend.ASP.dto.AuthenticationDTO;
import backend.ASP.dto.LoginResponseDTO;
import backend.ASP.dto.RegisterDTO;
import backend.ASP.entity.user.User;
import backend.ASP.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Limite de tentativas de login que FALHAM, por nome de usuário (veja RateLimiter).
    @Autowired
    private RateLimiter rateLimiter;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        String chave = chaveDeLogin(data.login());
        if (rateLimiter.excedeu(chave)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("mensagem", "Muitas tentativas de login. Aguarde alguns minutos e tente de novo."));
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        Authentication auth;
        try {
            auth = this.authenticationManager.authenticate(usernamePassword);
        } catch (AuthenticationException e) {
            rateLimiter.registrar(chave);
            throw e;
        }

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // O limite é por usuário (não por IP): atrás da Vercel todos os pedidos chegam do mesmo IP.
    private static String chaveDeLogin(String login) {
        String normalizado = login.trim().toLowerCase(Locale.ROOT);
        if (normalizado.length() > 100) {
            normalizado = normalizado.substring(0, 100);
        }
        return "login:" + normalizado;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        //verifica se ja existe no banco de dados
        if(this.repository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().body("Login já existe!");
        }
        //encriptografando senha do usuario
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}

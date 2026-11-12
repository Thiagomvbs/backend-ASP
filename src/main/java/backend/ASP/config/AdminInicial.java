package backend.ASP.config;

import backend.ASP.entity.user.User;
import backend.ASP.entity.user.UserRole;
import backend.ASP.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o primeiro administrador quando ADMIN_LOGIN e ADMIN_PASSWORD estão definidos.
 * Não altera um usuário que já existe e nunca escreve a senha no log.
 */
@Component
public class AdminInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);
    private static final int TAMANHO_MINIMO_SENHA = 8;

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String login;
    private final String senha;

    public AdminInicial(UserRepository repository,
                        PasswordEncoder passwordEncoder,
                        @Value("${ADMIN_LOGIN:}") String login,
                        @Value("${ADMIN_PASSWORD:}") String senha) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.login = login;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (login.isBlank() || senha.isBlank()) {
            log.info("ADMIN_LOGIN/ADMIN_PASSWORD não definidos: nenhum administrador será criado.");
            return;
        }
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            log.warn("ADMIN_PASSWORD tem menos de {} caracteres: administrador não criado.", TAMANHO_MINIMO_SENHA);
            return;
        }
        if (repository.findByLogin(login) != null) {
            log.info("Administrador inicial já existe: nada a fazer.");
            return;
        }
        repository.save(new User(login, passwordEncoder.encode(senha), UserRole.ADMIN));
        log.info("Administrador inicial criado.");
    }
}
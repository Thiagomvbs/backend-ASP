package backend.ASP.config;

import backend.ASP.entity.user.User;
import backend.ASP.entity.user.UserRole;
import backend.ASP.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdminInicialTest {

    private UserRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
    }

    private AdminInicial criar(String login, String senha) {
        return new AdminInicial(repository, encoder, login, senha);
    }

    @Test
    void criaOAdministradorComSenhaCriptografada() {
        when(repository.findByLogin("admin")).thenReturn(null);

        criar("admin", "senha-forte-123").run(null);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        User salvo = captor.getValue();
        assertEquals("admin", salvo.getLogin());
        assertEquals(UserRole.ADMIN, salvo.getRole());
        assertNotEquals("senha-forte-123", salvo.getPassword());
        assertTrue(encoder.matches("senha-forte-123", salvo.getPassword()));
    }

    @Test
    void naoFazNadaSeOAdminJaExiste() {
        when(repository.findByLogin("admin")).thenReturn(new User("admin", "hash", UserRole.ADMIN));

        criar("admin", "senha-forte-123").run(null);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void naoFazNadaSemAsVariaveis() {
        criar("", "").run(null);

        verifyNoInteractions(repository);
    }

    @Test
    void naoCriaComSenhaCurta() {
        criar("admin", "curta").run(null);

        verify(repository, never()).save(any(User.class));
    }
}
package backend.ASP.config.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    static class RelogioFalso extends Clock {
        private Instant agora = Instant.parse("2026-01-01T12:00:00Z");

        void avancar(Duration duracao) {
            agora = agora.plus(duracao);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return agora;
        }
    }

    private RelogioFalso relogio;
    private RateLimiter limiter;

    @BeforeEach
    void setUp() {
        relogio = new RelogioFalso();
        limiter = new RateLimiter(3, Duration.ofMinutes(10), relogio);
    }

    @Test
    void bloqueiaDepoisDoMaximoNaJanela() {
        assertTrue(limiter.permitir("1.1.1.1"));
        assertTrue(limiter.permitir("1.1.1.1"));
        assertTrue(limiter.permitir("1.1.1.1"));
        assertFalse(limiter.permitir("1.1.1.1"));
    }

    @Test
    void liberaDeNovoDepoisQueAJanelaPassa() {
        for (int i = 0; i < 3; i++) {
            limiter.permitir("1.1.1.1");
        }
        assertFalse(limiter.permitir("1.1.1.1"));

        relogio.avancar(Duration.ofMinutes(11));

        assertTrue(limiter.permitir("1.1.1.1"));
    }

    @Test
    void cadaVisitanteTemOSeuProprioLimite() {
        for (int i = 0; i < 3; i++) {
            limiter.permitir("1.1.1.1");
        }
        assertFalse(limiter.permitir("1.1.1.1"));
        assertTrue(limiter.permitir("2.2.2.2"));
    }
}
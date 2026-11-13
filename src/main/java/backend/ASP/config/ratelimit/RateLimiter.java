package backend.ASP.config.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private static final int LIMITE_DE_CHAVES_PARA_LIMPEZA = 5000;

    private final int maximo;
    private final Duration janela;
    private final Clock relogio;
    private final Map<String, Deque<Instant>> registros = new ConcurrentHashMap<>();

    
    public RateLimiter() {
        this(5, Duration.ofMinutes(10), Clock.systemUTC());
    }

    RateLimiter(int maximo, Duration janela, Clock relogio) {
        this.maximo = maximo;
        this.janela = janela;
        this.relogio = relogio;
    }

    public boolean permitir(String chave) {
        Instant agora = relogio.instant();
        Instant inicioDaJanela = agora.minus(janela);
        boolean[] permitido = new boolean[1];

        registros.compute(chave, (k, fila) -> {
            Deque<Instant> pedidos = fila == null ? new ArrayDeque<>() : fila;
            while (!pedidos.isEmpty() && pedidos.peekFirst().isBefore(inicioDaJanela)) {
                pedidos.pollFirst();
            }
            if (pedidos.size() < maximo) {
                pedidos.addLast(agora);
                permitido[0] = true;
            }
            return pedidos.isEmpty() ? null : pedidos;
        });

        if (registros.size() > LIMITE_DE_CHAVES_PARA_LIMPEZA) {
            limpar(inicioDaJanela);
        }
        return permitido[0];
    }

    private void limpar(Instant inicioDaJanela) {
        for (String chave : registros.keySet()) {
            registros.computeIfPresent(chave, (k, fila) ->
                    fila.isEmpty() || fila.peekLast().isBefore(inicioDaJanela) ? null : fila);
        }
    }
}
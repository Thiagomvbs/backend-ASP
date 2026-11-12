package backend.ASP.entity.products;

import backend.ASP.exception.RequisicaoInvalidaException;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.Locale;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Categoria {
        PRATOS, COPOS, RECHAUD,
        GARRAFAS, TALHERES,
        VIDROS, TRAVESSAS,
        INOX, LOUCAS,
        XICARAS, PRATARIA,
        MADEIRA, PANELAS,
        TOALHAS, EQUIPAMENTOS;

        /** Converte texto em categoria; valor inválido vira HTTP 400 (antes valueOf gerava HTTP 500). */
        public static Categoria fromString(String valor) {
                if (valor == null || valor.isBlank()) {
                        throw new RequisicaoInvalidaException("A categoria é obrigatória");
                }
                try {
                        return valueOf(valor.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                        throw new RequisicaoInvalidaException(
                                "Categoria inválida: " + valor + ". Valores aceitos: " + Arrays.toString(values()));
                }
        }
}
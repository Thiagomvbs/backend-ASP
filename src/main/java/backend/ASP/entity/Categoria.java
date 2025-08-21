package backend.ASP.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Categoria {
        PRATOS, COPOS, RECHAUD,
        GARRAFAS, TALHERES,
        VIDROS, TRAVESSAS,
        INOX, LOUCAS,
        XICARAS, PRATARIA,
        MADEIRA, PANELAS,
        TOALHAS, EQUIPAMENTOS

}

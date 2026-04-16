package BatiBloc.Domaine.Dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nicky
 */
public record VueProjetDTO(
        byte[] imagePng,
        double echelle,
        double zoom,
        List<ZoneProjetDTO> zones
) implements Serializable {}


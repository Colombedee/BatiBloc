package BatiBloc.Domaine.Dto;

import java.io.Serializable;
import java.util.List;

public record VueProjetDTO(byte[] imagePng, double echelle, double zoom,
                List<ZoneProjetDTO> zones) implements Serializable {}
package BatiBloc.Domaine.Dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nicky
 */
public record ZoneProjetDTO(
        TypeZoneDTO type,
        TypeFormeDTO forme,
        List<PointProjetDTO> points,
        double largeurBloc,
        double hauteurBloc,
        double prixUnitaireBloc
) implements Serializable {}


package BatiBloc.Domaine.Dto;

import java.io.Serializable;
import java.util.List;

public record ZoneProjetDTO(double hauteurBloc, double largeurBloc, double prixUnitaireBloc,
        List<PointProjetDTO> points, TypeFormeDTO forme, TypeZoneDTO type) implements Serializable {}
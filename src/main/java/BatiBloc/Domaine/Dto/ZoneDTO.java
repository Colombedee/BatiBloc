package BatiBloc.Domaine.Dto;

import java.awt.Point;
import java.util.List;

public final class ZoneDTO {

    private final TypeZoneDTO type;
    private final TypeFormeDTO forme;
    private final List<Point> points;
    private final boolean selectionnee;
    private final List<BlocDTO> blocs;

    public ZoneDTO(TypeZoneDTO type, TypeFormeDTO forme, List<Point> points,
                   boolean selectionnee,List<BlocDTO> blocs) {
        this.type = type;
        this.forme = forme;
        this.points = List.copyOf(points);
        this.selectionnee = selectionnee;
        this.blocs = blocs == null ? List.of() : List.copyOf(blocs);
    }

    public TypeZoneDTO getType() {
        return this.type;
    }

    public TypeFormeDTO getForme() {
        return this.forme;
    }

    public List<Point> getPoints() {
        return this.points;
    }

    public boolean estSelectionne() {
        return this.selectionnee;
    }    
        
    public List<BlocDTO> getBlocs() {
        return this.blocs;    
    }
}
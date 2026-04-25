package BatiBloc.Domaine.Dto;

import java.awt.Point;
import java.util.List;

public final class ZoneDTO {

    private final boolean selectionnee;
    private final List<BlocDTO> blocs;
    private final List<Point> points;
    private final TypeFormeDTO forme;
    private final TypeZoneDTO type;

    public ZoneDTO(boolean selectionnee, List<BlocDTO> blocs, List<Point> points,
            TypeFormeDTO forme, TypeZoneDTO type) {
        this.selectionnee = selectionnee;
        this.blocs = blocs == null ? List.of() : List.copyOf(blocs);
        this.points = List.copyOf(points);        
        this.forme = forme;
        this.type = type;
    }
    
    public boolean estSelectionne() {
        return this.selectionnee;
    }
    
    public List<BlocDTO> getBlocs() {
        return this.blocs;    
    }
    
    public List<Point> getPoints() {
        return this.points;
    }
    
    public TypeFormeDTO getForme() {
        return this.forme;
    }

    public TypeZoneDTO getType() {
        return this.type;
    }
}
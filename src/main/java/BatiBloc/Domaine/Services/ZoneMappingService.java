package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.TypeFormeDTO;
import BatiBloc.Domaine.Dto.TypeZoneDTO;
import BatiBloc.Domaine.Modeles.Formes.*;
import BatiBloc.Domaine.Modeles.Zones.*;

import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author nicky
 */
public class ZoneMappingService {

    public Forme creerForme(TypeFormeDTO type, List<Point2D.Double> pts) {
        return switch (type) {
            case RECTANGLE ->
                new Rectangle(pts);
            case TRIANGLE ->
                new Triangle(pts);
            case TRIANGLE_TRONQUE ->
                new TriangleTronque(pts);
            default ->
                null;
        };
    }

    public Zone creerZone(TypeZoneDTO type, Forme forme, List<Point2D.Double> pts) {
        return switch (type) {
            case ARMATURE_CLASSIQUE ->
                new ArmatureClassique(forme, pts);
            case ARMATURE_BLOCS ->
                new ArmatureBlocs(forme, pts);
            case OUVERTURE ->
                new Ouverture(forme, pts);
            default ->
                null;
        };
    }

    public TypeZoneDTO typeZone(Zone zone) {
        if (zone instanceof ArmatureClassique) {
            return TypeZoneDTO.ARMATURE_CLASSIQUE;
        }
        if (zone instanceof ArmatureBlocs) {
            return TypeZoneDTO.ARMATURE_BLOCS;
        }
        return TypeZoneDTO.OUVERTURE;
    }

    public TypeFormeDTO typeForme(Forme forme) {
        if (forme instanceof Rectangle) {
            return TypeFormeDTO.RECTANGLE;
        }
        if (forme instanceof Triangle) {
            return TypeFormeDTO.TRIANGLE;
        }
        return TypeFormeDTO.TRIANGLE_TRONQUE;
    }
}

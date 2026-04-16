
package BatiBloc.Domaine.Dto;
import java.io.Serializable;

/**
 *
 * @author nicky
 * Ajout pour sauvegarde/rechargement sur disque de l’état complet du projet.
 */
public record PointProjetDTO(double x, double y) implements Serializable {}


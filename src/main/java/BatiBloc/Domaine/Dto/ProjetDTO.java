package BatiBloc.Domaine.Dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nicky
 */
public record ProjetDTO(
        List<VueProjetDTO> vues,
        int indexVueCourante,
        double largeurBlocDefaut,
        double hauteurBlocDefaut
) implements Serializable {}

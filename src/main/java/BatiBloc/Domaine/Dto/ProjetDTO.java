package BatiBloc.Domaine.Dto;

import java.io.Serializable;
import java.util.List;

public record ProjetDTO(double hauteurBlocDefaut, double largeurBlocDefaut,
                int indexVueCourante, List<VueProjetDTO> vues) implements Serializable {}
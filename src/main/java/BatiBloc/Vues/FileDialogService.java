package BatiBloc.Vues;

import BatiBloc.Domaine.Services.ExportFichierService;
import static BatiBloc.Domaine.Services.ExportFichierService.TypeExport.PDF;
import static BatiBloc.Domaine.Services.ExportFichierService.TypeExport.PNG;
import static BatiBloc.Domaine.Services.ExportFichierService.TypeExport.PROJET;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Service de gestion des dialogues de fichiers.
 * Cette classe contient TOUTE la logique Swing (JFileChooser).
 * 
 * Elle est destinée à être utilisée uniquement par le contrôleur
 * pour afficher les dialogues de sélection de fichiers.
 * 
 * @author Équipe7
 */
public class FileDialogService {

    /**
     * Affiche un dialogue de sauvegarde pour un export.
     * 
     * @param typeExport Le type d'export (PDF, PNG, PROJET)
     * @return Le fichier sélectionné, ou null si l'utilisateur annule
     */
    public static File afficherDialogueSauvegarde(ExportFichierService.TypeExport typeExport) {
        if (typeExport == null) {
            return null;
        }

        JFileChooser fichierChoisi = new JFileChooser();
        fichierChoisi.setDialogTitle(ExportFichierService.getTitreDialogue(typeExport));

        String extension = ExportFichierService.getExtension(typeExport);
        String description = getDescriptionExport(typeExport);

        fichierChoisi.setFileFilter(new FileNameExtensionFilter(description, extension));

        if (fichierChoisi.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fichierChoisi.getSelectedFile();
        }

        return null;
    }

    /**
     * Affiche un dialogue d'ouverture pour importer un fichier.
     * 
     * @return Le fichier sélectionné, ou null si l'utilisateur annule
     */
    public static File afficherDialogueOuverture() {
        JFileChooser fichierChoisi = new JFileChooser();
        fichierChoisi.setDialogTitle("Importer un fichier");

        FileNameExtensionFilter filtrePdf = new FileNameExtensionFilter("PDF (*.pdf)", "pdf");
        FileNameExtensionFilter filtrePng = new FileNameExtensionFilter("PNG (*.png)", "png");
        FileNameExtensionFilter filtreProjet = new FileNameExtensionFilter("Projet BâtiBloc (*.batibloc)", "batibloc");

        fichierChoisi.addChoosableFileFilter(filtrePdf);
        fichierChoisi.addChoosableFileFilter(filtrePng);
        fichierChoisi.addChoosableFileFilter(filtreProjet);
        fichierChoisi.setFileFilter(filtrePdf);

        if (fichierChoisi.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fichierChoisi.getSelectedFile();
        }

        return null;
    }

    /**
     * Retourne la description d'un type d'export pour le dialogue.
     * 
     * @param typeExport Le type d'export
     * @return La description formatée
     */
    private static String getDescriptionExport(ExportFichierService.TypeExport typeExport) {
        return switch (typeExport) {
            case PDF -> "PDF (*.pdf)";
            case PNG -> "PNG (*.png)";
            case PROJET -> "Projet BâtiBloc (*.batibloc)";
        };
    }
}

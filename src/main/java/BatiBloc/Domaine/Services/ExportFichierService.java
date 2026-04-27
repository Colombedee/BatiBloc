package BatiBloc.Domaine.Services;
import java.io.File;

/**
 * Service métier pour gérer les exports de fichiers.
 * Responsabilités : validation et gestion des extensions.
 * 
 * NOTE: La sélection du fichier (JFileChooser) doit être gérée 
 * par le contrôleur ou une classe dédiée à l'interface utilisateur.
 */

public class ExportFichierService {

    public enum TypeExport {

        PDF("pdf", "Exporter en PDF"),
        PNG("png", "Exporter en PNG"),
        PROJET("batibloc", "Exporter le projet");

        private final String extension;
        private final String titre;

        TypeExport(String extension, String titre) {
            this.extension = extension;
            this.titre = titre;
        }

        public String getExtension() {
            return extension;
        }

        public String getTitre() {
            return titre;
        }
    }

    /**
     * Valide et prépare le fichier avec la bonne extension.
     * 
     * @param fichier Le fichier sélectionné (sans extension garantie)
     * @param type Le type d'export
     * @return Le fichier avec l'extension correcte
     */
    public static File validerFichierExport(File fichier, TypeExport type) {
        if (fichier == null || type == null) {
            return null;
        }
        
                String chemin = fichier.getAbsolutePath();
        String extension = type.getExtension();

        if (!chemin.toLowerCase().endsWith("." + extension)) {
            return new File(chemin + "." + extension);
        }
        return fichier;
    }

    /**
     * Retourne l'extension associée au type d'export.
     * 
     * @param type Le type d'export
     * @return L'extension sans le point
     */
    public static String getExtension(TypeExport type) {
        return type != null ? type.getExtension() : null;
    }

    /**
     * Retourne le titre du dialogue pour le type d'export.
     * 
     * @param type Le type d'export
     * @return Le titre du dialogue
     */
    public static String getTitreDialogue(TypeExport type) {
        return type != null ? type.getTitre() : null;
    }
}
        

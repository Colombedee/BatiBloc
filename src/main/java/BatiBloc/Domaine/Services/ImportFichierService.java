package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.FichierImporteDTO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Service métier pour gérer les imports de fichiers.
 * Responsabilités : chargement et validation des fichiers importés.
 * 
 * NOTE: La sélection du fichier (JFileChooser) doit être gérée 
 * par le contrôleur ou une classe dédiée à l'interface utilisateur.
 */

public class ImportFichierService {

    /**
     * Charge un fichier selon son extension.
     * Supporte : PDF, PNG, et projets BâtiBloc.
     * 
     * @param fichier Le fichier à charger
     * @return Un DTO contenant le fichier chargé, ou null si erreur
     * @throws IOException Si le fichier ne peut pas être lu
     */
    public static FichierImporteDTO chargerFichier(File fichier) throws IOException {
        if (fichier == null || !fichier.exists()) {
            return null;
        }

        String extension = getExtension(fichier);



         if (extension.equalsIgnoreCase("pdf")) {
            try {
                PDDocument documentPdf = PDDocument.load(fichier);
                return new FichierImporteDTO(fichier.getName(), documentPdf);
            } catch (IOException e) {
                throw new IOException("Erreur lors du chargement du PDF: " + e.getMessage(), e);
            }
        }
        
        if (extension.equalsIgnoreCase("png")) {
            try {
                BufferedImage image = ImageIO.read(fichier);
                if (image == null) {
                    throw new IOException("Le fichier PNG n'a pas pu être lu.");
                }
                return new FichierImporteDTO(fichier.getName(), image);
            } catch (IOException e) {
                throw new IOException("Erreur lors du chargement du PNG: " + e.getMessage(), e);
            }
        }
        
        if (extension.equalsIgnoreCase("batibloc")) {
            return new FichierImporteDTO(fichier.getName(), fichier);
        }

        throw new IOException("Format de fichier non supporté: " + extension);
    }

    /**
     * Extrait l'extension d'un fichier.
     * 
     * @param file Le fichier
     * @return L'extension sans le point, en minuscules
     */
    public static String getExtension(File file) {
        if (file == null) {
            return "";
        }
        
        String nom = file.getName();
        int lastIndex = nom.lastIndexOf(".");
        
        if (lastIndex == -1) {
            return "";
        }
        
         return nom.substring(lastIndex + 1).toLowerCase();
    }

    /**
     * Vérifie si un fichier a l'extension attendue.
     * 
     * @param fichier Le fichier à vérifier
     * @param extensionAttendue L'extension attendue (sans point)
     * @return true si le fichier a la bonne extension
     */
    public static boolean validerExtension(File fichier, String extensionAttendue) {
        if (fichier == null || extensionAttendue == null) {
            return false;
        }
        
        String extension = getExtension(fichier);
        return extension.equalsIgnoreCase(extensionAttendue);
      }
}
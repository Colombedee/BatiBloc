package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.FichierImporteDTO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class ImportFichierService {

    public static FichierImporteDTO importer() {
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
            File fichier = fichierChoisi.getSelectedFile();
            String extension = getExtension(fichier);

            if (extension.equalsIgnoreCase("pdf")) {
                try {
                    PDDocument documentPdf = PDDocument.load(fichier);
                    return new FichierImporteDTO(fichier.getName(), documentPdf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (extension.equalsIgnoreCase("png")) {
                try {
                    BufferedImage image = ImageIO.read(fichier);
                    return new FichierImporteDTO(fichier.getName(), image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (extension.equalsIgnoreCase("batibloc")) {
                return new FichierImporteDTO(fichier.getName(), fichier);
            }
        }
        return null;
    }

    private static String getExtension(File file) {
        String nom = file.getName();
        int lastIndex = nom.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return nom.substring(lastIndex + 1);
    }
}
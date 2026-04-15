package BatiBloc.Domaine.Services;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportFichierPdfService {

    public static File fichierExporteDTO() {

        File repertoire = new File("C:\\");

        JFileChooser fichierChoisi = new JFileChooser((File) null);

        if (repertoire.exists()) {
            fichierChoisi.setCurrentDirectory(repertoire);
        }

        FileNameExtensionFilter filtrePdf = new FileNameExtensionFilter("PDF", "pdf");
        FileNameExtensionFilter filtrePng = new FileNameExtensionFilter("PNG (*.png)", "png");
        
        fichierChoisi.addChoosableFileFilter(filtrePdf);
        fichierChoisi.addChoosableFileFilter(filtrePng);
        fichierChoisi.setFileFilter(filtrePdf);

        fichierChoisi.setDialogTitle("exportType");
        int resultat = fichierChoisi.showSaveDialog(null);

        if (resultat == JFileChooser.APPROVE_OPTION) {
            File fichier = fichierChoisi.getSelectedFile();
            String chemin = fichier.getAbsolutePath();
            
            String extension = "pdf";
            if (fichierChoisi.getFileFilter() == filtrePng) {
                extension = "png";
            }

            if (!chemin.toLowerCase().endsWith("." + extension)) {
                fichier = new File(chemin + "." + extension);
            }
            return fichier;
        }
        return null;
    }
}
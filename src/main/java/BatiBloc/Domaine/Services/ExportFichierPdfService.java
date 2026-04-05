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

        FileNameExtensionFilter filtre = new FileNameExtensionFilter("PDF", "pdf");
        fichierChoisi.setFileFilter(filtre);

        fichierChoisi.setDialogTitle("Exporter en PDF");
        int resultat = fichierChoisi.showSaveDialog(null);

        if (resultat == JFileChooser.APPROVE_OPTION) {
            File fichier = fichierChoisi.getSelectedFile();
            String chemin = fichier.getAbsolutePath();

            if (!chemin.toLowerCase().endsWith(".pdf")) {
                fichier = new File(chemin + ".pdf");
            }
            return fichier;
        }
        return null;
    }
}
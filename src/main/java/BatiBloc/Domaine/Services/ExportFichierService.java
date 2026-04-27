package BatiBloc.Domaine.Services;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportFichierService {

    public enum TypeExport {
        PDF,
        PNG,
        PROJET
    }

    public static File fichierExporteDTO(TypeExport type) {
        JFileChooser fichierChoisi = new JFileChooser();

        switch (type) {
            case PDF:
                fichierChoisi.setDialogTitle("Exporter en PDF");
                fichierChoisi.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
                break;
            case PNG:
                fichierChoisi.setDialogTitle("Exporter en PNG");
                fichierChoisi.setFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
                break;
            case PROJET:
                fichierChoisi.setDialogTitle("Exporter le projet");
                fichierChoisi.setFileFilter(new FileNameExtensionFilter("Projet BâtiBloc (*.batibloc)", "batibloc"));
                break;
        }

        if (fichierChoisi.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fichier = fichierChoisi.getSelectedFile();
            String extension = "";

            switch (type) {
                case PDF: extension = "pdf"; break;
                case PNG: extension = "png"; break;
                case PROJET: extension = "batibloc"; break;
            }

            String chemin = fichier.getAbsolutePath();
            if (!chemin.toLowerCase().endsWith("." + extension)) {
                fichier = new File(chemin + "." + extension);
            }
            return fichier;
        }
        return null;
    }
}
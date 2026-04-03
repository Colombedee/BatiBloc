package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.FichierImporteDTO;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;

public class ImportFichierPdfService {
    
    public static FichierImporteDTO importer() {
        
        File repertoire = new File("C:\\");

        JFileChooser fichierChoisi = new JFileChooser((File) null);

        if (repertoire.exists()) {
            fichierChoisi.setCurrentDirectory(repertoire);
        }

        FileNameExtensionFilter filtre = new FileNameExtensionFilter("PDF", "pdf");
        fichierChoisi.setFileFilter(filtre);
        
        int resultat = fichierChoisi.showOpenDialog(null);

        if(resultat == JFileChooser.APPROVE_OPTION) {
            File fichier = fichierChoisi.getSelectedFile();
            String extension = getExtension(fichier);
            
            try {            
                if (extension.equalsIgnoreCase("pdf")) {
                    PDDocument documentPdf = PDDocument.load(fichier);
                    return new FichierImporteDTO(fichier.getName(), documentPdf);
                }
                else {
                    throw new IllegalArgumentException("Type de fichier non supporté.");
                }       
            } 
            catch (IOException e) {
                e.printStackTrace();
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
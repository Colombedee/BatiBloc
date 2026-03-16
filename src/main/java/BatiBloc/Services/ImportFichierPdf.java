package BatiBloc.Services;

import BatiBloc.Dto.FichierImporteDTO;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;

public class ImportFichierPdf {
    
    public static FichierImporteDTO importer() {
        JFileChooser fichierChoisi = new JFileChooser();
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
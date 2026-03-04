package BatiBloc.Utils;

import BatiBloc.Dto.FichierImporte;
import java.awt.Desktop;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;

public class ImportationFichierPdfOuPng 
{
    
    public static FichierImporte fichierImporte()
    {
        JFileChooser fichierChoisi = new JFileChooser();
        FileNameExtensionFilter filtre = new FileNameExtensionFilter("PDF et Images PNG", "png", "pdf");
        fichierChoisi.setFileFilter(filtre);   
        
        int resultat = fichierChoisi.showOpenDialog(null);

        if(resultat == JFileChooser.APPROVE_OPTION)
        {
            File file = fichierChoisi.getSelectedFile();
            String extension = getExtension(file);
            
            try 
            {            
                if (extension.equalsIgnoreCase("pdf")) {
                    PDDocument document = PDDocument.load(file);
                    return new FichierImporte(file.getName(), document);
                } 
                else if (extension.equalsIgnoreCase("png")) {
                    BufferedImage image = ImageIO.read(file);
                    return new FichierImporte(file.getName(), image);
                } 
                else {
                    throw new IllegalArgumentException("Type de fichier non supporté.");
                }       
            } 
            catch (IOException e) 
            {
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

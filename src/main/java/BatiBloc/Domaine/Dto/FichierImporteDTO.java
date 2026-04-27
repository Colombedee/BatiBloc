package BatiBloc.Domaine.Dto;

import java.awt.image.BufferedImage;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;

public class FichierImporteDTO {

    private final String nomFichier;
    private final Object contenu;

    public FichierImporteDTO(String nomFichier, Object contenu) {
        this.nomFichier = nomFichier;
        this.contenu = contenu;
    }

    public Object getContent() {
        return this.contenu;
    }

    public boolean isPDF() {
        return this.contenu instanceof PDDocument;
    }

    public boolean isProjet() {
        return this.contenu instanceof File;
    }

    public boolean isPNG() {
        return this.contenu instanceof BufferedImage;
    }
}
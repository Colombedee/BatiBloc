package BatiBloc.Dto;

public class FichierImporte {

    private String nomFichier;
    private Object contenu;

    public FichierImporte(String nomFichier, Object contenu) {
        this.nomFichier = nomFichier;
        this.contenu = contenu;
    }

    public String getFileName() {
        return nomFichier;
    }

    public Object getContent() {
        return contenu;
    }

    public boolean isPDF() {
        return contenu instanceof org.apache.pdfbox.pdmodel.PDDocument;
    }

    public boolean isImage() {
        return contenu instanceof java.awt.image.BufferedImage;
    }
}
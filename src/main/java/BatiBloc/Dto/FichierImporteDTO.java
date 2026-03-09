package BatiBloc.Dto;

public class FichierImporteDTO {

    private final String nomFichier;
    private final Object contenu;

    public FichierImporteDTO(String nomFichier, Object contenu) {
        this.nomFichier = nomFichier;
        this.contenu = contenu;
    }

    public Object getContent() {
        return contenu;
    }

    public boolean isPDF() {
        return contenu instanceof org.apache.pdfbox.pdmodel.PDDocument;
    }
}
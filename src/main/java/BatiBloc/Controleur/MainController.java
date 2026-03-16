package BatiBloc.Controleur;

import BatiBloc.Dto.FichierImporteDTO;
import BatiBloc.Dto.VueDTO;
import BatiBloc.Services.ImportFichierPdf;
import BatiBloc.dto.RognerVueDTO;
import BatiBloc.langue.Langage;
import BatiBloc.langue.LangueManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class MainController {

    private final List<BufferedImage> listeVues = new ArrayList<>();
    private int indexVueCourante = -1;

    public void importerFichierPdf() {
        FichierImporteDTO fichier = ImportFichierPdf.importer();
        
        if (fichier == null) {
            return;
        }

        this.listeVues.clear();
        this.indexVueCourante = -1;

        if (fichier.isPDF()) {
            PDDocument documentPdf = (PDDocument) fichier.getContent();
            try {
                PDFRenderer renderer = new PDFRenderer(documentPdf);
                for (int i = 0; i < documentPdf.getNumberOfPages(); i++) {
                    BufferedImage img = renderer.renderImageWithDPI(i, 150, ImageType.RGB);
                    listeVues.add(img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    documentPdf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!this.listeVues.isEmpty()) {
            this.indexVueCourante = 0;
        }
    }
    
    public void exporterFichierPdf(){
        
    }
    
    public void changerLangue() {
        if (LangueManager.getLangageCourant() == Langage.FR) {
            this.setLangue(Langage.EN);
        } else {
            this.setLangue(Langage.FR);
        }
    }

    public void setLangue(Langage langue) {
        LangueManager.setLangage(langue);
    }
    
    public void naviguerVuePrecedente() {
        if (this.indexVueCourante > 0) {
            this.indexVueCourante--;
        }
    }

    public void naviguerVueSuivante() {
        if (this.indexVueCourante >= 0 && this.indexVueCourante < this.listeVues.size() - 1) {
            this.indexVueCourante++;
        }
    }

    public void supprimerVueCourante() {
        if (this.indexVueCourante >= 0) {
            this.supprimerVues(List.of(this.indexVueCourante));
        }
    }
    
    public void supprimerVues(List<Integer> indices) {
        indices.stream()
            .sorted(Comparator.reverseOrder())
            .forEach(i -> {
                if (i >= 0 && i < this.listeVues.size()) {
                    this.listeVues.remove((int) i);
                }
            });

        if (this.listeVues.isEmpty()) {
            this.indexVueCourante = -1;
        } else if (this.indexVueCourante >= this.listeVues.size()) {
            this.indexVueCourante = this.listeVues.size() - 1;
        }
    }
 
    public void rognerVueCourante(RognerVueDTO zone) {
        if (this.indexVueCourante < 0 || this.indexVueCourante >= this.listeVues.size() || zone == null) {
            return;
        }

        BufferedImage imageOrigine = this.listeVues.get(this.indexVueCourante);

        int x = Math.max(0, zone.getX());
        int y = Math.max(0, zone.getY());
        int w = Math.min(zone.getLargeur(), imageOrigine.getWidth() - x);
        int h = Math.min(zone.getHauteur(), imageOrigine.getHeight() - y);

        if (w <= 0 || h <= 0) {
            return;
        }

        BufferedImage imageRognee = imageOrigine.getSubimage(x, y, w, h);
        this.listeVues.set(this.indexVueCourante, imageRognee);
    }

    public VueDTO getVueCourante() {
        if (this.indexVueCourante < 0 || this.indexVueCourante >= this.listeVues.size()) {
            return null;
        }
        return new VueDTO(this.indexVueCourante, this.listeVues.get(this.indexVueCourante));
    }
    
    public List<VueDTO> getVues() {
        List<VueDTO> listeVuesDto = new ArrayList<>();
        for (int i = 0; i < this.listeVues.size(); i++) {
            listeVuesDto.add(new VueDTO(i, this.listeVues.get(i)));
        }
        return listeVuesDto;
    }
}
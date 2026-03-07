package BatiBloc.Controller;

import BatiBloc.Dto.FichierImporte;
import BatiBloc.Dto.VueDTO;
import BatiBloc.Utils.ImportationFichierPdfOuPng;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class MainController {

    private final List<BufferedImage> vues = new ArrayList<>();
    private int indexVueCourante = -1;

    /** Importer un fichier PDF ou PNG et créer les vues. */
    public void importerFichier() {
        FichierImporte fichier = ImportationFichierPdfOuPng.fichierImporte();
        if (fichier == null) {
            return;
        }

        vues.clear();
        indexVueCourante = -1;

        if (fichier.isPDF()) {
            PDDocument doc = (PDDocument) fichier.getContent();
            try {
                PDFRenderer renderer = new PDFRenderer(doc);
                for (int i = 0; i < doc.getNumberOfPages(); i++) {
                    BufferedImage img = renderer.renderImageWithDPI(i, 150, ImageType.RGB);
                    vues.add(img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    doc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (fichier.isImage()) {
            vues.add((BufferedImage) fichier.getContent());
        }

        if (!vues.isEmpty()) {
            indexVueCourante = 0;
        }
    }

    public void vueSuivante() {
        if (indexVueCourante >= 0 && indexVueCourante < vues.size() - 1) {
            indexVueCourante++;
        }
    }

    public void vuePrecedente() {
        if (indexVueCourante > 0) {
            indexVueCourante--;
        }
    }

    public void supprimerVueCourante() {
    if (indexVueCourante >= 0) {
        supprimerVues(List.of(indexVueCourante));
    }
}

    /** Rogner l'image de la vue courante selon un rectangle en pixels. */
    public void rognerVueCourante(Rectangle zone) {
        if (indexVueCourante < 0 || indexVueCourante >= vues.size()) {
            return;
        }

        BufferedImage originale = vues.get(indexVueCourante);
        int x = Math.max(0, zone.x);
        int y = Math.max(0, zone.y);
        int w = Math.min(zone.width,  originale.getWidth()  - x);
        int h = Math.min(zone.height, originale.getHeight() - y);

        if (w <= 0 || h <= 0) {
            return;
        }

        BufferedImage rognee = originale.getSubimage(x, y, w, h);
        vues.set(indexVueCourante, rognee);
    }

    /** DTO pour que la vue affiche l'image courante. */
    public VueDTO getVueCourante() {
        if (indexVueCourante < 0 || indexVueCourante >= vues.size()) {
            return null;
        }
        String nom = "Vue " + (indexVueCourante + 1);
        return new VueDTO(nom, vues.get(indexVueCourante));
    }
    
    public List<VueDTO> getToutesLesVues() {
    List<VueDTO> dtos = new ArrayList<>();
    for (int i = 0; i < vues.size(); i++) {
        dtos.add(new VueDTO("Vue " + (i + 1), vues.get(i)));
    }
    return dtos;
}

public void supprimerVues(List<Integer> indices) {
    indices.stream()
        .sorted(Comparator.reverseOrder())
        .forEach(i -> {
            if (i >= 0 && i < vues.size()) {
                vues.remove((int) i);
            }
        });
    if (vues.isEmpty()) {
        indexVueCourante = -1;
    } else if (indexVueCourante >= vues.size()) {
        indexVueCourante = vues.size() - 1;
    }
}

    public boolean aDesVues() {
        return !vues.isEmpty();
    }
}


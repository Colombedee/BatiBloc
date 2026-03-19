package BatiBloc.Controleur;

import BatiBloc.Modeles.*; // Ajouter par Emmanuella 
import BatiBloc.Dto.*;

import BatiBloc.Services.ImportFichierPdf;
import BatiBloc.dto.RognerVueDTO;
import BatiBloc.langue.Langage;
import BatiBloc.langue.LangueManager;
import java.awt.Point; // Ajouter par Emmanuella 
import java.awt.geom.Point2D; // Ajouter par Emmanuella 

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class MainController {
    
    private static final double DPI_RENDU = 150.0; // Échelle par défaut 1 pixel = 1/150 pouce


    private final List<Vue> ProjetVues = new ArrayList<>();
    private int indexVueCourante = -1;
    private double largeurBlocDefaut = 96.0; // Largeur de bloc par défaut


    // IMPORTATION PDF
    public void importerFichierPdf() {
        FichierImporteDTO fichier = ImportFichierPdf.importer();
        
        if (fichier == null) {
            return;
        }

        this.projetVues.clear();
        this.indexVueCourante = -1;

        if (fichier.isPDF()) {
            PDDocument documentPdf = (PDDocument) fichier.getContent();
            try {
                PDFRenderer renderer = new PDFRenderer(documentPdf);
                for (int i = 0; i < documentPdf.getNumberOfPages(); i++) {
                    BufferedImage img = renderer.renderImageWithDPI(i, 150, ImageType.RGB);
                    
                    //Ajouter par Emmanuella
                    Vue vue = new(ing);
                    double echelle = lireEchellePdf(documentPdf, i); // Lire échelle du PDF si disponible 
                    vue.setEchelle(echelle);                         // sinon on utilise la valeur par défaut 
                    
                    projetVues.add(new Vue(img));
                    
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

        if (!this.projetVues.isEmpty()) {
            this.indexVueCourante = 0;
        }
    }
    
        //  Ajouter par Emmanuella 
        // 1 unité PDF = 1/72 pouce
        // Rendu à 150 DPI donc 1 pixel = (1/150) pouce
        private double lireEchellePdf(PDDocument doc, int pageIndex) {
        try {
            
            return 1.0 / DPI_RENDU;
        } catch (Exception e) {
            return ECHELLE_DEFAUT;
            
    public void exporterFichierPdf(){
        
        // À implementer
    }
        
    }

    // ---GESTION DE L'ÉCHELLE ---
    public void calibrerEchelle(double distancePixels, int pieds, double pouces) {
        if (indexVueCourante >= 0 && distancePixels > 0) {
            double totalInches = (pieds * 12) + pouces;
            double ratio = totalInches / distancePixels;
            projetVues.get(indexVueCourante).setEchelle(ratio);
        }
    }
    
        public double getEchelleCourante() {
        if (indexVueCourante < 0) return ECHELLE_DEFAUT;
        return projetVues.get(indexVueCourante).getEchelle();
    }

    public void setEchelleCourante(double echelle) {
        if (indexVueCourante >= 0 && echelle > 0) {
            projetVues.get(indexVueCourante).setEchelle(echelle);
        }
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
    
    // ---NAVIGATION---
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
        this.projetVues.remove(this.indexVueCourante);
            if (this.projetVues.isEmpty()) {
                this.indexVueCourante = -1;
            } else if (this.indexVueCourante >= this.projetVues.size()) {
                this.indexVueCourante = this.projetVues.size() - 1;
            }
        }
    }   
      
    
    // --- SUPPRIMER_VUES ---
    public void supprimerVues(List<Integer> indices) {
        indices.stream()
            .sorted(Comparator.reverseOrder())
            .forEach(i -> {
                if (i >= 0 && i < this.projetVues.size()) {
                    this.projetVues.remove((int) i);
                }
            });

        if (this.projetVues.isEmpty()) {
            this.indexVueCourante = -1;
        } else if (this.indexVueCourante >= this.projetVues.size()) {
            this.indexVueCourante = this.projetVues.size() - 1;
        }
    }
    
    
    // --- ROGNER_VUE_COURANTE --
    public void rognerVueCourante(RognerVueDTO zone) {
        if (this.indexVueCourante < 0 || zone == null) {
            return;
        }
        
        Vue vue = this.projetVues.get(this.indexVueCourante);
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
    
    //---DTO --

    public VueDTO getVueCourante() {
        if (this.indexVueCourante < 0) {
            return null;
        }
        
        Vue modeleVue = this.projetVues.get(indexVueCourante);
        double echelle = modeleVue.getEchelle();
        
        List<ZoneDTO> zoneDtos = new ArrayList<>();
        for (Zone z : modeleVue.getZones()) {
            zoneDtos.add(new ZoneDTO(z.getForme(), echelle));
        }
                
        return new VueDTO(this.indexVueCourante, modeleVue.getImageDeFond(), ZoneDtos);
    }
    
    public List<VueDTO> getVues() {
        List<VueDTO> projetVuesDto = new ArrayList<>();
        for (int i = 0; i < this.projetVues.size(); i++) {
            Vue v = projetues.get(i);
            dtos.add(new VueDTO(i, v.getImageDeFond(), new ArrayList<>()));
        
        }
        return dtos;
    }
}
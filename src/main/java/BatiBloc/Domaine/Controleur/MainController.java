package BatiBloc.Domaine.Controleur;

import BatiBloc.Domaine.Services.SimulationService;
import BatiBloc.Domaine.Dto.SimulationDTO;
import BatiBloc.Domaine.Modeles.*;
import BatiBloc.Domaine.Dto.*;
import BatiBloc.Domaine.Services.ExportFichierPdfService;
import BatiBloc.Domaine.Services.ImportFichierPdfService;
import BatiBloc.Domaine.dto.RognerVueDTO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class MainController {
    
    private static final double DPI_RENDU = 150.0; // Échelle par défaut 1 pixel = 1/150 pouce
    private static final double DPI_ECRAN = 96.0;
    private static final double ECHELLE_DEFAUT = 1.0 / DPI_ECRAN;
    private final List<Vue> projetVues = new ArrayList<>();
    private int indexVueCourante = -1;
    private double largeurBlocDefaut = 96.0; // Largeur de bloc par défaut (8 pieds)
    private double hauteurBlocDefaut = 12.0;   // 12 pouces
    
    private final UndoRedoManager<ProjetMemento> undoRedoManager = new UndoRedoManager<>();
    
    private static class ProjetMemento {
    private final List<Vue> vues;
    private final int indexVueCourante;
    private final double largeurBlocDefaut;
    private final double hauteurBlocDefaut;

        private ProjetMemento(List<Vue> vues, int indexVueCourante, double largeurBlocDefaut, double hauteurBlocDefaut) {
            this.vues = vues;
            this.indexVueCourante = indexVueCourante;
            this.largeurBlocDefaut = largeurBlocDefaut;
            this.hauteurBlocDefaut = hauteurBlocDefaut;
        }
    }

    
    private BufferedImage genererImageAvecZones(Vue vue) //Colombe
    {
        BufferedImage original = vue.getImage();
        BufferedImage resultat = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resultat.createGraphics();
        g.drawImage(original, 0, 0, null);

        for (Zone z : vue.getZones()) 
        {
            java.util.List<java.awt.Point> pts = z.getForme().getPosition();
            int[] xs = pts.stream().mapToInt(p -> p.x).toArray();
            int[] ys = pts.stream().mapToInt(p -> p.y).toArray();

            TypeZone type = getTypeZone(z);
            java.awt.Color couleur = type == TypeZone.ARMATURE_CLASSIQUE
                ? new java.awt.Color(34, 139, 34, 80)
                : new java.awt.Color(0, 0, 255, 80);

            g.setColor(couleur);
            g.fillPolygon(xs, ys, pts.size());
            g.setColor(couleur.darker());
            g.setStroke(new java.awt.BasicStroke(2));
            g.drawPolygon(xs, ys, pts.size());
        }
        g.dispose();
        return resultat;
    }
    
    public void annulerAction(){
        this.undoRedoManager.annuler(this::creerMemento, this::restaurerMemento);  
    }
    
    public void retablirAction(){
        this.undoRedoManager.retablir(this::creerMemento, this::restaurerMemento);
    }
    
    public void ajouterVue(){
        
    }

    public void importerPdf() {
        FichierImporteDTO fichier = ImportFichierPdfService.importer();
        
        if (fichier == null) {
            return;
        }
        
        this.enregistrerEtatAvantModification();
        this.projetVues.clear();
        this.indexVueCourante = -1;

        this.projetVues.clear();
        this.indexVueCourante = -1;

        if (fichier.isPDF()) {
            PDDocument documentPdf = (PDDocument) fichier.getContent();
            try {
                PDFRenderer renderer = new PDFRenderer(documentPdf);
                for (int i = 0; i < documentPdf.getNumberOfPages(); i++) {
                    BufferedImage img = renderer.renderImageWithDPI(i, 150, ImageType.RGB);

                    Vue vue = new Vue(img);
                    double echelle = lireEchellePdf(documentPdf, i); // Lire échelle du PDF si disponible 
                    vue.setEchelle(echelle);                         // sinon on utilise la valeur par défaut 
                    projetVues.add(vue);
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
        }
    }
            
    public void exporterVues(List<Integer> indices) {
        if (indices == null || indices.isEmpty()) {
            return;
        }

        File fichierSortie = ExportFichierPdfService.fichierExporteDTO();
        if (fichierSortie == null) {
            return;
        }

        try (PDDocument document = new PDDocument()) {

            for (int index : indices) {
                if (index < 0 || index >= projetVues.size()) continue;

                BufferedImage image = genererImageAvecZones(projetVues.get(index));

                PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
                document.addPage(page);

                PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);

                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    cs.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
                }
            }
            document.save(fichierSortie);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---GESTION DE L'ÉCHELLE --- (Ajouter Par Emmanuella)
    public void calibrerEchelle(double distancePixels, int pieds, double pouces) {
        if (indexVueCourante >= 0 && distancePixels > 0) {
            double totalPouces = (pieds * 12) + pouces;
            double echelle = totalPouces / distancePixels;
            this.enregistrerEtatAvantModification();
            projetVues.get(indexVueCourante).setEchelle(echelle);
        }
    }
    
        public double getEchelleCourante() {
        if (indexVueCourante < 0) return ECHELLE_DEFAUT;
        return projetVues.get(indexVueCourante).getEchelle();
    }

    public void setEchelleCourante(double echelle) {
        if (indexVueCourante >= 0 && echelle > 0) {
            this.enregistrerEtatAvantModification();
            projetVues.get(indexVueCourante).setEchelle(echelle);
        }
    }
    
    public void naviguerVuePrecedente() {
        if (this.indexVueCourante > 0) {
            this.indexVueCourante--;
        }
    }

    public void naviguerVueSuivante() {
        if (this.indexVueCourante >= 0 && this.indexVueCourante < this.projetVues.size() - 1) {
            this.indexVueCourante++;
        }
    }

    public void supprimerVueCourante() {
        if (this.indexVueCourante >= 0) {
            this.enregistrerEtatAvantModification();
            this.projetVues.remove(this.indexVueCourante);
            if (this.projetVues.isEmpty()) {
                this.indexVueCourante = -1;
            } else if (this.indexVueCourante >= this.projetVues.size()) {
                this.indexVueCourante = this.projetVues.size() - 1;
            }
        }
    }   
    
    public void supprimerVues(List<Integer> indices) {
        indices.stream()
            .sorted(Comparator.reverseOrder())
            .forEach(i -> {
                if (i >= 0 && i < this.projetVues.size()) {
                    this.projetVues.remove((int) i);
                }
            });
        
        this.enregistrerEtatAvantModification();
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
    
    public void rognerVueCourante(RognerVueDTO zone) {
        if (this.indexVueCourante < 0 || zone == null) {
            return;
        }
        
        Vue vue = this.projetVues.get(this.indexVueCourante);
        BufferedImage imageOrigine = vue.getImage();
        int x = Math.max(0, zone.getX());
        int y = Math.max(0, zone.getY());
        int w = Math.min(zone.getLargeur(), imageOrigine.getWidth() - x);
        int h = Math.min(zone.getHauteur(), imageOrigine.getHeight() - y);
        
       if (w > 0 && h > 0) {
            this.enregistrerEtatAvantModification();
            BufferedImage imageRognee = imageOrigine.getSubimage(x, y, w, h);
            vue.setImage(imageRognee);
        }
    }
    
    // @roneh Pour le rognage inverser
    public void rognerInverseVueCourante(RognerVueDTO zone) {
    if (this.indexVueCourante < 0 || zone == null) {
        return;
    }

    Vue vue = this.projetVues.get(this.indexVueCourante);
    BufferedImage imageOrigine = vue.getImage();

    if (imageOrigine == null) {
        return;
    }

    int x = Math.max(0, zone.getX());
    int y = Math.max(0, zone.getY());
    int w = Math.min(zone.getLargeur(), imageOrigine.getWidth() - x);
    int h = Math.min(zone.getHauteur(), imageOrigine.getHeight() - y);

    if (w <= 0 || h <= 0) {
        return;
    }

    BufferedImage copie = new BufferedImage(
        imageOrigine.getWidth(),
        imageOrigine.getHeight(),
        imageOrigine.getType() == 0 ? BufferedImage.TYPE_INT_RGB : imageOrigine.getType()
    );
    
    this.enregistrerEtatAvantModification();

    Graphics2D g2d = copie.createGraphics();
    g2d.drawImage(imageOrigine, 0, 0, null);
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x, y, w, h);
    g2d.dispose();

    vue.setImage(copie);
}

    // ---GESTION DES ZONES --- (Ajoutée Par Colombe)
    public void ajouterZone(TypeZone type, TypeForme forme, List<Point> points)
    {
        if (indexVueCourante < 0) return;
        Forme formeModele = creerForme(forme, points);

        if (formeModele == null) return;
        Zone zone = creerZone(type, formeModele);

        if (zone == null) return;
        this.enregistrerEtatAvantModification();
        projetVues.get(indexVueCourante).ajouterZone(zone);
    }
    
    public void supprimerZone(int indexZone)
    {
        if (indexVueCourante < 0) return;
        Vue vue = projetVues.get(indexVueCourante);
        if (indexZone < 0 || indexZone >= vue.getZones().size()) return;
        this.enregistrerEtatAvantModification();
        vue.supprimerZone(indexZone);
    }
    
    public void selectionnerZone(Point point)
    {
        if (indexVueCourante < 0) return;
        projetVues.get(indexVueCourante).selectionnerZone(point);
    }
    
    public void deplacerZoneSelectionnee(int dx, int dy)
    {
        if (indexVueCourante < 0) return;
        if (dx == 0 && dy == 0) return;
        if (projetVues.get(indexVueCourante).getZoneSelectionnee() == null) return;
        this.enregistrerEtatAvantModification();
        projetVues.get(indexVueCourante).deplacerZoneSelectionnee(dx, dy);
    }
    
    //Colombe
    public void redimensionnerZoneSelectionnee(int indexPoignee, int dx, int dy) 
    {
        if (indexVueCourante < 0) return;
        if (dx == 0 && dy == 0) return;
        if (projetVues.get(indexVueCourante).getZoneSelectionnee() == null) return;
        this.enregistrerEtatAvantModification();
        projetVues.get(indexVueCourante).redimensionnerZoneSelectionnee(indexPoignee, dx, dy);
    }
    public void redimensionnerParBoundingBox(int indexPoignee, int dx, int dy) 
    {
        if (indexVueCourante < 0) return;
        if (dx == 0 && dy == 0) return;
        if (projetVues.get(indexVueCourante).getZoneSelectionnee() == null) return;
        this.enregistrerEtatAvantModification();
        projetVues.get(indexVueCourante).redimensionnerParBoundingBox(indexPoignee, dx, dy);
    }
    private Forme creerForme(TypeForme type, List<Point> points)
    {
        switch (type)
        {
            case RECTANGLE: return new RectangleForme(points);
            case TRIANGLE: return new Triangle(points);
            case TRIANGLE_TRONQUE: return new TriangleTronque(points);
            default: return null;   
        }
    }
              
    private Zone creerZone(TypeZone type, Forme forme) {
        switch (type) {
            case ARMATURE_CLASSIQUE:
                return new ArmatureClassique(forme, largeurBlocDefaut, hauteurBlocDefaut);

            case ARMATURE_BLOCS:
                return new ArmatureEnBlocs(forme, largeurBlocDefaut, hauteurBlocDefaut);

            case OUVERTURE:
            default:
                // Livrable 4 : on ne gère pas encore la simulation des ouvertures
                return null;
        }
    }

            
    private TypeZone getTypeZone(Zone zone)      
    {
        if (zone instanceof ArmatureClassique) 
        {
            return TypeZone.ARMATURE_CLASSIQUE;
        }
        if (zone instanceof ArmatureEnBlocs)
        {
            return TypeZone.ARMATURE_BLOCS;
        }
        return TypeZone.OUVERTURE;
    }
    
    /**
 * Modifie les dimensions des blocs de la zone actuellement sélectionnée.
 * Cette méthode doit être appelée avant la simulation pour appliquer
 * les dimensions saisies par l'utilisateur dans l'interface.
 */
public void modifierDimensionsBloc(double largeur, double hauteur) {
    if (indexVueCourante < 0 || largeur < 6.0 || hauteur <= 0) {
        return;
    }

    Vue vue = this.projetVues.get(indexVueCourante);
    Zone zone = vue.getZoneSelectionnee();

    if (zone != null) {
        zone.setLargeurBloc(largeur);
        zone.setHauteurBloc(hauteur);
    }
    
    this.enregistrerEtatAvantModification();
    zone.setLargeurBloc(largeur);
    zone.setHauteurBloc(hauteur);

    // Mémorise aussi les valeurs par défaut pour les prochaines zones
    this.largeurBlocDefaut = largeur;
    this.hauteurBlocDefaut = hauteur;
}


    
    
    
    public SimulationDTO simulerZoneSelectionnee() {
    if (indexVueCourante < 0) {
        return null;
    }

    Vue vue = this.projetVues.get(indexVueCourante);
    Zone zone = vue.getZoneSelectionnee();

    if (zone == null) {
        return null;
    }

    // Livrable 4 : pas de simulation pour les ouvertures
    if (zone instanceof Ouverture) {
        return null;
    }

    // Version minimale honnête : simulation seulement des rectangles
    if (!(zone.getForme() instanceof RectangleForme)) {
        return null;
    }

    return SimulationService.simuler(zone);
}
    
    public void commencerModificationComposee() {
        this.undoRedoManager.commencerModificationComposee();
    }

    public void terminerModificationComposee() {
        this.undoRedoManager.terminerModificationComposee();
    }

    private void enregistrerEtatAvantModification() {
        this.undoRedoManager.enregistrerEtatAvantModification(this::creerMemento);
    }

    private ProjetMemento creerMemento() {
        return new ProjetMemento(
                this.copierVues(this.projetVues),
                this.indexVueCourante,
                this.largeurBlocDefaut,
                this.hauteurBlocDefaut
        );
    }

    private void restaurerMemento(ProjetMemento memento) {
        this.projetVues.clear();
        this.projetVues.addAll(this.copierVues(memento.vues));
        this.indexVueCourante = memento.indexVueCourante;
        this.largeurBlocDefaut = memento.largeurBlocDefaut;
        this.hauteurBlocDefaut = memento.hauteurBlocDefaut;
    }

    private List<Vue> copierVues(List<Vue> vuesSource) {
        List<Vue> copies = new ArrayList<>();
        for (Vue vueSource : vuesSource) {
            copies.add(this.copierVue(vueSource));
        }
        return copies;
    }

    private Vue copierVue(Vue vueSource) {
        Vue copie = new Vue(this.copierImage(vueSource.getImage()));
        copie.setEchelle(vueSource.getEchelle());
        copie.setZoom(vueSource.getZoom());

        Zone zoneSelectionneeCopie = null;
        Zone zoneSelectionneeSource = vueSource.getZoneSelectionnee();

        for (Zone zoneSource : vueSource.getZones()) {
            Zone zoneCopie = this.copierZone(zoneSource);
            copie.ajouterZone(zoneCopie);

            if (zoneSource == zoneSelectionneeSource) {
                zoneSelectionneeCopie = zoneCopie;
            }
        }

        copie.definirZoneSelectionnee(zoneSelectionneeCopie);
        return copie;
    }

    private Zone copierZone(Zone zoneSource) {
        Forme formeCopie = this.copierForme(zoneSource.getForme());

        if (zoneSource instanceof ArmatureClassique) {
            return new ArmatureClassique(formeCopie, zoneSource.getLargeurBloc(), zoneSource.getHauteurBloc());
        }
        if (zoneSource instanceof ArmatureEnBlocs) {
            return new ArmatureEnBlocs(formeCopie, zoneSource.getLargeurBloc(), zoneSource.getHauteurBloc());
        }
        if (zoneSource instanceof Ouverture) {
            Ouverture ouverture = (Ouverture) zoneSource;
            return new Ouverture(formeCopie, zoneSource.getLargeurBloc(), ouverture.getType());
        }

        return null;
    }

    private Forme copierForme(Forme formeSource) {
        List<Point> pointsCopie = this.copierPoints(formeSource.getPosition());

        if (formeSource instanceof RectangleForme) {
            return new RectangleForme(pointsCopie);
        }
        if (formeSource instanceof Triangle) {
            return new Triangle(pointsCopie);
        }
        return new TriangleTronque(pointsCopie);
    }

    private BufferedImage copierImage(BufferedImage imageSource) {
        if (imageSource == null) {
            return null;
        }

        int type = imageSource.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : imageSource.getType();
        BufferedImage copie = new BufferedImage(imageSource.getWidth(), imageSource.getHeight(), type);
        Graphics2D g2d = copie.createGraphics();
        g2d.drawImage(imageSource, 0, 0, null);
        g2d.dispose();
        return copie;
    }

    private List<Point> copierPoints(List<Point> pointsSource) {
        List<Point> copies = new ArrayList<>();
        for (Point point : pointsSource) {
            copies.add(new Point(point));
        }
        return copies;
    }


    
    private TypeForme getTypeForme(Forme forme)
    {
        if (forme instanceof RectangleForme)
        {
            return TypeForme.RECTANGLE;
        }
        if (forme instanceof Triangle)
        {
            return TypeForme.TRIANGLE;
        }
        return TypeForme.TRIANGLE_TRONQUE;
    }
    
    public VueDTO getVueCourante() 
    {
        if (this.indexVueCourante < 0) 
        {
            return null;
        }
        
        Vue modeleVue = this.projetVues.get(indexVueCourante);
        List<ZoneDTO> zoneDtos = new ArrayList<>();
        for (Zone z : modeleVue.getZones()) 
        {
            TypeZone type = getTypeZone(z);
            TypeForme forme = getTypeForme(z.getForme());
            zoneDtos.add(new ZoneDTO(type, forme, z.getForme().getPosition(), 
                    z == modeleVue.getZoneSelectionnee()));
        }        
        return new VueDTO(this.indexVueCourante, modeleVue.getImage(), zoneDtos);
    }
    
    public List<VueDTO> getVues() {
        List<VueDTO> dtos = new ArrayList<>();

        for (int i = 0; i < this.projetVues.size(); i++) {
            Vue v = this.projetVues.get(i);
            List<ZoneDTO> zoneDtos = new ArrayList<>();

            for (Zone z : v.getZones()) {
                TypeZone type = getTypeZone(z);
                TypeForme forme = getTypeForme(z.getForme());

                zoneDtos.add(new ZoneDTO(
                        type,
                        forme,
                        z.getForme().getPosition(),
                        z == v.getZoneSelectionnee()
                ));
            }

            dtos.add(new VueDTO(i, v.getImage(), zoneDtos));
        }

        return dtos;
    }

}
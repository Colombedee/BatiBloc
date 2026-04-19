package BatiBloc.Domaine.Controleur;

import BatiBloc.Domaine.Dto.*;
import BatiBloc.Domaine.Modeles.*;
import BatiBloc.Domaine.Modeles.Formes.*;
import BatiBloc.Domaine.Modeles.Zones.*;
import BatiBloc.Domaine.Services.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * 
 * @authors Nicky,Colombe,Emmanuella,William,Eliazardson
 * 
 * Réécriture propre du MainController.
 *
 * 1) Réorganisation de la classe en sections cohérentes :
 *    - constantes et état interne
 *    - undo / redo
 *    - gestion des vues
 *    - import / export
 *    - échelle
 *    - zones
 *    - simulation
 *    - DTO / conversions
 *    - copie profonde / memento
 *
 * 2) Conservation de la logique actuelle :
 *    - aucune modification volontaire du comportement métier
 *    - mêmes signatures publiques
 *    - mêmes dépendances et même flux global du projet
 *
 * 3) Nettoyage de forme uniquement :
 *    - indentation uniforme
 *    - gardes plus lisibles
 *    - regroupement logique des méthodes
 *    - simplification visuelle sans changer l’exécution
 *
 * 4) Conservation du bloc de code commenté :
 *    - le bloc de code commenté final est conservé tel quel, sans modification
 *
 * 5) Ajustements minimaux ajoutés :
 *    - simulerZoneSelectionnee(...) ajoute aussi les ouvertures de la vue courante
 *      quand la zone sélectionnée est une ArmatureBlocs ;
 *    - genererImageAvecZones(...) rend les ouvertures avec une couleur distincte ;
 *    - gardes défensives minimales dans ajouterZone(...) et selectionnerZone(...).
 */
public class MainController {

    // =========================
    // Constantes
    // =========================

    private static final float DPI_RENDU = 150.0f; // Échelle par défaut 1 pixel = 1/150 pouce
    private static final double DPI_ECRAN = 96.0;
    private static final double ECHELLE_DEFAUT = 1.0 / DPI_ECRAN;
    private static final double COUT_UNITAIRE_PAR_BLOC_DEFAUT = 20.0;

    // =========================
    // État du projet
    // =========================

    private final List<Vue> projetVues = new ArrayList<>();
    private int indexVueCourante = -1;
    private double largeurBlocDefaut = 96.0; // Largeur de bloc par défaut (8 pieds)
    private double hauteurBlocDefaut = 12.0; // 12 pouces

    private final ZoneInteractionService interactionService = new ZoneInteractionService();
    private final UndoRedoService<ProjetMemento> undoRedoManager = new UndoRedoService<>();

    // =========================
    // Memento
    // =========================

    private static class ProjetMemento {
        private final List<Vue> vues;
        private final int indexVueCourante;
        private final double largeurBlocDefaut;
        private final double hauteurBlocDefaut;

        private ProjetMemento(List<Vue> vues, int index, double largeur, double hauteur) {
            this.vues = vues;
            this.indexVueCourante = index;
            this.largeurBlocDefaut = largeur;
            this.hauteurBlocDefaut = hauteur;
        }
    }

    // =========================
    // Undo / Redo
    // =========================

    private void enregistrerEtatAvantModification() {
        this.undoRedoManager.enregistrerEtatAvantModification(this::creerMemento);
    }

    private ProjetMemento creerMemento() {
        return new ProjetMemento(
                copierVues(this.projetVues),
                this.indexVueCourante,
                this.largeurBlocDefaut,
                this.hauteurBlocDefaut
        );
    }

    private void restaurerMemento(ProjetMemento memento) {
        this.projetVues.clear();
        this.projetVues.addAll(copierVues(memento.vues));
        this.indexVueCourante = memento.indexVueCourante;
        this.largeurBlocDefaut = memento.largeurBlocDefaut;
        this.hauteurBlocDefaut = memento.hauteurBlocDefaut;
        this.interactionService.reinitialiser();
    }

    public void annulerAction() {
        this.undoRedoManager.annuler(this::creerMemento, this::restaurerMemento);
    }

    public void retablirAction() {
        this.undoRedoManager.retablir(this::creerMemento, this::restaurerMemento);
    }

    public void commencerModificationComposee() {
        this.undoRedoManager.modificationComposee();
    }

    public void terminerModificationComposee() {
        this.undoRedoManager.modificationComposee();
    }

    // =========================
    // Getters(Accesseurs) simples
    // =========================

    public double getLargeurBlocDefaut() {
        return this.largeurBlocDefaut;
    }

    public double getHauteurBlocDefaut() {
        return this.hauteurBlocDefaut;
    }

    public double getEchelleCourante() {
        if (this.indexVueCourante < 0) {
            return ECHELLE_DEFAUT;
        }
        return this.projetVues.get(this.indexVueCourante).getEchelle().getPouces();
    }

    public void setEchelleCourante(double echelle) {
        if (this.indexVueCourante >= 0 && echelle > 0) {
            this.enregistrerEtatAvantModification();
            this.projetVues.get(this.indexVueCourante).setEchelle(new Echelle(echelle));
        }
    }

    // =========================
    // Gestion des vues
    // =========================

    public void ajouterVue() {
        this.enregistrerEtatAvantModification();

        int largeur = 1200;
        int hauteur = 1600;

        if (!this.projetVues.isEmpty() && this.indexVueCourante >= 0) {
            BufferedImage courante = this.projetVues.get(this.indexVueCourante).getImage();
            largeur = courante.getWidth();
            hauteur = courante.getHeight();
        }

        BufferedImage imageVide = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = imageVide.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, largeur, hauteur);
        g.dispose();

        Vue nouvelleVue = new Vue(imageVide);
        nouvelleVue.setEchelle(new Echelle(ECHELLE_DEFAUT));
        this.projetVues.add(nouvelleVue);

        this.indexVueCourante = this.projetVues.size() - 1;
    }

    public void naviguerVuePrecedente() {
        if (this.indexVueCourante > 0) {
            this.indexVueCourante--;
            this.interactionService.reinitialiser();
        }
    }

    public void naviguerVueSuivante() {
        if (this.indexVueCourante >= 0 && this.indexVueCourante < this.projetVues.size() - 1) {
            this.indexVueCourante++;
            this.interactionService.reinitialiser();
        }
    }

    public void supprimerVueCourante() {
        if (this.indexVueCourante >= 0) {
            this.enregistrerEtatAvantModification();
            this.projetVues.remove(this.indexVueCourante);
            this.indexVueCourante = this.projetVues.isEmpty()
                    ? -1
                    : Math.min(this.indexVueCourante, this.projetVues.size() - 1);
            this.interactionService.reinitialiser();
        }
    }

    public void supprimerVues(List<Integer> indices) {
        if (indices == null || indices.isEmpty()) {
            return;
        }

        this.enregistrerEtatAvantModification();

        indices.stream()
                .sorted(Comparator.reverseOrder())
                .forEach(i -> {
                    if (i >= 0 && i < this.projetVues.size()) {
                        this.projetVues.remove((int) i);
                    }
                });

        this.indexVueCourante = this.projetVues.isEmpty()
                ? -1
                : Math.min(this.indexVueCourante, this.projetVues.size() - 1);

        this.interactionService.reinitialiser();
    }

    // =========================
    // Rognage
    // =========================

    // @roneh
    public void rognerVueCourante(VueRogneeDTO zone) {
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
            vue.setImage(imageOrigine.getSubimage(x, y, w, h));
        }
    }

    // =========================
    // Import / Export PDF
    // =========================

    public void importerPdf() {
        FichierImporteDTO fichier = ImportFichierPdfService.importer();

        if (fichier == null) {
            return;
        }

        this.enregistrerEtatAvantModification();
        this.projetVues.clear();
        this.indexVueCourante = -1;
        this.interactionService.reinitialiser();

        if (fichier.isPDF()) {
            PDDocument documentPdf = (PDDocument) fichier.getContent();

            try {
                PDFRenderer renderer = new PDFRenderer(documentPdf);

                for (int i = 0; i < documentPdf.getNumberOfPages(); i++) {
                    BufferedImage img = renderer.renderImageWithDPI(i, DPI_RENDU, ImageType.RGB);
                    Vue vue = new Vue(img);
                    vue.setEchelle(new Echelle(lireEchellePdf(documentPdf, i)));
                    this.projetVues.add(vue);
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

    // Ajoutée par Emmanuella
    // 1 pouces par pixel
    private double lireEchellePdf(PDDocument documentPdf, int pageIndex) {
        return 1.0;
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
                if (index < 0 || index >= this.projetVues.size()) {
                    continue;
                }

                BufferedImage image = genererImageAvecZones(this.projetVues.get(index));
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

    // =========================
    // Gestion de l’échelle
    // =========================

    // ---GESTION DE L'ÉCHELLE --- (Ajoutée Par Emmanuella)
    public void calibrerEchelle(double distancePixels, int pieds, double pouces) {
        if (this.indexVueCourante < 0 || distancePixels <= 0) {
            return;
        }

        double totalPouces = (pieds * 12.0) + pouces;
        this.enregistrerEtatAvantModification();
        this.projetVues.get(this.indexVueCourante)
                .setEchelle(new Echelle(totalPouces / distancePixels));
    }

    // =========================
    // Gestion des zones
    // =========================

    // ---GESTION DES ZONES --- (Ajoutée Par Colombe)
    public void ajouterZone(TypeZoneDTO type, TypeFormeDTO forme, List<Point> points) {
        if (this.indexVueCourante < 0 || type == null || forme == null || points == null || points.isEmpty()) {
            return;
        }

        List<Point2D.Double> pts = convertirVersPoints2D(points);
        Forme formeModele = creerForme(forme, pts);

        if (formeModele == null) {
            return;
        }

        Zone zone = creerZone(type, formeModele, pts);

        if (zone == null) {
            return;
        }

        this.enregistrerEtatAvantModification();
        this.projetVues.get(this.indexVueCourante).ajouterZone(zone);
    }

    public void supprimerZone(int indexZone) {
        if (this.indexVueCourante < 0) {
            return;
        }

        Vue vue = this.projetVues.get(this.indexVueCourante);
        List<Zone> zones = vue.getZones();

        if (indexZone < 0 || indexZone >= zones.size()) {
            return;
        }

        this.enregistrerEtatAvantModification();

        Zone aSupprimer = zones.get(indexZone);
        vue.supprimerZone(aSupprimer);

        if (this.interactionService.getZoneSelectionnee() == aSupprimer) {
            this.interactionService.reinitialiser();
        }
    }

    public void selectionnerZone(Point point) {
        if (this.indexVueCourante < 0 || point == null) {
            return;
        }

        Vue vue = this.projetVues.get(this.indexVueCourante);
        Zone selectionnee = this.interactionService.selectionner(
                vue.getZones(),
                new Point2D.Double(point.x, point.y)
        );
        vue.definirZoneSelectionnee(selectionnee);
    }

    public void deplacerZoneSelectionnee(int dx, int dy) {
        if (this.indexVueCourante < 0 || (dx == 0 && dy == 0)) {
            return;
        }

        if (this.interactionService.getZoneSelectionnee() == null) {
            return;
        }

        this.enregistrerEtatAvantModification();
        this.interactionService.deplacer(dx, dy);
    }

    // Colombe
    public void redimensionnerZoneSelectionnee(int indexPoignee, int dx, int dy) {
        if (this.indexVueCourante < 0 || (dx == 0 && dy == 0)) {
            return;
        }

        if (this.interactionService.getZoneSelectionnee() == null) {
            return;
        }

        this.enregistrerEtatAvantModification();
        this.interactionService.redimensionner(indexPoignee, dx, dy);
    }

    public void redimensionnerParBoundingBox(int indexPoignee, int dx, int dy) {
        if (this.indexVueCourante < 0 || (dx == 0 && dy == 0)) {
            return;
        }

        if (this.interactionService.getZoneSelectionnee() == null) {
            return;
        }

        this.enregistrerEtatAvantModification();
        this.interactionService.redimensionner(indexPoignee, dx, dy);
    }

    // =========================
    // Simulation
    // =========================

    public SimulationResultatDTO simulerZoneSelectionnee() {
        return simulerZoneSelectionnee(COUT_UNITAIRE_PAR_BLOC_DEFAUT);
    }

    public SimulationResultatDTO simulerZoneSelectionnee(double prixUnitaireBloc) {
        if (this.indexVueCourante < 0) {
            return null;
        }

        Vue vue = this.projetVues.get(this.indexVueCourante);
        Zone zone = this.interactionService.getZoneSelectionnee();

        if (zone == null) {
            return null;
        }

        List<Zone> zonesASimuler = new ArrayList<>();
        zonesASimuler.add(zone);

        // Correction :
        // si la zone sélectionnée est une armature de blocs, on ajoute aussi
        // les ouvertures de la vue courante pour que SimulationService puisse
        // les soustraire correctement.
        if (zone instanceof ArmatureBlocs) {
            for (Zone z : vue.getZones()) {
                if (z instanceof Ouverture) {
                    zonesASimuler.add(z);
                }
            }
        }

        return new SimulationService().simulerToutesLesZones(
                zonesASimuler,
                this.hauteurBlocDefaut,
                this.largeurBlocDefaut,
                prixUnitaireBloc
        );
    }

    public void modifierDimensionsBloc(double largeur, double hauteur) {
        this.largeurBlocDefaut = largeur;
        this.hauteurBlocDefaut = hauteur;
    }

    /**
     * Simule toutes les zones ARMATURE_BLOCS de la vue courante.
     * @param prixUnitaireBloc prix d'un bloc (saisi par l'utilisateur)
     * @return DTO global (somme de toutes les zones simulées)
     */
    public SimulationResultatDTO simulerVueCourante(double prixUnitaireBloc) {
        if (this.indexVueCourante < 0) {
            return null;
        }

        return new SimulationService().simulerToutesLesZones(
                this.projetVues.get(this.indexVueCourante).getZones(),
                this.hauteurBlocDefaut,
                this.largeurBlocDefaut,
                prixUnitaireBloc
        );
    }

    // =========================
    // DTO exposés à l’interface utilisateur
    // =========================

    public VueDTO getVueCourante() {
        if (this.indexVueCourante < 0) {
            return null;
        }
        return construireVueDTO(this.projetVues.get(this.indexVueCourante), this.indexVueCourante);
    }

    public List<VueDTO> getVues() {
        List<VueDTO> dtos = new ArrayList<>();

        for (int i = 0; i < this.projetVues.size(); i++) {
            dtos.add(construireVueDTO(this.projetVues.get(i), i));
        }

        return dtos;
    }
    
    
    private List<BlocDTO> convertirBlocs(Zone zone) {
    if (!(zone instanceof ArmatureBlocs armature)) {
        return List.of();
    }

    List<BlocDTO> resultat = new ArrayList<>();

    for (Bloc bloc : armature.getBlocsSimules()) {
        resultat.add(new BlocDTO(
                bloc.getX(),
                bloc.getY(),
                bloc.getLargeur(),
                bloc.getHauteur(),
                bloc.getPrix()
        ));
    }

    return resultat;
   }

    private VueDTO construireVueDTO(Vue vue, int index) {
    List<ZoneDTO> zoneDtos = new ArrayList<>();
    Zone selectionnee = vue.getZoneSelectionnee();

    for (Zone z : vue.getZones()) {
        zoneDtos.add(new ZoneDTO(
                typeZone(z),
                typeForme(z.getForme()),
                convertirVersPoints(z.getPoints()),
                z == selectionnee,
                convertirBlocs(z)
        ));
    }

    return new VueDTO(
            vue.getImage(),
            vue.getEchelle().getPouces(),
            index,
            zoneDtos
    );
  }

    // =========================
    // Copie profonde
    // =========================

    private List<Vue> copierVues(List<Vue> vuesSource) {
        List<Vue> copies = new ArrayList<>();

        for (Vue vueSource : vuesSource) {
            copies.add(this.copierVue(vueSource));
        }

        return copies;
    }

    private Vue copierVue(Vue vueSource) {
        Vue copie = new Vue(copierImage(vueSource.getImage()));
        copie.setEchelle(vueSource.getEchelle());
        copie.setZoom(vueSource.getZoom());

        Zone zoneSelectionneeSource = vueSource.getZoneSelectionnee();
        Zone zoneSelectionneeCopie = null;

        for (Zone zoneSource : vueSource.getZones()) {
            Zone zoneCopie = copierZone(zoneSource);
            copie.ajouterZone(zoneCopie);

            if (zoneSource == zoneSelectionneeSource) {
                zoneSelectionneeCopie = zoneCopie;
            }
        }

        copie.definirZoneSelectionnee(zoneSelectionneeCopie);
        return copie;
    }

    private Zone copierZone(Zone zoneSource) {
        List<Point2D.Double> pts = copierPoints2D(zoneSource.getPoints());
        Forme formeCopie = copierForme(zoneSource.getForme(), pts);

        if (zoneSource instanceof ArmatureClassique) {
            return new ArmatureClassique(formeCopie, pts);
        }
        if (zoneSource instanceof ArmatureBlocs) {
            return new ArmatureBlocs(formeCopie, pts);
        }
        if (zoneSource instanceof Ouverture) {
            return new Ouverture(formeCopie, pts);
        }

        return null;
    }

    private Forme copierForme(Forme formeSource, List<Point2D.Double> pts) {
        if (formeSource instanceof Rectangle) {
            return new Rectangle(pts);
        }
        if (formeSource instanceof Triangle) {
            return new Triangle(pts);
        }
        return new TriangleTronque(pts);
    }

    private BufferedImage copierImage(BufferedImage imageSource) {
        if (imageSource == null) {
            return null;
        }

        int type = imageSource.getType() == 0
                ? BufferedImage.TYPE_INT_ARGB
                : imageSource.getType();

        BufferedImage copie = new BufferedImage(
                imageSource.getWidth(),
                imageSource.getHeight(),
                type
        );

        Graphics2D g2d = copie.createGraphics();
        g2d.drawImage(imageSource, 0, 0, null);
        g2d.dispose();

        return copie;
    }

    private List<Point2D.Double> copierPoints2D(List<Point2D.Double> src) {
        List<Point2D.Double> copies = new ArrayList<>();

        for (Point2D.Double p : src) {
            copies.add(new Point2D.Double(p.x, p.y));
        }

        return copies;
    }

    // =========================
    // Création / typage domaine
    // =========================

    private Forme creerForme(TypeFormeDTO type, List<Point2D.Double> pts) {
        return switch (type) {
            case RECTANGLE -> new Rectangle(pts);
            case TRIANGLE -> new Triangle(pts);
            case TRIANGLE_TRONQUE -> new TriangleTronque(pts);
            default -> null;
        };
    }

    private Zone creerZone(TypeZoneDTO type, Forme forme, List<Point2D.Double> pts) {
        return switch (type) {
            case ARMATURE_CLASSIQUE -> new ArmatureClassique(forme, pts);
            case ARMATURE_BLOCS -> new ArmatureBlocs(forme, pts);
            case OUVERTURE -> new Ouverture(forme, pts);
            default -> null;
        };
    }

    private TypeZoneDTO typeZone(Zone zone) {
        if (zone instanceof ArmatureClassique) {
            return TypeZoneDTO.ARMATURE_CLASSIQUE;
        }
        if (zone instanceof ArmatureBlocs) {
            return TypeZoneDTO.ARMATURE_BLOCS;
        }
        return TypeZoneDTO.OUVERTURE;
    }

    private TypeFormeDTO typeForme(Forme forme) {
        if (forme instanceof Rectangle) {
            return TypeFormeDTO.RECTANGLE;
        }
        if (forme instanceof Triangle) {
            return TypeFormeDTO.TRIANGLE;
        }
        return TypeFormeDTO.TRIANGLE_TRONQUE;
    }

    // =========================
    // Conversions de points
    // =========================

    private List<Point2D.Double> convertirVersPoints2D(List<Point> points) {
        List<Point2D.Double> resultat = new ArrayList<>();

        for (Point p : points) {
            resultat.add(new Point2D.Double(p.x, p.y));
        }

        return resultat;
    }

    private List<Point> convertirVersPoints(List<Point2D.Double> points) {
        List<Point> resultat = new ArrayList<>();

        for (Point2D.Double p : points) {
            resultat.add(new Point((int) Math.round(p.x), (int) Math.round(p.y)));
        }

        return resultat;
    }

    // =========================
    // Génération image export
    // =========================

    private BufferedImage genererImageAvecZones(Vue vue) {
        BufferedImage original = vue.getImage();
        BufferedImage resultat = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = resultat.createGraphics();
        g.drawImage(original, 0, 0, null);

        for (Zone z : vue.getZones()) {
            List<Point2D.Double> pts = z.getPoints();
            int[] xs = pts.stream().mapToInt(p -> (int) p.x).toArray();
            int[] ys = pts.stream().mapToInt(p -> (int) p.y).toArray();

            Color couleur;
            if (z instanceof ArmatureClassique) {
                couleur = new Color(34, 139, 34, 80);
            } else if (z instanceof ArmatureBlocs) {
                couleur = new Color(0, 0, 255, 80);
            } else {
                couleur = new Color(255, 165, 0, 80);
            }

            g.setColor(couleur);
            g.fillPolygon(xs, ys, pts.size());
            g.setColor(couleur.darker());
            g.setStroke(new java.awt.BasicStroke(2));
            g.drawPolygon(xs, ys, pts.size());
        }

        g.dispose();
        return resultat;
    }

//  
//    /**
// * Modifie les dimensions des blocs de la zone actuellement sélectionnée.
// * Cette méthode doit être appelée avant la simulation pour appliquer
// * les dimensions saisies par l'utilisateur dans l'interface.
// */

//    this.enregistrerEtatAvantModification();
//
//    // Appliquer à toutes les zones de toutes les vues
//    for (Vue vue : this.projetVues) {
//        for (Zone zone : vue.getZones()) {
//            if (zone instanceof ArmatureBlocs) {
//                zone.setLargeurBloc(largeur);
//                zone.setHauteurBloc(hauteur);
//            }
//        }
//    }
//    
//    // Mémorise aussi les valeurs par défaut pour les prochaines zones
//    this.largeurBlocDefaut = largeur;
//    this.hauteurBlocDefaut = hauteur;
//}
//  
//    private List<Point> copierPoints(List<Point> pointsSource) {
//        List<Point> copies = new ArrayList<>();
//        for (Point point : pointsSource) {
//            copies.add(new Point(point));
//        }
//        return copies;
//    }

}

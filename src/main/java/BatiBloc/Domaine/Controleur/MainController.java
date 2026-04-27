package BatiBloc.Domaine.Controleur;
import BatiBloc.Vues.FileDialogService;

import static BatiBloc.Domaine.Constantes.ConstantesBloc.COUT_UNITAIRE_PAR_BLOC_DEFAUT;
import static BatiBloc.Domaine.Constantes.ConstantesVue.DPI_RENDU;
import static BatiBloc.Domaine.Constantes.ConstantesVue.ECHELLE_DEFAUT;

import BatiBloc.Domaine.Dto.*;
import BatiBloc.Domaine.Modeles.*;
import BatiBloc.Domaine.Modeles.Formes.Forme;
import BatiBloc.Domaine.Modeles.Zones.*;
import BatiBloc.Domaine.Services.*;
import BatiBloc.Domaine.Services.ExportFichierService.TypeExport;

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
import javax.imageio.ImageIO;

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
 * @author william, nicky, emmanuella, colombe, eliazardson
 */
public class MainController {

    // =========================
    // État du projet
    // =========================

    private final List<Vue> projetVues = new ArrayList<>();
    private int indexVueCourante = -1;
    private double largeurBlocDefaut = 96.0;
    private double hauteurBlocDefaut = 12.0;

    // =========================
    // Services
    // =========================

    private final ZoneInteractionService interactionService = new ZoneInteractionService();
    private final UndoRedoService<ProjetMemento> undoRedoManager = new UndoRedoService<>();
    private final OuvertureService ouvertureService = new OuvertureService();
    private final RenduImageService renduService = new RenduImageService();
    private final ZoneMappingService mappingService = new ZoneMappingService();
    private final CopieProfondeService copieService = new CopieProfondeService();
    private final SimulationFacadeService simulationFacade = new SimulationFacadeService();

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
                copieService.copierVues(this.projetVues),
                this.indexVueCourante,
                this.largeurBlocDefaut,
                this.hauteurBlocDefaut
        );
    }

    private void restaurerMemento(ProjetMemento memento) {
        if (memento == null) return;

        this.projetVues.clear();
        this.projetVues.addAll(copieService.copierVues(memento.vues));
        this.indexVueCourante = memento.indexVueCourante;
        this.largeurBlocDefaut = memento.largeurBlocDefaut;
        this.hauteurBlocDefaut = memento.hauteurBlocDefaut;
        this.interactionService.reinitialiser();
        synchroniserArmaturesAutomatiquesToutesLesVues();
    }

    public void annulerAction() {
        this.undoRedoManager.annuler(this::creerMemento, this::restaurerMemento);
    }

    public void retablirAction() {
        this.undoRedoManager.retablir(this::creerMemento, this::restaurerMemento);
    }

    public void commencerModificationComposee() {
        this.undoRedoManager.commencerModificationComposee(this::creerMemento);
    }

    public void terminerModificationComposee() {
        this.undoRedoManager.terminerModificationComposee();
    }

    public boolean canUndo() {
        return this.undoRedoManager.canUndo();
    }

    public boolean canRedo() {
        return this.undoRedoManager.canRedo();
    }

    // =========================
    // Getters simples
    // =========================

    public double getLargeurBlocDefaut() { return this.largeurBlocDefaut; }
    public double getHauteurBlocDefaut() { return this.hauteurBlocDefaut; }

    public double getEchelleCourante() {
        if (this.indexVueCourante < 0) return ECHELLE_DEFAUT;
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

        int largeur = 1200, hauteur = 1600;

        if (!this.projetVues.isEmpty() && this.indexVueCourante >= 0) {
            BufferedImage courante = this.projetVues.get(this.indexVueCourante).getImage();
            if (courante != null) {
                largeur = courante.getWidth();
                hauteur = courante.getHeight();
            }
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
        this.interactionService.reinitialiser();
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
            this.indexVueCourante = this.projetVues.isEmpty() ? -1 : Math.min(this.indexVueCourante, this.projetVues.size() - 1);
            this.interactionService.reinitialiser();
        }
    }

    public void supprimerVues(List<Integer> indices) {
        if (indices == null || indices.isEmpty()) return;

        this.enregistrerEtatAvantModification();
        indices.stream().sorted(Comparator.reverseOrder()).forEach(i -> {
            if (i >= 0 && i < this.projetVues.size()) this.projetVues.remove((int) i);
        });

        this.indexVueCourante = this.projetVues.isEmpty() ? -1 : Math.min(this.indexVueCourante, this.projetVues.size() - 1);
        this.interactionService.reinitialiser();
    }

    // =========================
    // Rognage
    // =========================

    public void rognerVueCourante(VueRogneeDTO zone) {
        if (this.indexVueCourante < 0 || zone == null) return;

        Vue vue = this.projetVues.get(this.indexVueCourante);
        BufferedImage imageOrigine = vue.getImage();
        if (imageOrigine == null) return;

        int x = Math.max(0, zone.getPositionX());
        int y = Math.max(0, zone.getPositionY());
        int w = Math.min(zone.getLargeur(), imageOrigine.getWidth() - x);
        int h = Math.min(zone.getHauteur(), imageOrigine.getHeight() - y);

        if (w > 0 && h > 0) {
            this.enregistrerEtatAvantModification();
            vue.setImage(imageOrigine.getSubimage(x, y, w, h));
        }
    }

    // =========================
    // Import (PDF, PNG, Projet)
    // =========================

    /**
     * Importe un fichier sélectionné (PDF, PNG, ou projet BâtiBloc).
     * 
     * @param fichierSelectionne Le fichier à importer (pas de dialogue)
     */
    public void importerFichier(File fichierSelectionne) {
        if (fichierSelectionne == null || !fichierSelectionne.exists()) {
            return;
        }

        try {
            FichierImporteDTO fichier = ImportFichierService.chargerFichier(fichierSelectionne);
            if (fichier == null) return;


            this.enregistrerEtatAvantModification();
            if (fichier.isPDF()) {
                importerPdf((PDDocument) fichier.getContent());
            } else if (fichier.isPNG()) {
                importerPng((BufferedImage) fichier.getContent());
            } else if (fichier.isProjet()) {
                chargerProjet((File) fichier.getContent());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'import du fichier: " + e.getMessage());
            e.printStackTrace();

        }
    }

    private void importerPdf(PDDocument documentPdf) {
        this.projetVues.clear();
        this.indexVueCourante = -1;
        this.interactionService.reinitialiser();

        try {
            PDFRenderer renderer = new PDFRenderer(documentPdf);
            for (int i = 0; i < documentPdf.getNumberOfPages(); i++) {
                BufferedImage img = renderer.renderImageWithDPI(i, DPI_RENDU, ImageType.RGB);
                Vue vue = new Vue(img);
                vue.setEchelle(new Echelle(ECHELLE_DEFAUT));
                this.projetVues.add(vue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { documentPdf.close(); } catch (IOException e) { e.printStackTrace(); }
        }

        if (!this.projetVues.isEmpty()) this.indexVueCourante = 0;
    }

    private void importerPng(BufferedImage image) {
        this.projetVues.clear();
        this.indexVueCourante = -1;
        this.interactionService.reinitialiser();

        Vue vue = new Vue(image);
        vue.setEchelle(new Echelle(ECHELLE_DEFAUT));
        this.projetVues.add(vue);
        this.indexVueCourante = 0;
    }

    // =========================
    // Export (PDF, PNG, Projet)
    // =========================

    public void exporterFichier(List<Integer> indices, TypeExport type) {
        if (indices == null || indices.isEmpty()) return;

        File fichierDestination = FileDialogService.afficherDialogueSauvegarde(type);
        if (fichierDestination == null) return;

        switch (type) {
            case PDF    -> exporterPdf(indices, fichierDestination);
            case PNG    -> exporterPng(indices, fichierDestination);
            case PROJET -> exporterProjet(indices, fichierDestination);
        }
    }

     /**
     * Exporte les vues sélectionnées en PDF.
     * 
     * @param indices Indices des vues à exporter
     * @param fichierSortie Fichier de destination (pas de dialogue)
     */
    public void exporterPdf(List<Integer> indices, File fichierSortie) {
        if (fichierSortie == null || indices == null || indices.isEmpty()) {
            return;
        }

        // Valider et ajouter l'extension si nécessaire
        fichierSortie = ExportFichierService.validerFichierExport(fichierSortie, ExportFichierService.TypeExport.PDF);


        try (PDDocument document = new PDDocument()) {
            for (int index : indices) {
                if (index < 0 || index >= this.projetVues.size()) continue;

                Vue vue = this.projetVues.get(index);
                synchroniserArmaturesAutomatiques(vue);

                // Délégation au service de rendu
                BufferedImage image = renduService.genererImageAvecZones(vue);
                PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
                document.addPage(page);

                PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    cs.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
                }
            }
            document.save(fichierSortie);
            System.out.println("PDF exporté: " + fichierSortie.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erreur lors de l'export PDF: " + e.getMessage());

            e.printStackTrace();
        }
    }

     /**
     * Exporte les vues sélectionnées en PNG.
     * 
     * @param indices Indices des vues à exporter
     * @param fichierOuDossier Fichier ou dossier de destination (pas de dialogue)
     */
    public void exporterPng(List<Integer> indices, File fichierOuDossier) {
        if (fichierOuDossier == null || indices == null || indices.isEmpty()) {
            return;
        }

        // Valider et ajouter l'extension si nécessaire
        File dossier = ExportFichierService.validerFichierExport(fichierOuDossier, ExportFichierService.TypeExport.PNG);


        if (indices.size() == 1) {
            try {
                Vue vue = this.projetVues.get(indices.get(0));
                synchroniserArmaturesAutomatiques(vue);
                BufferedImage image = renduService.genererImageAvecZones(vue);
                ImageIO.write(image, "png", dossier);
                System.out.println("PNG exporté: " + dossier.getAbsolutePath());

            } catch (IOException e) {
                System.err.println("Erreur lors de l'export PNG: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            String baseName = dossier.getAbsolutePath();
            if (baseName.endsWith(".png")) {
                baseName = baseName.substring(0, baseName.length() - 4);
            }
            File dir = new File(baseName);
            if (!dir.exists()) dir.mkdirs();

            for (int index : indices) {
                if (index < 0 || index >= this.projetVues.size()) continue;
                try {
                    Vue vue = this.projetVues.get(index);
                    synchroniserArmaturesAutomatiques(vue);
                    BufferedImage image = renduService.genererImageAvecZones(vue);
                    File fichier = new File(dir, "vue_" + (index + 1) + ".png");
                    ImageIO.write(image, "png", fichier);
                    System.out.println("PNG exporté: " + fichier.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'export PNG: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Exporte le projet en fichier .batibloc.
     * 
     * @param indices Non utilisé (le projet entier est exporté)
     * @param fichierSortie Fichier de destination (pas de dialogue)
     */
    public void exporterProjet(List<Integer> indices, File fichierSortie) {
        if (fichierSortie == null) {
            return;
        }

        // Valider et ajouter l'extension si nécessaire
        fichierSortie = ExportFichierService.validerFichierExport(fichierSortie, ExportFichierService.TypeExport.PROJET);
        sauvegarderProjet(fichierSortie);
    }

    // =========================
    // Sauvegarde / Chargement de projet
    // =========================

    private void sauvegarderProjet(File fichier) {
        try {
            ProjetDTO projetDTO = construireProjetDTO();
            ProjetPersistenceService.sauvegarder(projetDTO, fichier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargerProjet(File fichier) {
        try {
            ProjetDTO projetDTO = ProjetPersistenceService.charger(fichier);
            restaurerDepuisProjetDTO(projetDTO);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ProjetDTO construireProjetDTO() {
        List<VueProjetDTO> vuesDTO = new ArrayList<>();

        for (Vue vue : this.projetVues) {
            try {
                byte[] imageData = ProjetPersistenceService.imageVersBytes(vue.getImage());
                synchroniserArmaturesAutomatiques(vue);

                List<ZoneProjetDTO> zonesDTO = new ArrayList<>();
                for (Zone zone : vue.getZonesSansArmaturesAutomatiques()) {
                    List<PointProjetDTO> pointsDTO = zone.getPoints().stream()
                            .map(p -> new PointProjetDTO(p.x, p.y)).toList();

                    zonesDTO.add(new ZoneProjetDTO(
                            this.hauteurBlocDefaut, this.largeurBlocDefaut, 0.0,
                            pointsDTO, mappingService.typeForme(zone.getForme()), mappingService.typeZone(zone)
                    ));
                }

                vuesDTO.add(new VueProjetDTO(imageData, vue.getEchelle().getPouces(), vue.getZoom(), zonesDTO));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ProjetDTO(this.hauteurBlocDefaut, this.largeurBlocDefaut, this.indexVueCourante, vuesDTO);
    }

    private void restaurerDepuisProjetDTO(ProjetDTO projet) {
        this.enregistrerEtatAvantModification();

        this.projetVues.clear();
        this.hauteurBlocDefaut = projet.hauteurBlocDefaut();
        this.largeurBlocDefaut = projet.largeurBlocDefaut();
        this.indexVueCourante = projet.indexVueCourante();

        for (VueProjetDTO vueDTO : projet.vues()) {
            try {
                BufferedImage img = ProjetPersistenceService.bytesVersImage(vueDTO.imagePng());
                Vue vue = new Vue(img);
                vue.setEchelle(new Echelle(vueDTO.echelle()));
                vue.setZoom(vueDTO.zoom());

                for (ZoneProjetDTO zoneDTO : vueDTO.zones()) {
                    List<Point2D.Double> points = zoneDTO.points().stream()
                            .map(p -> new Point2D.Double(p.positionX(), p.positionY())).toList();

                    Forme forme = mappingService.creerForme(zoneDTO.forme(), points);
                    Zone zone = mappingService.creerZone(zoneDTO.type(), forme, points);
                    if (zone != null) vue.ajouterZone(zone);
                }
                this.projetVues.add(vue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.interactionService.reinitialiser();
        synchroniserArmaturesAutomatiquesToutesLesVues();
    }

    // =========================
    // Calibration échelle
    // =========================

    public void calibrerEchelle(double distancePixels, int pieds, double pouces) {
        if (this.indexVueCourante < 0 || distancePixels <= 0) return;

        double totalPouces = (pieds * 12.0) + pouces;
        if (totalPouces <= 0) return;

        this.enregistrerEtatAvantModification();
        this.projetVues.get(this.indexVueCourante).setEchelle(new Echelle(totalPouces / distancePixels));
    }

    // =========================
    // Gestion des zones
    // =========================

    public void ajouterZone(TypeZoneDTO type, TypeFormeDTO forme, List<Point> points) {
        if (this.indexVueCourante < 0 || type == null || forme == null || points == null || points.isEmpty()) return;
        if (type == TypeZoneDTO.OUVERTURE && forme != TypeFormeDTO.RECTANGLE) return;

        List<Point2D.Double> pts = convertirVersPoints2D(points);
        
        // Délégation au service de mapping
        Forme formeModele = mappingService.creerForme(forme, pts);
        if (formeModele == null) return;

        Zone zone = mappingService.creerZone(type, formeModele, pts);
        if (zone == null) return;

        this.enregistrerEtatAvantModification();

        Vue vue = this.projetVues.get(this.indexVueCourante);
        vue.ajouterZone(zone);
        synchroniserArmaturesAutomatiques(vue);
    }

    public void supprimerZone(int indexZone) {
        if (this.indexVueCourante < 0) return;

        Vue vue = this.projetVues.get(this.indexVueCourante);
        List<Zone> zones = vue.getZones();
        if (indexZone < 0 || indexZone >= zones.size()) return;

        this.enregistrerEtatAvantModification();
        Zone aSupprimer = zones.get(indexZone);
        vue.supprimerZone(aSupprimer);
        synchroniserArmaturesAutomatiques(vue);

        if (this.interactionService.getZoneSelectionnee() == aSupprimer) {
            this.interactionService.reinitialiser();
        }
    }

    public void selectionnerZone(Point point) {
        if (this.indexVueCourante < 0 || point == null) return;

        Vue vue = this.projetVues.get(this.indexVueCourante);
        Zone selectionnee = this.interactionService.selectionner(vue.getZones(), new Point2D.Double(point.x, point.y));
        vue.definirZoneSelectionnee(selectionnee);
    }

    public void deplacerZoneSelectionnee(int dx, int dy) {
        if (this.indexVueCourante < 0 || (dx == 0 && dy == 0)) return;
        if (this.interactionService.getZoneSelectionnee() == null) return;

        this.enregistrerEtatAvantModification();
        this.interactionService.deplacer(dx, dy);
        synchroniserArmaturesAutomatiques(this.projetVues.get(this.indexVueCourante));
    }

    public void redimensionnerZoneSelectionnee(int indexPoignee, int dx, int dy) {
        if (this.indexVueCourante < 0 || (dx == 0 && dy == 0)) return;
        if (this.interactionService.getZoneSelectionnee() == null) return;

        this.enregistrerEtatAvantModification();
        this.interactionService.redimensionner(indexPoignee, dx, dy);
        synchroniserArmaturesAutomatiques(this.projetVues.get(this.indexVueCourante));
    }

    public void redimensionnerParBoundingBox(int indexPoignee, int dx, int dy) {
        if (this.indexVueCourante < 0 || (dx == 0 && dy == 0)) return;
        if (this.interactionService.getZoneSelectionnee() == null) return;

        this.enregistrerEtatAvantModification();
        this.interactionService.redimensionnerParBoundingBox(indexPoignee, dx, dy);
        synchroniserArmaturesAutomatiques(this.projetVues.get(this.indexVueCourante));
    }

    // =========================
    // Synchronisation des ouvertures
    // =========================

    private void synchroniserArmaturesAutomatiques(Vue vue) {
        this.ouvertureService.synchroniserArmaturesAutomatiques(
                vue,
                this.hauteurBlocDefaut,
                this.largeurBlocDefaut
        );

        if (vue != null && vue.getZoneSelectionnee() == null) {
            this.interactionService.setZoneSelctionnee(null);
        }
    }

    private void synchroniserArmaturesAutomatiquesToutesLesVues() {
        for (Vue vue : this.projetVues) {
            synchroniserArmaturesAutomatiques(vue);
        }
    }

    // =========================
    // Simulation
    // =========================

    public SimulationResultatDTO simulerZoneSelectionnee() {
        return simulerZoneSelectionnee(COUT_UNITAIRE_PAR_BLOC_DEFAUT);
    }

    public SimulationResultatDTO simulerZoneSelectionnee(double prixUnitaireBloc) {
        if (this.indexVueCourante < 0) return null;

        Vue vue = this.projetVues.get(this.indexVueCourante);
        synchroniserArmaturesAutomatiques(vue);

        Zone zone = this.interactionService.getZoneSelectionnee();
        if (zone == null) return null;

        List<Zone> zonesASimuler = new ArrayList<>();
        zonesASimuler.add(zone);

        if (zone instanceof ArmatureBlocs) {
            for (Zone z : vue.getZones()) {
                if (z instanceof Ouverture) zonesASimuler.add(z);
            }
        }

        return new SimulationService().simulerToutesLesZones(zonesASimuler, 
                this.hauteurBlocDefaut, this.largeurBlocDefaut, prixUnitaireBloc);
    }

    public void modifierDimensionsBloc(double largeur, double hauteur) {
        if (largeur <= 0 || hauteur <= 0) return;

        this.enregistrerEtatAvantModification();
        this.largeurBlocDefaut = largeur;
        this.hauteurBlocDefaut = hauteur;
        synchroniserArmaturesAutomatiquesToutesLesVues();
    }

    public SimulationResultatDTO simulerVueCourante(double prixUnitaireBloc) {
        if (this.indexVueCourante < 0) return null;
        Vue vue = this.projetVues.get(this.indexVueCourante);
        synchroniserArmaturesAutomatiques(vue);
        return simulationFacade.simulerVue(vue, 
                this.hauteurBlocDefaut, this.largeurBlocDefaut, prixUnitaireBloc);
    }

    public int getNombreBlocsVueCourante(double prixUnitaireBloc) {
        SimulationResultatDTO r = simulerVueCouranteInterne(prixUnitaireBloc);
        return r == null ? -1 : r.getTotalBlocs();
    }

    public double getPrixVueCourante(double prixUnitaireBloc) {
        SimulationResultatDTO r = simulerVueCouranteInterne(prixUnitaireBloc);
        return r == null ? -1.0 : r.getPrixTotal();
    }

    private SimulationResultatDTO simulerVueCouranteInterne(double prixUnitaireBloc) {
        if (this.indexVueCourante < 0) return null;
        Vue vue = this.projetVues.get(this.indexVueCourante);
        synchroniserArmaturesAutomatiques(vue);
        return simulationFacade.simulerVue(vue, this.hauteurBlocDefaut, 
                this.largeurBlocDefaut, prixUnitaireBloc);
    }

    public int getNombreBlocsProjet(double prixUnitaireBloc) {
        return simulerProjetInterne(prixUnitaireBloc).getTotalBlocs();
    }

    public double getPrixProjet(double prixUnitaireBloc) {
        return simulerProjetInterne(prixUnitaireBloc).getPrixTotal();
    }

    private SimulationResultatDTO simulerProjetInterne(double prixUnitaireBloc) {
        synchroniserArmaturesAutomatiquesToutesLesVues();
        return simulationFacade.simulerProjet(this.projetVues, 
                this.hauteurBlocDefaut, this.largeurBlocDefaut, prixUnitaireBloc);
    }

    // =========================
    // DTO exposés à l'interface
    // =========================

    public VueDTO getVueCourante() {
        if (this.indexVueCourante < 0) return null;

        Vue vue = this.projetVues.get(this.indexVueCourante);
        synchroniserArmaturesAutomatiques(vue);

        return construireVueDTO(vue, this.indexVueCourante);
    }

    public List<VueDTO> getVues() {
        synchroniserArmaturesAutomatiquesToutesLesVues();

        List<VueDTO> dtos = new ArrayList<>();
        for (int i = 0; i < this.projetVues.size(); i++) {
            dtos.add(construireVueDTO(this.projetVues.get(i), i));
        }
        return dtos;
    }

    private List<BlocDTO> convertirBlocs(Zone zone) {
        if (!(zone instanceof ArmatureBlocs armature)) return List.of();
        return armature.getBlocsSimules().stream()
                .map(b -> new BlocDTO(b.getX(), b.getY(), b.getLargeur(), b.getHauteur(), b.getPrix()))
                .toList();
    }

    private VueDTO construireVueDTO(Vue vue, int index) {
        List<ZoneDTO> zoneDtos = new ArrayList<>();
        Zone selectionnee = vue.getZoneSelectionnee();

        for (Zone z : vue.getZones()) {
            zoneDtos.add(new ZoneDTO(z == selectionnee, convertirBlocs(z),
                    convertirVersPoints(z.getPoints()), 
                    mappingService.typeForme(z.getForme()), 
                    mappingService.typeZone(z)));
        }

        return new VueDTO(vue.getImage(), vue.getEchelle().getPouces(), index, zoneDtos);
    }

    // =========================
    // Conversions de points (utilitaires)
    // =========================

    private List<Point2D.Double> convertirVersPoints2D(List<Point> points) {
        return points.stream().map(p -> new Point2D.Double(p.x, p.y)).toList();
    }

    private List<Point> convertirVersPoints(List<Point2D.Double> points) {
        return points.stream().map(p -> new Point((int) Math.round(p.x), (int) Math.round(p.y))).toList();
    }
}
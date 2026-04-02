package BatiBloc.Vues;

import BatiBloc.Controleur.MainController;
import BatiBloc.Dto.VueDTO;
import BatiBloc.Dto.TypeZone;//Colombe
import BatiBloc.Dto.TypeForme;//Colombe
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import BatiBloc.dto.RognerVueDTO;

public class NewMainWindow extends JFrame {
    
    private IPanelAffichage drawingPanel;
    private JButton btnAnnulerModification;
    private JButton btnConfirmerModification;
    private JCheckBox btnToutSelectionner;
    private JPanel panelMiniatures;
    private JPanel panelSelectionVues;    
    private List<JCheckBox> checkboxes;
    private Locale langueCourante = Locale.FRENCH;
    private static final Logger logger = Logger.getLogger(NewMainWindow.class.getName());
    private final MainController controleur;
    private ResourceBundle bundle;
    private String panelActionCourante = null;
    public enum Langage { FR, EN }
    private boolean modeRognage = false;
    private boolean typeZoneChoisi = false;//Colombe
    private boolean formeChoisie = false;//Colombe
    private JButton btn_arreterZone = null;//Colombe

    public NewMainWindow(MainController controleur) {
        this.controleur = controleur;
        this.initComponents();
        this.initialisationFormulaire();        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void initialisationFormulaire(){
        this.configurationPanelVues();
        this.changerLangueTexte();
        this.configurerActionListener();

        this.cbo_langue.setSelectedItem(Langage.FR);

        this.btn_deplacerZone.setIcon(new FlatSVGIcon("icones/" + "move_icon.svg", 25, 25));
        this.btn_dimensionZone.setIcon(new FlatSVGIcon("icones/" + "resize_icon.svg", 25, 25));
        this.btn_supprimerZone.setIcon(new FlatSVGIcon("icones/" + "delete_icon.svg", 25, 25));
        this.btn_zoneArmatureClassique.setIcon(new FlatSVGIcon("icones/" + "classicZone_icon.svg", 25, 25));
        this.btn_zoneArmatureBloc.setIcon(new FlatSVGIcon("icones/" + "blockZone_icon.svg", 25, 25));
        this.btn_zoneOuverture.setIcon(new FlatSVGIcon("icones/" + "opening_icon.svg", 25, 25));
        this.btn_formeRectangle.setIcon(new FlatSVGIcon("icones/" + "rectangle_icon.svg", 25, 25));
        this.btn_formeTriangle.setIcon(new FlatSVGIcon("icones/" + "triangle_icon.svg", 25, 25));
        this.btn_formeTriangleTronque.setIcon(new FlatSVGIcon("icones/" + "truncatedTriangle_icon.svg", 25, 25));
        this.btn_vuePrecedente.setIcon(new FlatSVGIcon("icones/" + "backward_icon.svg", 25, 25));
        this.btn_vueSuivante.setIcon(new FlatSVGIcon("icones/" + "forward_icon.svg", 25, 25));
        this.btn_zoomIn.setIcon(new FlatSVGIcon("icones/" + "zoomIn_icon.svg", 25, 25));
        this.btn_zoomOut.setIcon(new FlatSVGIcon("icones/" + "zoomOut_icon.svg", 20, 20));
        
        this.rafraichirVueCourante();
    }
    
    private void configurationPanelVues(){
        this.drawingPanel = new DrawingPanel(this.controleur);
        this.drawingPanel.setOnRognageTermine(rect -> this.rognerVueCourante(rect)); // n
       
        
        this.drawingPanel.setOnZoneAjoutee(() -> this.rafraichirVueCourante());
//        if (this.drawingPanel instanceof DrawingPanel dp)
//        {
//            dp.setModeAjoutZone(true);
//        }
        
        this.panel_vues.setLayout(new BorderLayout());
        this.panel_vues.add((Component) this.drawingPanel, BorderLayout.CENTER);
        
        JPanel panelZoom = new JPanel();
        panelZoom.setOpaque(false);
        panelZoom.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        this.panel_vues.add(panelZoom, BorderLayout.SOUTH);
    }
    
    private void configurerActionListener() 
    {        
        this.txt_echelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double nouvelleEchelle = Double.parseDouble(txt_echelle.getText().trim());
                    if (nouvelleEchelle > 0) {
                        controleur.setEchelleCourante(nouvelleEchelle);
                        rafraichirVueCourante();
                    }
                } catch (NumberFormatException ex) {
                }
            }
        });
        
        this.btn_annulerAction.addActionListener(e -> {
            this.controleur.annulerAction();
            this.rafraichirVueCourante();
        });
        
        this.btn_retablirAction.addActionListener(e -> {
            this.controleur.retablirAction();
            this.rafraichirVueCourante();
        });
        
        this.btn_importerPdf.addActionListener(e -> {
            this.controleur.importerPdf();
            this.rafraichirVueCourante();
        });
        
        this.btn_exporterPdf.addActionListener(e -> this.afficherPanelVues("panel.export"));
        
        this.btn_ajouterVue.addActionListener(e -> {
            this.controleur.ajouterVue();
            this.rafraichirVueCourante();
        });
        
        this.btn_supprimerVue.addActionListener(e -> this.afficherPanelVues("panel.delete"));
        
        // activation du mode rognage
        this.btn_rognerVue.addActionListener(e -> {
        this.modeRognage = !this.modeRognage;
        this.drawingPanel.setModeRognage(this.modeRognage);
        this.btn_rognerVue.setText(this.modeRognage ? "Rognage actif" : this.bundle.getString("button.cropView"));
        });
        
                // bouton zoom +
        this.btn_zoomIn.addActionListener(e -> {
            this.drawingPanel.zoomInCenter();
        });
        //  bouton zoom -
        this.btn_zoomOut.addActionListener(e -> {
            this.drawingPanel.zoomOutCenter();
        });
        // bouton precendant
        this.btn_vuePrecedente.addActionListener(e -> {
            this.controleur.naviguerVuePrecedente(); 
        this.rafraichirVueCourante();
        });
        //bouton suivant
        this.btn_vueSuivante.addActionListener(e -> {
            this.controleur.naviguerVueSuivante(); 
        this.rafraichirVueCourante();
        });
        
        this.cbo_langue.setModel(new DefaultComboBoxModel<>(new String[] {Langage.FR.toString(), Langage.EN.toString()}));
        
        this.cbo_langue.addActionListener(e -> {
            String choix = (String) this.cbo_langue.getSelectedItem();
            switch(choix) {
                case "FR" -> this.langueCourante = Locale.FRENCH;
                case "EN" -> this.langueCourante = Locale.ENGLISH;
            }
            this.changerLangueTexte();
        });
        // Type de zone
        this.btn_zoneArmatureClassique.addActionListener(e -> {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setTypeZoneEnCours(TypeZone.ARMATURE_CLASSIQUE);
                this.typeZoneChoisi = true;
            }
        });

        this.btn_zoneArmatureBloc.addActionListener(e -> {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setTypeZoneEnCours(TypeZone.ARMATURE_BLOCS);
                this.typeZoneChoisi = true;
            }
        });

        this.btn_zoneOuverture.addActionListener(e -> {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setTypeZoneEnCours(TypeZone.OUVERTURE);
                this.typeZoneChoisi = true;
            }
        });

        // Forme
        this.btn_formeRectangle.addActionListener(e -> {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setFormeEnCours(TypeForme.RECTANGLE);
                this.formeChoisie = true;
                this.verifierEtActiverDessin();
            }
        });

        this.btn_formeTriangle.addActionListener(e -> {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setFormeEnCours(TypeForme.TRIANGLE);
                this.formeChoisie = true;
                this.verifierEtActiverDessin();
            }
        });

        this.btn_formeTriangleTronque.addActionListener(e -> {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setFormeEnCours(TypeForme.TRIANGLE_TRONQUE);
                this.formeChoisie = true;
                this.verifierEtActiverDessin();
            }
        });
        //Colombe
        this.btn_deplacerZone.addActionListener(e -> {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setModeDeplacementZone(true);
            }
        });
    }
    
    private void rafraichirVueCourante() {
        VueDTO vue = this.controleur.getVueCourante();
        if (this.drawingPanel != null) {
            this.drawingPanel.afficherVue(vue);
            
            // completion @roneh
            if (vue != null) {
          this.lbl_vueCourante.setText("Vue " + (vue.getIndex() + 1));
        } else {
            this.lbl_vueCourante.setText("Aucune vue");
        }
        }
    }
    
    private void changerLangueTexte() {
        this.bundle = ResourceBundle.getBundle("i18n.messages", this.langueCourante);

        this.configBouton(this.btn_annulerAction, "button.undoAction", "undo_icon.svg");
        this.configBouton(this.btn_retablirAction, "button.redoAction", "redo_icon.svg");
        this.configBouton(this.btn_importerPdf, "button.importPdf", "import_icon.svg");
        this.configBouton(this.btn_exporterPdf, "button.exportPdf", "export_icon.svg");
        this.configBouton(this.btn_ajouterVue, "button.addView", "add_icon.svg");
        this.configBouton(this.btn_supprimerVue, "button.deleteView", "delete_icon.svg");
        this.configBouton(this.btn_rognerVue, "button.cropView", "crop_icon.svg");
        
        this.btn_appliquerEchelle.setText(bundle.getString("label.applyScale"));

        this.lbl_dimensionBlocTitre.setText(bundle.getString("label.blockDimension"));
        this.lbl_largeurBloc.setText(bundle.getString("label.width"));
        this.lbl_hauteurBloc.setText(bundle.getString("label.height"));
        this.lbl_typeZoneTitre.setText(bundle.getString("label.zoneType"));
        this.lbl_formeZoneTitre.setText(bundle.getString("label.form"));
        this.lbl_echelle.setText(bundle.getString("label.scale"));
        
        this.changerLangueFenetreVues();
        if (this.panelSelectionVues != null && this.panelSelectionVues.isShowing()) {
            List<Integer> indicesCochesAvant = new ArrayList<>();
            for (int i = 0; i < this.checkboxes.size(); i++) {
                if (this.checkboxes.get(i).isSelected()) {
                    indicesCochesAvant.add(i);
                }
            }

            this.afficherPanelVues(this.panelActionCourante);

            for (int index : indicesCochesAvant) {
                if (index < this.checkboxes.size()) {
                    this.checkboxes.get(index).setSelected(true);
                }
            }
        }
        
        this.revalidate();
        this.repaint();
    }
    
    private void configBouton(JButton bouton, String nomBouton, String nomIcone){
        bouton.setText(bundle.getString(nomBouton));
        bouton.setIcon(new FlatSVGIcon("icones/" + nomIcone, 25, 25));
        bouton.setIconTextGap(10);
    }
    
    private void afficherPanelVues(String actionKey) {
        this.panelActionCourante = actionKey;
        
        if (this.panelSelectionVues != null) {
            this.panel_principal.remove(this.panelSelectionVues);
            this.panelSelectionVues = null;
        }

        List<VueDTO> vues = this.controleur.getVues();
        if (vues.isEmpty()) return;
        
        String action = this.bundle.getString(this.panelActionCourante);
        String titre = MessageFormat.format(bundle.getString("panel.selectViews"), action);

        this.panelSelectionVues = new JPanel();
        this.panelSelectionVues.setLayout(new BorderLayout());
        this.panelSelectionVues.setBorder(BorderFactory.createTitledBorder(titre));
        this.panelSelectionVues.setBackground(new Color(240, 240, 240));

        this.panelMiniatures = new JPanel();
        this.panelMiniatures.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.panelMiniatures.setBackground(new Color(240, 240, 240));

        this.checkboxes = new ArrayList<>();
        JPanel panelVue;
        JCheckBox cb;
        BufferedImage img;
        BufferedImage miniature;
        JLabel lblMiniature;
        
        for (int i = 0; i < vues.size(); i++) {
            panelVue = new JPanel();
            panelVue.setLayout(new BorderLayout());
            panelVue.setBackground(new Color(240, 240, 240));

            img = vues.get(i).getImage();
            miniature = this.creerMiniature(img, 100, 80);
            
            lblMiniature = new JLabel(new ImageIcon(miniature));
            
            lblMiniature.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            cb = new JCheckBox(vues.get(i).getNomVue(this.langueCourante));
            cb.addActionListener(e -> this.majBoutonToutSelectionnerVues());
            cb.setBackground(new Color(240, 240, 240));
            this.checkboxes.add(cb);
            
            int indexFinal = i;
            lblMiniature.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JCheckBox check = checkboxes.get(indexFinal);
                    check.setSelected(!check.isSelected());
                    majBoutonToutSelectionnerVues();
                }
            });

            panelVue.add(lblMiniature, BorderLayout.CENTER);
            panelVue.add(cb, BorderLayout.SOUTH);
            this.panelMiniatures.add(panelVue);
        }

        JPanel panelBoutons = new JPanel();
        this.btnToutSelectionner = new JCheckBox(this.bundle.getString("panel.selectAll"));
        this.btnConfirmerModification = new JButton(this.bundle.getString("panel.confirm"));
        this.btnAnnulerModification = new JButton(this.bundle.getString("panel.cancel"));

        this.btnToutSelectionner.addActionListener(e -> this.onToggleSelectionnerVues());
        this.btnConfirmerModification.addActionListener(e -> this.confirmerSupressionOuExportVues());
        this.btnAnnulerModification.addActionListener(e -> this.annulerSupressionOuExportVues());

        panelBoutons.add(this.btnToutSelectionner);
        panelBoutons.add(this.btnConfirmerModification);
        panelBoutons.add(this.btnAnnulerModification);

        this.panelSelectionVues.add(new JScrollPane(this.panelMiniatures), BorderLayout.CENTER);
        this.panelSelectionVues.add(panelBoutons, BorderLayout.SOUTH);
        this.panelSelectionVues.setPreferredSize(new Dimension(getWidth(), 180));

        this.panel_principal.add(this.panelSelectionVues, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
    
    private BufferedImage creerMiniature(BufferedImage imageOriginal, int largeur, int hauteur) {
        BufferedImage miniature = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = miniature.createGraphics();
        g.drawImage(imageOriginal, 0, 0, largeur, hauteur, null);
        g.dispose();
        return miniature;
    }
    
    private void majBoutonToutSelectionnerVues() {
        boolean tousCoches = checkboxes.stream().allMatch(JCheckBox::isSelected);
        this.btnToutSelectionner.setSelected(tousCoches);
    }

    private void onToggleSelectionnerVues(){
        boolean etat = this.btnToutSelectionner.isSelected();

        for (JCheckBox cb : checkboxes) {
            cb.setSelected(etat);
        }
    }

    private void confirmerSupressionOuExportVues() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).isSelected()) {
                indices.add(i);
            }
        }
        if (!indices.isEmpty() && this.panelActionCourante.equalsIgnoreCase("panel.delete")) {
            this.controleur.supprimerVues(indices);
        }
        else if (!indices.isEmpty() && this.panelActionCourante.equalsIgnoreCase("panel.export")){
            this.controleur.exporterVues(indices);
        }
        
        this.annulerSupressionOuExportVues();
        this.rafraichirVueCourante();
    }

    private void annulerSupressionOuExportVues() {
        this.panel_principal.remove(this.panelSelectionVues);
        revalidate();
        repaint();
    }
    
    private void changerLangueFenetreVues() {
        if (this.panelSelectionVues != null && this.panelSelectionVues.isShowing()) {
            String titre = MessageFormat.format(bundle.getString("panel.selectViews"),this.panelActionCourante);
            
            if (this.panelSelectionVues.getBorder() instanceof TitledBorder border) {
                border.setTitle(titre);
            }
            
            this.panelSelectionVues.repaint();
            
            this.btnToutSelectionner.setText(bundle.getString("panel.selectAll"));
            this.btnConfirmerModification.setText(bundle.getString("panel.confirm"));
            this.btnAnnulerModification.setText(bundle.getString("panel.cancel"));
        }
        this.changerLangueTitreFenetreVues();
    }
    
    private void changerLangueTitreFenetreVues() {
        if (this.panelSelectionVues == null || !this.panelSelectionVues.isShowing() || this.panelActionCourante == null) {
            return;
        }
        
        String titre = MessageFormat.format(bundle.getString("panel.selectViews"),this.panelActionCourante);
        
        if (this.panelSelectionVues.getBorder() instanceof TitledBorder tb) {
            tb.setTitle(titre);
            this.panelSelectionVues.repaint();
        }
    }
    
    // methode du rognage
    private void rognerVueCourante(Rectangle zoneAffichage) {
    if (zoneAffichage == null || zoneAffichage.width <= 0 || zoneAffichage.height <= 0) {
        return;
    }

    if (!(this.drawingPanel instanceof DrawingPanel panel)) {
        return;
    }

    Rectangle zoneImage = panel.convertirSelectionEcranVersImage(zoneAffichage);
    if (zoneImage == null) {
        return;
    }

    this.controleur.rognerVueCourante(
        new RognerVueDTO(zoneImage.x, zoneImage.y, zoneImage.width, zoneImage.height)
    );

    this.modeRognage = false;
    this.drawingPanel.setModeRognage(false);
    this.btn_rognerVue.setText(this.bundle.getString("button.cropView"));

    this.rafraichirVueCourante();
    }
    
    private void verifierEtActiverDessin() {
        if (this.typeZoneChoisi && this.formeChoisie) {
            if (this.drawingPanel instanceof DrawingPanel dp) {
                dp.setModeAjoutZone(true);
            }
            this.afficherBoutonArreter();
        }
    }

    private void afficherBoutonArreter() {
        if (this.btn_arreterZone == null) {
            this.btn_arreterZone = new JButton("Dessin actif");
            this.btn_arreterZone.setMaximumSize(new java.awt.Dimension(150, 30));
            this.btn_arreterZone.setMinimumSize(new java.awt.Dimension(150, 30));
            this.btn_arreterZone.setPreferredSize(new java.awt.Dimension(150, 30));
            this.btn_arreterZone.addActionListener(e -> this.arreterModeZone());
            this.panel_menu.add(this.btn_arreterZone);
            this.panel_menu.revalidate();
            this.panel_menu.repaint();
        }
    }

    private void arreterModeZone() {
        if (this.drawingPanel instanceof DrawingPanel dp) {
            dp.setModeAjoutZone(false);
        }
        this.typeZoneChoisi = false;
        this.formeChoisie = false;
        if (this.btn_arreterZone != null) {
            this.panel_menu.remove(this.btn_arreterZone);
            this.btn_arreterZone = null;
            this.panel_menu.revalidate();
            this.panel_menu.repaint();
                    }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_boutons = new javax.swing.JPanel();
        btn_annulerAction = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_retablirAction = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_importerPdf = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_exporterPdf = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_ajouterVue = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_supprimerVue = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_rognerVue = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        cbo_langue = new javax.swing.JComboBox<>();
        panel_menu = new javax.swing.JPanel();
        lbl_dimensionBlocTitre = new javax.swing.JLabel();
        panel_dimensionsBloc = new javax.swing.JPanel();
        lbl_largeurBloc = new javax.swing.JLabel();
        txt_largeurBloc = new javax.swing.JTextField();
        lbl_hauteurBloc = new javax.swing.JLabel();
        txt_hauteurBloc = new javax.swing.JTextField();
        lbl_actionTitre = new javax.swing.JLabel();
        panel_boutonAction = new javax.swing.JPanel();
        btn_deplacerZone = new javax.swing.JButton();
        btn_dimensionZone = new javax.swing.JButton();
        btn_supprimerZone = new javax.swing.JButton();
        lbl_typeZoneTitre = new javax.swing.JLabel();
        panel_typeZone = new javax.swing.JPanel();
        btn_zoneArmatureClassique = new javax.swing.JButton();
        btn_zoneArmatureBloc = new javax.swing.JButton();
        btn_zoneOuverture = new javax.swing.JButton();
        lbl_formeZoneTitre = new javax.swing.JLabel();
        panel_formeZone = new javax.swing.JPanel();
        btn_formeRectangle = new javax.swing.JButton();
        btn_formeTriangle = new javax.swing.JButton();
        btn_formeTriangleTronque = new javax.swing.JButton();
        panel_principal = new javax.swing.JPanel();
        panel_echelle = new javax.swing.JPanel();
        btn_vuePrecedente = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_vueSuivante = new javax.swing.JButton();
        lbl_vueCourante = new javax.swing.JLabel();
        btn_appliquerEchelle = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        lbl_echelle = new javax.swing.JLabel();
        txt_echelle = new javax.swing.JTextField();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        btn_zoomIn = new javax.swing.JButton();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_zoomOut = new javax.swing.JButton();
        panel_vues = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panel_boutons.setMaximumSize(new java.awt.Dimension(33897, 40));
        panel_boutons.setMinimumSize(new java.awt.Dimension(1130, 40));
        panel_boutons.setPreferredSize(new java.awt.Dimension(412, 40));
        panel_boutons.setLayout(new javax.swing.BoxLayout(panel_boutons, javax.swing.BoxLayout.X_AXIS));

        btn_annulerAction.setIconTextGap(15);
        btn_annulerAction.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_annulerAction.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_annulerAction.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_boutons.add(btn_annulerAction);
        panel_boutons.add(filler1);

        btn_retablirAction.setIconTextGap(15);
        btn_retablirAction.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_retablirAction.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_retablirAction.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_boutons.add(btn_retablirAction);
        panel_boutons.add(filler2);

        btn_importerPdf.setIconTextGap(15);
        btn_importerPdf.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_importerPdf.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_importerPdf.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_boutons.add(btn_importerPdf);
        panel_boutons.add(filler3);

        btn_exporterPdf.setIconTextGap(15);
        btn_exporterPdf.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_exporterPdf.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_exporterPdf.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_boutons.add(btn_exporterPdf);
        panel_boutons.add(filler4);

        btn_ajouterVue.setIconTextGap(15);
        btn_ajouterVue.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_ajouterVue.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_ajouterVue.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_boutons.add(btn_ajouterVue);
        panel_boutons.add(filler5);

        btn_supprimerVue.setIconTextGap(15);
        btn_supprimerVue.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_supprimerVue.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_supprimerVue.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_boutons.add(btn_supprimerVue);
        panel_boutons.add(filler6);

        btn_rognerVue.setIconTextGap(15);
        btn_rognerVue.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_rognerVue.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_rognerVue.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_boutons.add(btn_rognerVue);
        panel_boutons.add(filler7);

        cbo_langue.setMaximumSize(new java.awt.Dimension(50, 30));
        cbo_langue.setMinimumSize(new java.awt.Dimension(50, 30));
        cbo_langue.setPreferredSize(new java.awt.Dimension(50, 30));
        panel_boutons.add(cbo_langue);

        getContentPane().add(panel_boutons, java.awt.BorderLayout.PAGE_START);

        panel_menu.setLayout(new javax.swing.BoxLayout(panel_menu, javax.swing.BoxLayout.Y_AXIS));

        lbl_dimensionBlocTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_dimensionBlocTitre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_dimensionBlocTitre.setText("Dimensions des blocs");
        lbl_dimensionBlocTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_dimensionBlocTitre.setMaximumSize(new java.awt.Dimension(140, 30));
        lbl_dimensionBlocTitre.setMinimumSize(new java.awt.Dimension(140, 30));
        lbl_dimensionBlocTitre.setPreferredSize(new java.awt.Dimension(140, 30));
        panel_menu.add(lbl_dimensionBlocTitre);

        panel_dimensionsBloc.setMaximumSize(new java.awt.Dimension(275, 40));
        panel_dimensionsBloc.setMinimumSize(new java.awt.Dimension(275, 40));
        panel_dimensionsBloc.setPreferredSize(new java.awt.Dimension(275, 40));
        panel_dimensionsBloc.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lbl_largeurBloc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_largeurBloc.setPreferredSize(new java.awt.Dimension(65, 30));
        panel_dimensionsBloc.add(lbl_largeurBloc);

        txt_largeurBloc.setMaximumSize(new java.awt.Dimension(60, 30));
        txt_largeurBloc.setMinimumSize(new java.awt.Dimension(60, 30));
        txt_largeurBloc.setPreferredSize(new java.awt.Dimension(60, 30));
        panel_dimensionsBloc.add(txt_largeurBloc);

        lbl_hauteurBloc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_hauteurBloc.setMaximumSize(new java.awt.Dimension(75, 25));
        lbl_hauteurBloc.setMinimumSize(new java.awt.Dimension(75, 25));
        lbl_hauteurBloc.setPreferredSize(new java.awt.Dimension(65, 30));
        panel_dimensionsBloc.add(lbl_hauteurBloc);

        txt_hauteurBloc.setMaximumSize(new java.awt.Dimension(60, 30));
        txt_hauteurBloc.setMinimumSize(new java.awt.Dimension(60, 30));
        txt_hauteurBloc.setPreferredSize(new java.awt.Dimension(60, 30));
        panel_dimensionsBloc.add(txt_hauteurBloc);

        panel_menu.add(panel_dimensionsBloc);

        lbl_actionTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_actionTitre.setText("Actions");
        lbl_actionTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_actionTitre.setMaximumSize(new java.awt.Dimension(49, 30));
        lbl_actionTitre.setMinimumSize(new java.awt.Dimension(49, 30));
        lbl_actionTitre.setPreferredSize(new java.awt.Dimension(49, 30));
        panel_menu.add(lbl_actionTitre);

        panel_boutonAction.setMaximumSize(new java.awt.Dimension(275, 40));
        panel_boutonAction.setMinimumSize(new java.awt.Dimension(275, 40));
        panel_boutonAction.setPreferredSize(new java.awt.Dimension(275, 40));

        btn_deplacerZone.setIconTextGap(0);
        btn_deplacerZone.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_deplacerZone.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_deplacerZone.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_boutonAction.add(btn_deplacerZone);

        btn_dimensionZone.setIconTextGap(0);
        btn_dimensionZone.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_dimensionZone.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_dimensionZone.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_boutonAction.add(btn_dimensionZone);

        btn_supprimerZone.setIconTextGap(0);
        btn_supprimerZone.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_supprimerZone.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_supprimerZone.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_boutonAction.add(btn_supprimerZone);

        panel_menu.add(panel_boutonAction);

        lbl_typeZoneTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_typeZoneTitre.setText("Type de zone");
        lbl_typeZoneTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_typeZoneTitre.setMaximumSize(new java.awt.Dimension(87, 30));
        lbl_typeZoneTitre.setMinimumSize(new java.awt.Dimension(87, 30));
        lbl_typeZoneTitre.setPreferredSize(new java.awt.Dimension(87, 30));
        panel_menu.add(lbl_typeZoneTitre);

        panel_typeZone.setMaximumSize(new java.awt.Dimension(275, 40));
        panel_typeZone.setMinimumSize(new java.awt.Dimension(275, 40));
        panel_typeZone.setPreferredSize(new java.awt.Dimension(275, 40));

        btn_zoneArmatureClassique.setIconTextGap(0);
        btn_zoneArmatureClassique.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_zoneArmatureClassique.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_zoneArmatureClassique.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_typeZone.add(btn_zoneArmatureClassique);

        btn_zoneArmatureBloc.setIconTextGap(0);
        btn_zoneArmatureBloc.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_zoneArmatureBloc.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_zoneArmatureBloc.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_typeZone.add(btn_zoneArmatureBloc);

        btn_zoneOuverture.setIconTextGap(0);
        btn_zoneOuverture.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_zoneOuverture.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_zoneOuverture.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_typeZone.add(btn_zoneOuverture);

        panel_menu.add(panel_typeZone);

        lbl_formeZoneTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_formeZoneTitre.setText("Formes");
        lbl_formeZoneTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_formeZoneTitre.setMaximumSize(new java.awt.Dimension(47, 30));
        lbl_formeZoneTitre.setMinimumSize(new java.awt.Dimension(47, 30));
        lbl_formeZoneTitre.setPreferredSize(new java.awt.Dimension(47, 30));
        panel_menu.add(lbl_formeZoneTitre);

        panel_formeZone.setMaximumSize(new java.awt.Dimension(275, 40));
        panel_formeZone.setMinimumSize(new java.awt.Dimension(275, 40));
        panel_formeZone.setPreferredSize(new java.awt.Dimension(275, 40));

        btn_formeRectangle.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_formeRectangle.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_formeRectangle.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_formeZone.add(btn_formeRectangle);

        btn_formeTriangle.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_formeTriangle.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_formeTriangle.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_formeZone.add(btn_formeTriangle);

        btn_formeTriangleTronque.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_formeTriangleTronque.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_formeTriangleTronque.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_formeZone.add(btn_formeTriangleTronque);

        panel_menu.add(panel_formeZone);

        getContentPane().add(panel_menu, java.awt.BorderLayout.LINE_START);

        panel_principal.setMaximumSize(new java.awt.Dimension(32767, 35));
        panel_principal.setMinimumSize(new java.awt.Dimension(85, 35));
        panel_principal.setPreferredSize(new java.awt.Dimension(651, 35));
        panel_principal.setLayout(new javax.swing.BoxLayout(panel_principal, javax.swing.BoxLayout.Y_AXIS));

        panel_echelle.setMaximumSize(new java.awt.Dimension(33442, 40));
        panel_echelle.setMinimumSize(new java.awt.Dimension(675, 40));
        panel_echelle.setPreferredSize(new java.awt.Dimension(853, 40));
        panel_echelle.setLayout(new javax.swing.BoxLayout(panel_echelle, javax.swing.BoxLayout.LINE_AXIS));

        btn_vuePrecedente.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_vuePrecedente.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_vuePrecedente.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(btn_vuePrecedente);
        panel_echelle.add(filler8);

        btn_vueSuivante.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_vueSuivante.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_vueSuivante.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(btn_vueSuivante);

        lbl_vueCourante.setMaximumSize(new java.awt.Dimension(75, 30));
        lbl_vueCourante.setMinimumSize(new java.awt.Dimension(75, 30));
        lbl_vueCourante.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(lbl_vueCourante);

        btn_appliquerEchelle.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_appliquerEchelle.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_appliquerEchelle.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_echelle.add(btn_appliquerEchelle);
        panel_echelle.add(filler9);

        lbl_echelle.setMaximumSize(new java.awt.Dimension(75, 30));
        lbl_echelle.setMinimumSize(new java.awt.Dimension(75, 30));
        lbl_echelle.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(lbl_echelle);

        txt_echelle.setMaximumSize(new java.awt.Dimension(75, 30));
        txt_echelle.setMinimumSize(new java.awt.Dimension(75, 30));
        txt_echelle.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(txt_echelle);
        panel_echelle.add(filler10);

        btn_zoomIn.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_zoomIn.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_zoomIn.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(btn_zoomIn);
        panel_echelle.add(filler11);

        btn_zoomOut.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_zoomOut.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_zoomOut.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(btn_zoomOut);

        panel_principal.add(panel_echelle);

        javax.swing.GroupLayout panel_vuesLayout = new javax.swing.GroupLayout(panel_vues);
        panel_vues.setLayout(panel_vuesLayout);
        panel_vuesLayout.setHorizontalGroup(
            panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 853, Short.MAX_VALUE)
        );
        panel_vuesLayout.setVerticalGroup(
            panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        panel_principal.add(panel_vues);

        getContentPane().add(panel_principal, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

public static void main(String args[]) { 
    FlatDarkLaf.setup();

    UIManager.put("Button.arc", 8);
    UIManager.put("Component.arc", 8);
    UIManager.put("TextComponent.arc", 10);

    UIManager.put("Button.hoverBackground", new Color(60,63,65));
    UIManager.put("Button.pressedBackground", new Color(50,50,50));

    EventQueue.invokeLater(() -> {
        new NewMainWindow(new MainController()).setVisible(true);
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_ajouterVue;
    private javax.swing.JButton btn_annulerAction;
    private javax.swing.JButton btn_appliquerEchelle;
    private javax.swing.JButton btn_deplacerZone;
    private javax.swing.JButton btn_dimensionZone;
    private javax.swing.JButton btn_exporterPdf;
    private javax.swing.JButton btn_formeRectangle;
    private javax.swing.JButton btn_formeTriangle;
    private javax.swing.JButton btn_formeTriangleTronque;
    private javax.swing.JButton btn_importerPdf;
    private javax.swing.JButton btn_retablirAction;
    private javax.swing.JButton btn_rognerVue;
    private javax.swing.JButton btn_supprimerVue;
    private javax.swing.JButton btn_supprimerZone;
    private javax.swing.JButton btn_vuePrecedente;
    private javax.swing.JButton btn_vueSuivante;
    private javax.swing.JButton btn_zoneArmatureBloc;
    private javax.swing.JButton btn_zoneArmatureClassique;
    private javax.swing.JButton btn_zoneOuverture;
    private javax.swing.JButton btn_zoomIn;
    private javax.swing.JButton btn_zoomOut;
    private javax.swing.JComboBox<String> cbo_langue;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel lbl_actionTitre;
    private javax.swing.JLabel lbl_dimensionBlocTitre;
    private javax.swing.JLabel lbl_echelle;
    private javax.swing.JLabel lbl_formeZoneTitre;
    private javax.swing.JLabel lbl_hauteurBloc;
    private javax.swing.JLabel lbl_largeurBloc;
    private javax.swing.JLabel lbl_typeZoneTitre;
    private javax.swing.JLabel lbl_vueCourante;
    private javax.swing.JPanel panel_boutonAction;
    private javax.swing.JPanel panel_boutons;
    private javax.swing.JPanel panel_dimensionsBloc;
    private javax.swing.JPanel panel_echelle;
    private javax.swing.JPanel panel_formeZone;
    private javax.swing.JPanel panel_menu;
    private javax.swing.JPanel panel_principal;
    private javax.swing.JPanel panel_typeZone;
    private javax.swing.JPanel panel_vues;
    private javax.swing.JTextField txt_echelle;
    private javax.swing.JTextField txt_hauteurBloc;
    private javax.swing.JTextField txt_largeurBloc;
    // End of variables declaration//GEN-END:variables
}

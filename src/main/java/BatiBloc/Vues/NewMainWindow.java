package BatiBloc.Vues;

import BatiBloc.Domaine.Controleur.MainController;
import BatiBloc.Domaine.Dto.VueDTO;
import BatiBloc.Domaine.Dto.TypeZone;
import BatiBloc.Domaine.Dto.TypeForme;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
import BatiBloc.Domaine.Dto.RognerVueDTO;
import java.awt.Cursor;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class NewMainWindow extends JFrame {
    
private enum ActionVue{ EXPORT, DELETE }
    private ActionVue actionVue;
    private boolean formeChoisie = false;
    private boolean modeRognage = false;
    private boolean modeSuppressionZone = false;
    private boolean typeChoisi = false;
    private DrawingPanel drawingPanel;
    private JButton btnAnnulerModif;
    private JButton btnConfirmerModif;
    private JButton btnStopZoneDrawing;
    private JCheckBox chkToutesVues;
    private JPanel panneauVues;
    private JPanel viewsThumbnailPanel;
    private Locale langueCourante = Locale.FRENCH;
    private List<JButton> boutonsVue;
    private List<JLabel> labelsVue;
    private List<JTextField> textFieldsVue;
    private List<JCheckBox> casesVues;
    private final MainController controleur;
    private ResourceBundle resourceBundle;

    public NewMainWindow(MainController controleur) {
        this.controleur = controleur;
        this.initComponents();
        this.initFormulaire();        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void initFormulaire() {
        this.initListes();
        this.initPanneauDessin();
        this.initBoutonsIcones();
        this.majInterface(); 
        this.initPlaceholders();
        this.initListeners();
        this.rafraichirUI();
    }
    
    private void initListes() {
        this.initListeBoutonsVue();
        this.initListeLabelVue();
        this.initListetextFieldVue();
    }
    
    private void initListeBoutonsVue() {
        this.boutonsVue = List.of(this.btn_exporterPdf,
            this.btn_supprimerVue,
            this.btn_rognerVue,
            this.btn_vuePrecedente,
            this.btn_vueSuivante,
            this.btn_appliquerEchelle,
            this.btn_deplacerZone,
            this.btn_redimensionnerZone,
            this.btn_supprimerZone,
            this.btn_zoneArmatureClassique,
            this.btn_zoneArmatureBloc,
            this.btn_zoneOuverture,
            this.btn_formeRectangle,
            this.btn_formeTriangle,
            this.btn_formeTriangleTronque,
            this.btn_zoomIn,
            this.btn_zoomOut
        );
    }
    
    private void initListeLabelVue() {
        this.labelsVue = List.of(
            this.lbl_largeurBloc,
            this.lbl_hauteurBloc,
            this.lbl_coutParBloc,
            this.lbl_echelle
        );
    }
    
    private void initListetextFieldVue() {
        this.textFieldsVue = List.of(
            this.txt_largeurBloc,
            this.txt_hauteurBloc,
            this.txt_coutParBloc,
            this.txt_echelle
        );
    }
    
    private void majEtatComposants() {
        boolean hasViews = !this.controleur.getVues().isEmpty();

        for(JButton btn : this.boutonsVue){
            btn.setEnabled(hasViews);
        }
        
        for(JLabel label : this.labelsVue){
            label.setEnabled(hasViews);
        }
        
        for(JTextField textField : this.textFieldsVue){
            textField.setEnabled(hasViews);
        }
    }
    
    private void initPanneauDessin() {
        this.drawingPanel = new DrawingPanel(this.controleur);
        this.drawingPanel.setOnRognageTermine(rect -> this.rognerVueCourante(rect));
        this.drawingPanel.setOnZoneAjoutee(() -> this.rafraichirUI());
        
        this.panel_vues.setLayout(new BorderLayout());
        this.panel_vues.add((Component) this.drawingPanel, BorderLayout.CENTER);
        
        JPanel panelZoom = new JPanel();
        panelZoom.setOpaque(false);
        panelZoom.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        this.panel_vues.add(panelZoom, BorderLayout.SOUTH);
    }
    
    private void rognerVueCourante(Rectangle zoneAffichage) {
        if (zoneAffichage == null || zoneAffichage.width <= 0 || zoneAffichage.height <= 0) {
            return;
        }

        Rectangle zoneImage = this.drawingPanel.convertirSelectionEcranVersImage(zoneAffichage);
        if (zoneImage == null) {
            return;
        }

        this.controleur.rognerVueCourante(
            new RognerVueDTO(zoneImage.x, zoneImage.y, zoneImage.width, zoneImage.height)
        );

        this.modeRognage = false;
        this.drawingPanel.setModeRognage(false);
        this.btn_rognerVue.setText(this.getTexte("button.cropView"));

        this.rafraichirUI();
    }
    
    private String getTexte(String key) {
        return this.resourceBundle.getString(key);
    }
    
        private void rafraichirUI() {
        this.rafraichirVue();
        this.majEtatComposants();
        this.panel_principal.revalidate();
        this.panel_principal.repaint();
    }
    
    private void rafraichirVue() {
        VueDTO vue = this.controleur.getVueCourante();
        this.drawingPanel.afficherVue(vue);
        this.lbl_vueCourante.setText(vue != null ? "Vue " + (vue.getIndex() + 1) : this.getTexte("label.noView"));
        double echelle = this.controleur.getEchelleCourante();
        if (echelle > 0) {
            this.txt_echelle.setText(formaterEchelleImperiale(echelle));
        }
    }
    
    private void initBoutonsIcones() {
        this.btn_deplacerZone.setIcon(loadIcon("move_icon.svg", 25));
        this.btn_redimensionnerZone.setIcon(loadIcon("resize_icon.svg", 25));
        this.btn_supprimerZone.setIcon(loadIcon("delete_icon.svg", 25));
        this.btn_zoneArmatureClassique.setIcon(loadIcon("classicZone_icon.svg", 25));
        this.btn_zoneArmatureBloc.setIcon(loadIcon("blockZone_icon.svg", 25));
        this.btn_zoneOuverture.setIcon(loadIcon("opening_icon.svg", 25));
        this.btn_formeRectangle.setIcon(loadIcon("rectangle_icon.svg", 25));
        this.btn_formeTriangle.setIcon(loadIcon("triangle_icon.svg", 25));
        this.btn_formeTriangleTronque.setIcon(loadIcon("truncatedTriangle_icon.svg", 25));
        this.btn_vuePrecedente.setIcon(loadIcon("backward_icon.svg", 25));
        this.btn_vueSuivante.setIcon(loadIcon("forward_icon.svg", 25));
        this.btn_zoomIn.setIcon(loadIcon("zoomIn_icon.svg", 25));
        this.btn_zoomOut.setIcon(loadIcon("zoomOut_icon.svg", 25));
    }
    
    private FlatSVGIcon loadIcon(String name,int size) {
        return new FlatSVGIcon("icones/" + name, size, size);
    }
    
    private void majInterface() {
        this.resourceBundle = ResourceBundle.getBundle("i18n.messages", this.langueCourante);
        
        this.btn_appliquerEchelle.setText(this.getTexte("button.applyScale"));
        this.btn_changerLangue.setText(this.getTexte("button.changeLanguage"));

        this.configBoutons();
        this.configLabels();
        this.configBoutonsMouseHover();
        this.majLanguePanneauVues();

        this.rafraichirUI();
    }
    
    private void configBoutons() {
        this.configBouton(this.btn_annulerAction, "button.undoAction", "undo_icon.svg");
        this.configBouton(this.btn_retablirAction, "button.redoAction", "redo_icon.svg");
        this.configBouton(this.btn_importerPdf, "button.importPdf", "import_icon.svg");
        this.configBouton(this.btn_exporterPdf, "button.exportPdf", "export_icon.svg");
        this.configBouton(this.btn_ajouterVue, "button.addView", "add_icon.svg");
        this.configBouton(this.btn_supprimerVue, "button.deleteView", "delete_icon.svg");
        this.configBouton(this.btn_rognerVue, "button.cropView", "crop_icon.svg");
    }
    
    private void configBouton(JButton bouton, String nomBouton, String nomIcone) {
        bouton.setText(this.getTexte(nomBouton));
        bouton.setIcon(new FlatSVGIcon("icones/" + nomIcone, 25, 25));
        bouton.setIconTextGap(10);
    }
    
    private void configLabels() {
        this.lbl_blocsTitre.setText(this.getTexte("label.block"));
        this.lbl_actionsTitre.setText(this.getTexte("label.action"));
        this.lbl_largeurBloc.setText(this.getTexte("label.width"));
        this.lbl_hauteurBloc.setText(this.getTexte("label.height"));
        this.lbl_coutParBloc.setText(this.getTexte("label.costPerBlock"));
        this.lbl_typeZoneTitre.setText(this.getTexte("label.zoneType"));
        this.lbl_formeZoneTitre.setText(this.getTexte("label.form"));
        this.lbl_blocsTotaux.setText(this.getTexte("label.totalBlock"));
        this.lbl_coutTotal.setText(this.getTexte("label.totalCost"));
        this.lbl_echelle.setText(this.getTexte("label.scale"));
    }
    
    private void configBoutonsMouseHover() {
        this.initMouseHover(this.btn_deplacerZone, this.getTexte("button.move"));
        this.initMouseHover(this.btn_redimensionnerZone, this.getTexte("button.resize"));
        this.initMouseHover(this.btn_supprimerZone, this.getTexte("button.deleteZone"));
        this.initMouseHover(this.btn_zoneArmatureClassique, this.getTexte("button.zoneClassicFrame"));
        this.initMouseHover(this.btn_zoneArmatureBloc, this.getTexte("button.zoneReinforcementBlocks"));
        this.initMouseHover(this.btn_zoneOuverture, this.getTexte("button.zoneOpening"));
        this.initMouseHover(this.btn_formeRectangle, this.getTexte("button.formRectangle"));
        this.initMouseHover(this.btn_formeTriangle, this.getTexte("button.formTriangle"));
        this.initMouseHover(this.btn_formeTriangleTronque, this.getTexte("button.formTruncatedTriangle"));
    }
    
    private void majLanguePanneauVues() {
        if (this.panneauVues != null && this.panneauVues.isShowing()) {            
            this.configPanneauVues();
            
            this.chkToutesVues.setText(this.getTexte("panel.selectAll"));
            this.btnConfirmerModif.setText(this.getTexte("panel.confirm"));
            this.btnAnnulerModif.setText(this.getTexte("panel.cancel"));
        }
    }
    
    private void configPanneauVues() {
        List<Integer> indicesCochesAvant = this.sauvegarderCasesCochees();

        this.afficherSelectionVues(this.actionVue);

        this.restaurerSelection(indicesCochesAvant);
    }
    
    private List<Integer> sauvegarderCasesCochees() {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < this.casesVues.size(); i++) {
            if (this.casesVues.get(i).isSelected()) {
                indices.add(i);
            }
        }
        return indices;
    }

    private void afficherSelectionVues(ActionVue action) {
        this.actionVue = action;

        this.retirerPanneauVues();

        List<VueDTO> vues = this.controleur.getVues();
        if (vues.isEmpty()) return;

        String titre = this.construireTitre();

        this.initPanneauVues(titre);
        this.initMiniatures();
        this.chargerMiniatures(vues);
        this.ajouterBoutonsActions();

        this.panel_principal.add(this.panneauVues, BorderLayout.SOUTH);
        this.rafraichirUI();
    }
    
    private void retirerPanneauVues() {
        if (this.panneauVues != null) {
            this.panel_principal.remove(this.panneauVues);
            this.panneauVues = null;
        }
    }
    
    private String construireTitre() {
        String titre = this.actionVue == ActionVue.DELETE 
                ? this.getTexte("panel.delete").toLowerCase() : this.getTexte("panel.export").toLowerCase();
        return MessageFormat.format(this.getTexte("panel.selectViews"), titre);
    }

    private void initPanneauVues(String titre) {
        this.panneauVues = new JPanel(new BorderLayout());
        this.panneauVues.setBorder(BorderFactory.createTitledBorder(titre));
        this.panneauVues.setBackground(UIManager.getColor("Panel.background"));
        this.panneauVues.setPreferredSize(new Dimension(getWidth(), 180));
    }
    
    private void initMiniatures() {
        this.viewsThumbnailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.viewsThumbnailPanel.setBackground(UIManager.getColor("Panel.background"));
        this.casesVues = new ArrayList<>();
    }

    private void chargerMiniatures(List<VueDTO> vues) {
        for (int i = 0; i < vues.size(); i++) {
            this.viewsThumbnailPanel.add(this.creerMiniature(vues.get(i)));
        }
        this.panneauVues.add(new JScrollPane(this.viewsThumbnailPanel), BorderLayout.CENTER);
    }
    
    private JPanel creerMiniature(VueDTO vue) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIManager.getColor("Panel.background"));

        BufferedImage miniature = this.createThumbnail(vue.getImage(), 100, 80);
        JLabel lblMiniature = new JLabel(new ImageIcon(miniature));
        lblMiniature.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JCheckBox cb = new JCheckBox(vue.getNomVue(this.langueCourante));
        cb.setBackground(UIManager.getColor("Panel.background"));
        cb.addActionListener(e -> this.majSelection());
        this.casesVues.add(cb);

        lblMiniature.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cb.setSelected(!cb.isSelected());
                majSelection();
            }
        });

        panel.add(lblMiniature, BorderLayout.CENTER);
        panel.add(cb, BorderLayout.SOUTH);

        return panel;
    }
    
    private BufferedImage createThumbnail(BufferedImage imageOriginal, int largeur, int hauteur) {
        BufferedImage miniature = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = miniature.createGraphics();
        g.drawImage(imageOriginal, 0, 0, largeur, hauteur, null);
        g.dispose();
        return miniature;
    }
    
    private void majSelection() {
        boolean tousCoches = this.casesVues.stream().allMatch(JCheckBox::isSelected);
        this.chkToutesVues.setSelected(tousCoches);
    }

    private void ajouterBoutonsActions() {
        JPanel panelBoutons = new JPanel();

        this.chkToutesVues = new JCheckBox(this.getTexte("panel.selectAll"));
        this.btnConfirmerModif = new JButton(this.getTexte("panel.confirm"));
        this.btnAnnulerModif = new JButton(this.getTexte("panel.cancel"));

        this.chkToutesVues.addActionListener(e -> this.selectionnerTout());
        this.btnConfirmerModif.addActionListener(e -> this.confirmerSelection());
        this.btnAnnulerModif.addActionListener(e -> this.fermerSelection());

        panelBoutons.add(this.chkToutesVues);
        panelBoutons.add(this.btnConfirmerModif);
        panelBoutons.add(this.btnAnnulerModif);

        this.panneauVues.add(panelBoutons, BorderLayout.SOUTH);
    }
    
    private void selectionnerTout() {
        boolean etat = this.chkToutesVues.isSelected();

        for (JCheckBox cb : this.casesVues) {
            cb.setSelected(etat);
        }
    }

    private void confirmerSelection() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < this.casesVues.size(); i++) {
            if (this.casesVues.get(i).isSelected()) indices.add(i);
        }

        if (!indices.isEmpty()) {
            if (ActionVue.DELETE.equals(this.actionVue)) {
                this.controleur.supprimerVues(indices);
            } else {
                this.controleur.exporterVues(indices);
            }
        }
        this.fermerSelection();
    }

    private void fermerSelection() {
        if (this.panneauVues != null) {
            this.panel_principal.remove(this.panneauVues);
            this.panneauVues = null;
        }

        this.actionVue = null;
        this.rafraichirUI();
    }
    
    private void restaurerSelection(List<Integer> indices) {
        for (int index : indices) {
            if (index < this.casesVues.size()) {
                this.casesVues.get(index).setSelected(true);
            }
        }
    }
    
    private void initPlaceholders() {
        this.txt_largeurBloc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 3'6\"1/3");
        this.txt_hauteurBloc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 1'2\"4/5");
        this.txt_coutParBloc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 5,00");
        this.txt_echelle.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 6\"");
    }
    
    private void initListeners() {
        this.initBoutonsPrincipal();
	this.initBoutonsAction();
        this.initBoutonsZone();
        this.initBoutonsForme();
        this.initBoutonsVues();
    }
    
    private void initBoutonsPrincipal() {
        this.addAction(this.btn_annulerAction, this.controleur::annulerAction);
        this.addAction(this.btn_retablirAction, this.controleur::retablirAction);
        
        this.addAction(this.btn_importerPdf, () -> {
            this.controleur.importerPdf();
            this.rafraichirUI();
        });

        this.addAction(this.btn_ajouterVue, () -> {
            this.controleur.ajouterVue();
            this.rafraichirUI();
        });

        this.btn_exporterPdf.addActionListener(e -> {
            this.actionVue = ActionVue.EXPORT;
            this.afficherSelectionVues(this.actionVue);
        });

        this.btn_supprimerVue.addActionListener(e -> {
            this.actionVue = ActionVue.DELETE;
            this.afficherSelectionVues(this.actionVue);
        });
        
        this.btn_rognerVue.addActionListener(e -> {
            this.modeRognage = !this.modeRognage;
            this.drawingPanel.setModeRognage(this.modeRognage);
            this.btn_rognerVue.setText(this.modeRognage ? this.getTexte("button.activeCropping") : this.getTexte("button.cropView"));
        });
        
        this.btn_changerLangue.addActionListener(e -> {
            this.langueCourante = (this.langueCourante.equals(Locale.FRENCH)) 
                    ? Locale.ENGLISH : Locale.FRENCH;
            this.majInterface();
        });
    }
    
    private void addAction(AbstractButton button, Runnable action){
        button.addActionListener(e -> {
            action.run();
            this.rafraichirUI();
        });
    }

    private void initBoutonsAction() {
        this.btn_deplacerZone.addActionListener(e -> {
            boolean actuel = this.drawingPanel.getModeDeplacementZone();
            this.arreterDessin();                            
            this.drawingPanel.setModeRedimensionnementZone(false); 
            this.drawingPanel.setModeDeplacementZone(!actuel);
        });

        this.btn_redimensionnerZone.addActionListener(e -> {
            boolean actuel = this.drawingPanel.getModeRedimensionnementZone();
            this.arreterDessin();                       
            this.drawingPanel.setModeDeplacementZone(false);   
            this.drawingPanel.setModeRedimensionnementZone(!actuel);
        });
        
        this.btn_supprimerZone.addActionListener(e -> {
            boolean actuel = this.modeSuppressionZone;
            this.arreterDessin();
            this.drawingPanel.setModeDeplacementZone(false);
            this.drawingPanel.setModeRedimensionnementZone(false);
            this.modeSuppressionZone = !actuel;
            this.drawingPanel.setModeSuppressionZone(this.modeSuppressionZone);
        });
    }
    
    private void initMouseHover(JButton bouton, String texte) {
        bouton.setToolTipText(texte);
    }

    private void initBoutonsZone() {
        this.addZoneBoutonListener(this.btn_zoneArmatureClassique, TypeZone.ARMATURE_CLASSIQUE);
        this.addZoneBoutonListener(this.btn_zoneArmatureBloc, TypeZone.ARMATURE_BLOCS);
        this.addZoneBoutonListener(this.btn_zoneOuverture, TypeZone.OUVERTURE);
    }
    
    private void addZoneBoutonListener(AbstractButton button, TypeZone zone) {
        button.addActionListener(e -> {
            this.drawingPanel.setModeDeplacementZone(false);       
            this.drawingPanel.setModeRedimensionnementZone(false); 
            this.modeSuppressionZone = false;                      
            this.drawingPanel.setModeSuppressionZone(false); 
            this.drawingPanel.setTypeZoneEnCours(zone);
            this.typeChoisi = true;
            this.verifierModeDessin();
        });
    }
    
    private void initBoutonsForme() {
        this.addFormeBoutonListener(this.btn_formeRectangle, TypeForme.RECTANGLE);
        this.addFormeBoutonListener(this.btn_formeTriangle, TypeForme.TRIANGLE);
        this.addFormeBoutonListener(this.btn_formeTriangleTronque, TypeForme.TRIANGLE_TRONQUE);
    }
    
    private void addFormeBoutonListener(AbstractButton button, TypeForme forme) {
        button.addActionListener(e -> {
            this.drawingPanel.setFormeEnCours(forme);
            this.formeChoisie = true;
            this.verifierModeDessin();
        });
    }
    
        private void verifierModeDessin() {        
        if (this.typeChoisi && this.formeChoisie) {
            this.drawingPanel.setModeAjoutZone(true);
            this.afficherBtnStop();
        }
    }

    private void afficherBtnStop() {
        if (this.btnStopZoneDrawing == null) {
            this.btnStopZoneDrawing = new JButton("Dessin actif");
            this.btnStopZoneDrawing.setMaximumSize(new java.awt.Dimension(150, 30));
            this.btnStopZoneDrawing.setMinimumSize(new java.awt.Dimension(150, 30));
            this.btnStopZoneDrawing.setPreferredSize(new java.awt.Dimension(150, 30));
            this.btnStopZoneDrawing.addActionListener(e -> this.arreterDessin());
            this.panel_menu.add(this.btnStopZoneDrawing);
            this.panel_menu.revalidate();
            this.panel_menu.repaint();
        }
    }

    private void arreterDessin() {
        this.drawingPanel.setModeAjoutZone(false);
        
        this.typeChoisi = false;
        this.formeChoisie = false;

        this.modeSuppressionZone = false;                        
        this.drawingPanel.setModeSuppressionZone(false); 
        
        if (this.btnStopZoneDrawing != null) {
            this.panel_menu.remove(this.btnStopZoneDrawing);
            this.btnStopZoneDrawing = null;
            this.panel_menu.revalidate();
            this.panel_menu.repaint();
        }
    }
    
    private void initBoutonsVues() {
        this.addAction(this.btn_vuePrecedente, this.controleur::naviguerVuePrecedente);
        this.addAction(this.btn_vueSuivante, this.controleur::naviguerVueSuivante);
        this.addAction(this.btn_zoomIn, this.drawingPanel::zoomInCenter);
        this.addAction(this.btn_zoomOut, this.drawingPanel::zoomOutCenter);
        
        this.btn_appliquerEchelle.addActionListener(e -> {
            try {
                String texte = this.txt_echelle.getText().trim();
                double poucesParPixel;

                if (texte.contains("'") || texte.contains("\"")) {
                    poucesParPixel = parserMesureImperiale(texte);
                } else {
                    poucesParPixel = Double.parseDouble(texte);
                }

                if (poucesParPixel <= 0) {
                    JOptionPane.showMessageDialog(this, this.getTexte("messageDialog.scaleSize"));
                    return;
                }
                this.controleur.setEchelleCourante(poucesParPixel);
                this.rafraichirUI();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, this.getTexte("messageDialog.scaleSizeException"));
            }
        });
    }
    
    private void initLabelFocus() {
        this.addTextFieldFocus(this.lbl_largeurBloc, this.txt_largeurBloc);
        this.addTextFieldFocus(this.lbl_hauteurBloc, this.txt_hauteurBloc);
        this.addTextFieldFocus(this.lbl_coutParBloc, this.txt_coutParBloc);
        this.addTextFieldFocus(this.lbl_echelle, this.txt_echelle);
    }
    
    private void addTextFieldFocus(JLabel label, JTextField field) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                field.requestFocusInWindow();
            }
        });
    }

    private double parserMesureImperiale(String texte) {
        double totalPouces = 0;

        String regex = "(?:(\\d+)')?\\s*(?:(\\d+(?:\\.\\d+)?)\")?\\s*(?:(\\d+)/(\\d+))?";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(texte.trim());

        if (m.find()) {
            if (m.group(1) != null) totalPouces += Double.parseDouble(m.group(1)) * 12;

            if (m.group(2) != null) totalPouces += Double.parseDouble(m.group(2));

            if (m.group(3) != null && m.group(4) != null) {
                double num = Double.parseDouble(m.group(3));
                double den = Double.parseDouble(m.group(4));
                if (den != 0) totalPouces += num / den;
            }
        }
        return totalPouces > 0 ? totalPouces : 1.0;
    }

private String formaterEchelleImperiale(double echelle) {
    int pieds = (int) (echelle / 12);
    double reste = echelle % 12;
    int pouces = (int) reste;
    double fraction = reste - pouces;

    StringBuilder sb = new StringBuilder();
    if (pieds > 0) sb.append(pieds).append("'");
    if (pouces > 0 || fraction > 0.01) {
        if (pieds > 0) sb.append(" ");
        sb.append(pouces);
        if (fraction >= 0.2 && fraction < 0.4) sb.append(" 1/4");
        else if (fraction >= 0.4 && fraction < 0.6) sb.append(" 1/2");
        else if (fraction >= 0.6 && fraction < 0.9) sb.append(" 3/4");
        sb.append("\"");
    }
    return sb.length() == 0 ? "1\"" : sb.toString().trim();
}
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
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
        btn_changerLangue = new javax.swing.JButton();
        panel_menu = new javax.swing.JPanel();
        lbl_blocsTitre = new javax.swing.JLabel();
        panel_dimensionsBloc = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lbl_largeurBloc = new javax.swing.JLabel();
        txt_largeurBloc = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lbl_hauteurBloc = new javax.swing.JLabel();
        txt_hauteurBloc = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        lbl_coutParBloc = new javax.swing.JLabel();
        txt_coutParBloc = new javax.swing.JTextField();
        lbl_actionsTitre = new javax.swing.JLabel();
        panel_boutonAction = new javax.swing.JPanel();
        btn_deplacerZone = new javax.swing.JButton();
        btn_redimensionnerZone = new javax.swing.JButton();
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
        lbl_blocsTotaux = new javax.swing.JLabel();
        lbl_coutTotal = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btn_simuler = new javax.swing.JButton();
        panel_principal = new javax.swing.JPanel();
        panel_echelle = new javax.swing.JPanel();
        btn_vuePrecedente = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_vueSuivante = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        lbl_vueCourante = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        btn_appliquerEchelle = new javax.swing.JButton();
        fille11 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 30), new java.awt.Dimension(50, 30), new java.awt.Dimension(50, 30));
        lbl_echelle = new javax.swing.JLabel();
        txt_echelle = new javax.swing.JTextField();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        btn_zoomIn = new javax.swing.JButton();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_zoomOut = new javax.swing.JButton();
        panel_vues = new javax.swing.JPanel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panel_boutons.setMaximumSize(new java.awt.Dimension(33897, 40));
        panel_boutons.setMinimumSize(new java.awt.Dimension(1130, 40));
        panel_boutons.setPreferredSize(new java.awt.Dimension(412, 40));
        panel_boutons.setLayout(new javax.swing.BoxLayout(panel_boutons, javax.swing.BoxLayout.X_AXIS));

        btn_annulerAction.setIconTextGap(15);
        btn_annulerAction.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_annulerAction.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_annulerAction.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_boutons.add(btn_annulerAction);
        panel_boutons.add(filler1);

        btn_retablirAction.setIconTextGap(15);
        btn_retablirAction.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_retablirAction.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_retablirAction.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_boutons.add(btn_retablirAction);
        panel_boutons.add(filler2);

        btn_importerPdf.setIconTextGap(15);
        btn_importerPdf.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_importerPdf.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_importerPdf.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_boutons.add(btn_importerPdf);
        panel_boutons.add(filler3);

        btn_exporterPdf.setIconTextGap(15);
        btn_exporterPdf.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_exporterPdf.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_exporterPdf.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_boutons.add(btn_exporterPdf);
        panel_boutons.add(filler4);

        btn_ajouterVue.setIconTextGap(15);
        btn_ajouterVue.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_ajouterVue.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_ajouterVue.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_boutons.add(btn_ajouterVue);
        panel_boutons.add(filler5);

        btn_supprimerVue.setIconTextGap(15);
        btn_supprimerVue.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_supprimerVue.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_supprimerVue.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_boutons.add(btn_supprimerVue);
        panel_boutons.add(filler6);

        btn_rognerVue.setIconTextGap(15);
        btn_rognerVue.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_rognerVue.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_rognerVue.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_boutons.add(btn_rognerVue);
        panel_boutons.add(filler7);

        btn_changerLangue.setMaximumSize(new java.awt.Dimension(50, 30));
        btn_changerLangue.setMinimumSize(new java.awt.Dimension(50, 30));
        btn_changerLangue.setPreferredSize(new java.awt.Dimension(50, 30));
        panel_boutons.add(btn_changerLangue);

        getContentPane().add(panel_boutons, java.awt.BorderLayout.PAGE_START);

        panel_menu.setMaximumSize(new java.awt.Dimension(300, 280));
        panel_menu.setMinimumSize(new java.awt.Dimension(300, 280));
        panel_menu.setPreferredSize(new java.awt.Dimension(300, 280));

        lbl_blocsTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_blocsTitre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_blocsTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_blocsTitre.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_blocsTitre.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_blocsTitre.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_menu.add(lbl_blocsTitre);

        panel_dimensionsBloc.setMaximumSize(new java.awt.Dimension(300, 120));
        panel_dimensionsBloc.setMinimumSize(new java.awt.Dimension(300, 120));
        panel_dimensionsBloc.setPreferredSize(new java.awt.Dimension(300, 120));
        panel_dimensionsBloc.setLayout(new javax.swing.BoxLayout(panel_dimensionsBloc, javax.swing.BoxLayout.Y_AXIS));

        lbl_largeurBloc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_largeurBloc.setMaximumSize(new java.awt.Dimension(110, 30));
        lbl_largeurBloc.setMinimumSize(new java.awt.Dimension(110, 30));
        lbl_largeurBloc.setPreferredSize(new java.awt.Dimension(110, 30));

        txt_largeurBloc.setMaximumSize(new java.awt.Dimension(90, 30));
        txt_largeurBloc.setMinimumSize(new java.awt.Dimension(90, 30));
        txt_largeurBloc.setPreferredSize(new java.awt.Dimension(90, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lbl_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(5, 5, 5)
                    .addComponent(txt_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 5, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbl_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 5, Short.MAX_VALUE)))
        );

        panel_dimensionsBloc.add(jPanel2);

        lbl_hauteurBloc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_hauteurBloc.setMaximumSize(new java.awt.Dimension(110, 30));
        lbl_hauteurBloc.setMinimumSize(new java.awt.Dimension(110, 30));
        lbl_hauteurBloc.setPreferredSize(new java.awt.Dimension(110, 30));

        txt_hauteurBloc.setMaximumSize(new java.awt.Dimension(90, 30));
        txt_hauteurBloc.setMinimumSize(new java.awt.Dimension(90, 30));
        txt_hauteurBloc.setPreferredSize(new java.awt.Dimension(90, 30));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lbl_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(5, 5, 5)
                    .addComponent(txt_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 5, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbl_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 5, Short.MAX_VALUE)))
        );

        panel_dimensionsBloc.add(jPanel3);

        lbl_coutParBloc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_coutParBloc.setMaximumSize(new java.awt.Dimension(110, 30));
        lbl_coutParBloc.setMinimumSize(new java.awt.Dimension(110, 30));
        lbl_coutParBloc.setPreferredSize(new java.awt.Dimension(110, 30));

        txt_coutParBloc.setMaximumSize(new java.awt.Dimension(90, 30));
        txt_coutParBloc.setMinimumSize(new java.awt.Dimension(90, 30));
        txt_coutParBloc.setPreferredSize(new java.awt.Dimension(90, 30));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lbl_coutParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(5, 5, 5)
                    .addComponent(txt_coutParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 5, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbl_coutParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_coutParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 5, Short.MAX_VALUE)))
        );

        panel_dimensionsBloc.add(jPanel4);

        panel_menu.add(panel_dimensionsBloc);

        lbl_actionsTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_actionsTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_actionsTitre.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_actionsTitre.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_actionsTitre.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_menu.add(lbl_actionsTitre);

        panel_boutonAction.setMaximumSize(new java.awt.Dimension(300, 40));
        panel_boutonAction.setMinimumSize(new java.awt.Dimension(300, 40));
        panel_boutonAction.setPreferredSize(new java.awt.Dimension(300, 40));

        btn_deplacerZone.setIconTextGap(0);
        btn_deplacerZone.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_deplacerZone.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_deplacerZone.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_boutonAction.add(btn_deplacerZone);

        btn_redimensionnerZone.setIconTextGap(0);
        btn_redimensionnerZone.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_redimensionnerZone.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_redimensionnerZone.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_boutonAction.add(btn_redimensionnerZone);

        btn_supprimerZone.setIconTextGap(0);
        btn_supprimerZone.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_supprimerZone.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_supprimerZone.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_boutonAction.add(btn_supprimerZone);

        panel_menu.add(panel_boutonAction);

        lbl_typeZoneTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_typeZoneTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_typeZoneTitre.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_typeZoneTitre.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_typeZoneTitre.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_menu.add(lbl_typeZoneTitre);

        panel_typeZone.setMaximumSize(new java.awt.Dimension(300, 40));
        panel_typeZone.setMinimumSize(new java.awt.Dimension(300, 40));
        panel_typeZone.setPreferredSize(new java.awt.Dimension(300, 40));

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
        lbl_formeZoneTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_formeZoneTitre.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_formeZoneTitre.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_formeZoneTitre.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_menu.add(lbl_formeZoneTitre);

        panel_formeZone.setMaximumSize(new java.awt.Dimension(300, 40));
        panel_formeZone.setMinimumSize(new java.awt.Dimension(300, 40));
        panel_formeZone.setPreferredSize(new java.awt.Dimension(300, 40));

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

        lbl_blocsTotaux.setMaximumSize(new java.awt.Dimension(140, 30));
        lbl_blocsTotaux.setMinimumSize(new java.awt.Dimension(140, 30));
        lbl_blocsTotaux.setPreferredSize(new java.awt.Dimension(140, 30));
        panel_menu.add(lbl_blocsTotaux);

        lbl_coutTotal.setMaximumSize(new java.awt.Dimension(140, 30));
        lbl_coutTotal.setMinimumSize(new java.awt.Dimension(140, 30));
        lbl_coutTotal.setPreferredSize(new java.awt.Dimension(140, 30));
        panel_menu.add(lbl_coutTotal);

        jPanel1.setMaximumSize(new java.awt.Dimension(300, 40));
        jPanel1.setMinimumSize(new java.awt.Dimension(300, 40));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 40));

        btn_simuler.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_simuler.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_simuler.setPreferredSize(new java.awt.Dimension(75, 30));
        jPanel1.add(btn_simuler);

        panel_menu.add(jPanel1);

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
        panel_echelle.add(filler9);

        lbl_vueCourante.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbl_vueCourante.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_vueCourante.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_vueCourante.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_echelle.add(lbl_vueCourante);
        panel_echelle.add(filler10);

        btn_appliquerEchelle.setMaximumSize(new java.awt.Dimension(160, 30));
        btn_appliquerEchelle.setMinimumSize(new java.awt.Dimension(160, 30));
        btn_appliquerEchelle.setPreferredSize(new java.awt.Dimension(160, 30));
        panel_echelle.add(btn_appliquerEchelle);
        panel_echelle.add(fille11);

        lbl_echelle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_echelle.setMaximumSize(new java.awt.Dimension(60, 30));
        lbl_echelle.setMinimumSize(new java.awt.Dimension(60, 30));
        lbl_echelle.setPreferredSize(new java.awt.Dimension(60, 30));
        panel_echelle.add(lbl_echelle);

        txt_echelle.setMaximumSize(new java.awt.Dimension(90, 30));
        txt_echelle.setMinimumSize(new java.awt.Dimension(90, 30));
        txt_echelle.setPreferredSize(new java.awt.Dimension(90, 30));
        panel_echelle.add(txt_echelle);
        panel_echelle.add(filler12);

        btn_zoomIn.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_zoomIn.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_zoomIn.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(btn_zoomIn);
        panel_echelle.add(filler13);

        btn_zoomOut.setMaximumSize(new java.awt.Dimension(75, 30));
        btn_zoomOut.setMinimumSize(new java.awt.Dimension(75, 30));
        btn_zoomOut.setPreferredSize(new java.awt.Dimension(75, 30));
        panel_echelle.add(btn_zoomOut);

        panel_principal.add(panel_echelle);

        javax.swing.GroupLayout panel_vuesLayout = new javax.swing.GroupLayout(panel_vues);
        panel_vues.setLayout(panel_vuesLayout);
        panel_vuesLayout.setHorizontalGroup(
            panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 830, Short.MAX_VALUE)
        );
        panel_vuesLayout.setVerticalGroup(
            panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
        );

        panel_principal.add(panel_vues);

        getContentPane().add(panel_principal, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_ajouterVue;
    private javax.swing.JButton btn_annulerAction;
    private javax.swing.JButton btn_appliquerEchelle;
    private javax.swing.JButton btn_changerLangue;
    private javax.swing.JButton btn_deplacerZone;
    private javax.swing.JButton btn_exporterPdf;
    private javax.swing.JButton btn_formeRectangle;
    private javax.swing.JButton btn_formeTriangle;
    private javax.swing.JButton btn_formeTriangleTronque;
    private javax.swing.JButton btn_importerPdf;
    private javax.swing.JButton btn_redimensionnerZone;
    private javax.swing.JButton btn_retablirAction;
    private javax.swing.JButton btn_rognerVue;
    private javax.swing.JButton btn_simuler;
    private javax.swing.JButton btn_supprimerVue;
    private javax.swing.JButton btn_supprimerZone;
    private javax.swing.JButton btn_vuePrecedente;
    private javax.swing.JButton btn_vueSuivante;
    private javax.swing.JButton btn_zoneArmatureBloc;
    private javax.swing.JButton btn_zoneArmatureClassique;
    private javax.swing.JButton btn_zoneOuverture;
    private javax.swing.JButton btn_zoomIn;
    private javax.swing.JButton btn_zoomOut;
    private javax.swing.Box.Filler fille11;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lbl_actionsTitre;
    private javax.swing.JLabel lbl_blocsTitre;
    private javax.swing.JLabel lbl_blocsTotaux;
    private javax.swing.JLabel lbl_coutParBloc;
    private javax.swing.JLabel lbl_coutTotal;
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
    private javax.swing.JTextField txt_coutParBloc;
    private javax.swing.JTextField txt_echelle;
    private javax.swing.JTextField txt_hauteurBloc;
    private javax.swing.JTextField txt_largeurBloc;
    // End of variables declaration//GEN-END:variables
}

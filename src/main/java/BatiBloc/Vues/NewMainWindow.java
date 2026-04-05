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
import BatiBloc.Domaine.dto.RognerVueDTO;
import java.awt.Cursor;
import java.util.function.Consumer;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class NewMainWindow extends JFrame {
    
    private boolean formeZoneEstChoisie = false;
    private boolean modeRognage = false;
    private boolean typeZoneEstChoisi = false;
    private JButton btnAnnulerModif;
    private JButton btnConfirmerModif;
    private JButton btnStopZoneDrawing = null;
    private JCheckBox chkToutesVues;
    private DrawingPanel drawingPanel;
    private final MainController controleur;
    private JPanel panneauVues;
    private JPanel viewsThumbnailPanel;
    private List<JCheckBox> viewCheckBoxes;
    private Locale langueCourante = Locale.FRENCH;
    private ResourceBundle resourceBundle;
    private String actionCourantePanneauVues = null;
    public enum Langage { FR, EN }

    public NewMainWindow(MainController controleur) {
        this.controleur = controleur;
        this.initComponents();
        this.initFormulaire();        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void initFormulaire(){        
        this.initDrawingPanel();
        this.initIconesBoutons();
        this.majLanguetextes(); 
        this.initPlaceholders();
        this.initListeners();
        this.majVueCourante();
    }
    
    private void initDrawingPanel(){
        this.drawingPanel = new DrawingPanel(this.controleur);
        this.drawingPanel.setOnRognageTermine(rect -> this.rognerVueCourante(rect));
        this.drawingPanel.setOnZoneAjoutee(() -> this.majVueCourante());
        
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
        this.btn_rognerVue.setText(this.getComposantTexte("button.cropView"));

        this.majVueCourante();
    }
    
    private String getComposantTexte(String key){
        return this.resourceBundle.getString(key);
    }
    
    private void majVueCourante() {
        VueDTO vue = this.controleur.getVueCourante();
        if (this.drawingPanel != null) {
            this.drawingPanel.afficherVue(vue);
            
            this.lbl_vueCourante.setText(vue != null ? "Vue " + (vue.getIndex() + 1) : "Aucune vue");
        }
    }
    
    private void majLanguetextes() {
        this.resourceBundle = ResourceBundle.getBundle("i18n.messages", this.langueCourante);
        
        this.btn_appliquerEchelle.setText(this.getComposantTexte("button.applyScale"));
        this.btn_changerLangue.setText(this.getComposantTexte("button.changeLanguage"));

        this.configBoutons();
        this.configLabels();
        this.majLanguePanneauVues();

        this.revalidate();
        this.repaint();
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
    
    private void configBouton(JButton bouton, String nomBouton, String nomIcone){
        bouton.setText(this.getComposantTexte(nomBouton));
        bouton.setIcon(new FlatSVGIcon("icones/" + nomIcone, 25, 25));
        bouton.setIconTextGap(10);
    }
    
    private void configLabels() {
        this.lbl_blocsTitre.setText(this.getComposantTexte("label.block"));
        this.lbl_actionsTitre.setText(this.getComposantTexte("label.action"));
        this.lbl_largeurBloc.setText(this.getComposantTexte("label.width"));
        this.lbl_hauteurBloc.setText(this.getComposantTexte("label.height"));
        this.lbl_prixParBloc.setText(this.getComposantTexte("label.costPerBlock"));
        this.lbl_typeZoneTitre.setText(this.getComposantTexte("label.zoneType"));
        this.lbl_formeZoneTitre.setText(this.getComposantTexte("label.form"));
        this.lbl_blocsTotaux.setText(this.getComposantTexte("label.totalBlock"));
        this.lbl_coutTotal.setText(this.getComposantTexte("label.totalCost"));
        this.lbl_echelle.setText(this.getComposantTexte("label.scale"));
    }
    
    private void majLanguePanneauVues() {
        if (this.panneauVues != null && this.panneauVues.isShowing()) {            
            this.configPanneauVues();
            
            this.chkToutesVues.setText(this.getComposantTexte("panel.selectAll"));
            this.btnConfirmerModif.setText(this.getComposantTexte("panel.confirm"));
            this.btnAnnulerModif.setText(this.getComposantTexte("panel.cancel"));
        }
    }
    

    
    private void configPanneauVues() {
        List<Integer> indicesCochesAvant = this.sauvegarderCasesCochees();

        this.creerPanneauVues(this.actionCourantePanneauVues);

        this.restaurerCasesCochees(indicesCochesAvant);
    }
    
    private List<Integer> sauvegarderCasesCochees() {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < this.viewCheckBoxes.size(); i++) {
            if (this.viewCheckBoxes.get(i).isSelected()) {
                indices.add(i);
            }
        }
        return indices;
    }
    
    private void restaurerCasesCochees(List<Integer> indices) {
        for (int index : indices) {
            if (index < this.viewCheckBoxes.size()) {
                this.viewCheckBoxes.get(index).setSelected(true);
            }
        }
    }


    
    private void creerPanneauVues(String actionKey) {
        this.actionCourantePanneauVues = actionKey;
        
        if (this.panneauVues != null) {
            this.panel_principal.remove(this.panneauVues);
            this.panneauVues = null;
        }

        List<VueDTO> vues = this.controleur.getVues();
        if (vues.isEmpty()) return;
        
        String action = this.resourceBundle.getString(this.actionCourantePanneauVues);
        String titre = MessageFormat.format(this.resourceBundle.getString("panel.selectViews"), action);

        this.panneauVues = new JPanel();
        this.panneauVues.setLayout(new BorderLayout());
        this.panneauVues.setBorder(BorderFactory.createTitledBorder(titre));
        this.panneauVues.setBackground(UIManager.getColor("Panel.background"));

        this.viewsThumbnailPanel = new JPanel();
        this.viewsThumbnailPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.viewsThumbnailPanel.setBackground(UIManager.getColor("Panel.background"));

        this.viewCheckBoxes = new ArrayList<>();
        JPanel panelVue;
        JCheckBox cb;
        BufferedImage img;
        BufferedImage miniature;
        JLabel lblMiniature;
        
        for (int i = 0; i < vues.size(); i++) {
            panelVue = new JPanel();
            panelVue.setLayout(new BorderLayout());
            panelVue.setBackground(UIManager.getColor("Panel.background"));

            img = vues.get(i).getImage();
            miniature = this.createThumbnail(img, 100, 80);
            
            lblMiniature = new JLabel(new ImageIcon(miniature));
            
            lblMiniature.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            cb = new JCheckBox(vues.get(i).getNomVue(this.langueCourante));
            cb.addActionListener(e -> this.updateSelectAllButtonState());
            cb.setBackground(UIManager.getColor("Panel.background"));
            this.viewCheckBoxes.add(cb);
            
            int indexFinal = i;
            lblMiniature.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JCheckBox check = viewCheckBoxes.get(indexFinal);
                    check.setSelected(!check.isSelected());
                    updateSelectAllButtonState();
                }
            });

            panelVue.add(lblMiniature, BorderLayout.CENTER);
            panelVue.add(cb, BorderLayout.SOUTH);
            this.viewsThumbnailPanel.add(panelVue);
        }

        JPanel panelBoutons = new JPanel();
        this.chkToutesVues = new JCheckBox(this.resourceBundle.getString("panel.selectAll"));
        this.btnConfirmerModif = new JButton(this.resourceBundle.getString("panel.confirm"));
        this.btnAnnulerModif = new JButton(this.resourceBundle.getString("panel.cancel"));

        this.chkToutesVues.addActionListener(e -> this.toggleSelectAllViews());
        this.btnConfirmerModif.addActionListener(e -> this.confirmViewsModification());
        this.btnAnnulerModif.addActionListener(e -> this.cancelViewsModification());

        panelBoutons.add(this.chkToutesVues);
        panelBoutons.add(this.btnConfirmerModif);
        panelBoutons.add(this.btnAnnulerModif);

        this.panneauVues.add(new JScrollPane(this.viewsThumbnailPanel), BorderLayout.CENTER);
        this.panneauVues.add(panelBoutons, BorderLayout.SOUTH);
        this.panneauVues.setPreferredSize(new Dimension(getWidth(), 180));

        this.panel_principal.add(this.panneauVues, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
    
    private BufferedImage createThumbnail(BufferedImage imageOriginal, int largeur, int hauteur) {
        BufferedImage miniature = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = miniature.createGraphics();
        g.drawImage(imageOriginal, 0, 0, largeur, hauteur, null);
        g.dispose();
        return miniature;
    }
    
    private void updateSelectAllButtonState() {
        boolean tousCoches = viewCheckBoxes.stream().allMatch(JCheckBox::isSelected);
        this.chkToutesVues.setSelected(tousCoches);
    }
    
    
    private void toggleSelectAllViews(){
        boolean etat = this.chkToutesVues.isSelected();

        for (JCheckBox cb : viewCheckBoxes) {
            cb.setSelected(etat);
        }
    }

    private void confirmViewsModification() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < viewCheckBoxes.size(); i++) {
            if (viewCheckBoxes.get(i).isSelected()) {
                indices.add(i);
            }
        }
        if (!indices.isEmpty() && this.actionCourantePanneauVues.equalsIgnoreCase("panel.delete")) {
            this.controleur.supprimerVues(indices);
        }
        else if (!indices.isEmpty() && this.actionCourantePanneauVues.equalsIgnoreCase("panel.export")){
            this.controleur.exporterVues(indices);
        }
        
        this.cancelViewsModification();
        this.majVueCourante();
    }

    private void cancelViewsModification() {
        this.panel_principal.remove(this.panneauVues);
        revalidate();
        repaint();
    }
    
    private void initListeners(){
        this.initializeMainButtons();
        this.initializeZoneButtons();
        this.initializeViewButtons();
        this.initializeScaleControls();
        this.initializeLanguageControls();
    }
    
    private void initializeMainButtons(){
        this.addAction(this.btn_annulerAction, this.controleur::annulerAction);
        this.addAction(this.btn_retablirAction, this.controleur::retablirAction);
        this.addAction(this.btn_importerPdf, this.controleur::importerPdf);
        this.btn_exporterPdf.addActionListener(e -> this.creerPanneauVues("panel.export"));
        this.addAction(this.btn_ajouterVue, this.controleur::ajouterVue);
        this.btn_supprimerVue.addActionListener(e -> this.creerPanneauVues("panel.delete"));
        
        this.btn_rognerVue.addActionListener(e -> {
            this.modeRognage = !this.modeRognage;
            this.drawingPanel.setModeRognage(this.modeRognage);
            this.btn_rognerVue.setText(this.modeRognage ? "Rognage actif" : this.resourceBundle.getString("button.cropView"));
        });
        
        this.btn_changerLangue.addActionListener(e -> {
            if (this.langueCourante.equals(Locale.FRENCH)){
                this.langueCourante = Locale.ENGLISH;
            } else if (this.langueCourante.equals(Locale.ENGLISH)) {
                this.langueCourante = Locale.FRENCH;
            }
            this.majLanguetextes();
        });
    }

    private void initializeZoneButtons(){
        this.addZoneButtonListener(this.btn_zoneArmatureClassique, TypeZone.ARMATURE_CLASSIQUE);
        this.addZoneButtonListener(this.btn_zoneArmatureBloc, TypeZone.ARMATURE_BLOCS);
        this.addZoneButtonListener(this.btn_zoneOuverture, TypeZone.OUVERTURE);
    }
    
    private void initializeShapeButtons(){
        this.addShapeButtonListener(this.btn_formeRectangle, TypeForme.RECTANGLE);
        this.addShapeButtonListener(this.btn_formeTriangle, TypeForme.TRIANGLE);
        this.addShapeButtonListener(this.btn_formeTriangleTronque, TypeForme.TRIANGLE_TRONQUE);
    }
    
    private void initializeViewButtons(){
        this.addAction(this.btn_vuePrecedente, this.controleur::naviguerVuePrecedente);
        this.addAction(this.btn_vueSuivante, this.controleur::naviguerVueSuivante);
        this.addAction(this.btn_zoomIn, this.drawingPanel::zoomInCenter);
        this.addAction(this.btn_zoomOut, this.drawingPanel::zoomOutCenter);
    }
    
    private void initializeScaleControls() {
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
                    javax.swing.JOptionPane.showMessageDialog(this, "L'échelle doit être supérieure à 0.");
                    return;
                }
                this.controleur.setEchelleCourante(poucesParPixel);
                this.majVueCourante();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Format invalide.\nEntrez un nombre (ex: 3) ou un format impérial (ex: 3' 8\" 3/4)");
            }
        });
    }

    private void initializeLanguageControls() {
        
    }
    
    

 private double parserMesureImperiale(String texte) {
        double totalPouces = 0;

        int indexPied = texte.indexOf('\'');
        if (indexPied >= 0) {
            String piedsStr = texte.substring(0, indexPied).trim();
            totalPouces += Integer.parseInt(piedsStr) * 12;
            texte = texte.substring(indexPied + 1).trim();
        }

        int indexPouce = texte.indexOf('"');
        if (indexPouce >= 0) {
            String poucesStr = texte.substring(0, indexPouce).trim();
            totalPouces += Double.parseDouble(poucesStr);
            texte = texte.substring(indexPouce + 1).trim();
        }

        texte = texte.trim();
        if (!texte.isEmpty()) {
            String[] parts = texte.split("/");
            if (parts.length == 2) {
                double num = Double.parseDouble(parts[0].trim());
                double den = Double.parseDouble(parts[1].trim());
                if (den != 0) totalPouces += num / den;
            }
        }

        if (totalPouces <= 0) {
            return 1.0;
        }
        return totalPouces;
    }
    
    private void addAction(AbstractButton button, Runnable action){
        button.addActionListener(e -> {

            action.run();

            majVueCourante();

        });
    }
    
    private void addZoneButtonListener(AbstractButton button, TypeZone zone) {
        button.addActionListener(e ->
            this.withDrawingPanel(dp -> {
                dp.setTypeZoneEnCours(zone);
                this.typeZoneEstChoisi = true;
            })
        );
    }
    
    private void withDrawingPanel(Consumer<DrawingPanel> action) {
        if (this.drawingPanel instanceof DrawingPanel dp) {
            action.accept(dp);
        }
    }
    
    private void addShapeButtonListener(AbstractButton button, TypeForme forme) {
        button.addActionListener(e ->
            this.withDrawingPanel(dp -> {
                dp.setFormeEnCours(forme);
                this.formeZoneEstChoisie = true;
                this.enableDrawingIfReady();
            })
        );
    }
    
    private void enableDrawingIfReady() {
        if (this.typeZoneEstChoisi && this.formeZoneEstChoisie) {
            this.withDrawingPanel(dp -> {
                dp.setModeAjoutZone(true);
            });
            this.showStopDrawingButton();
        }
    }

    private void showStopDrawingButton() {
        if (this.btnStopZoneDrawing == null) {
            this.btnStopZoneDrawing = new JButton("Dessin actif");
            this.btnStopZoneDrawing.setMaximumSize(new java.awt.Dimension(150, 30));
            this.btnStopZoneDrawing.setMinimumSize(new java.awt.Dimension(150, 30));
            this.btnStopZoneDrawing.setPreferredSize(new java.awt.Dimension(150, 30));
            this.btnStopZoneDrawing.addActionListener(e -> this.stopZoneDrawingMode());
            this.panel_menu.add(this.btnStopZoneDrawing);
            this.panel_menu.revalidate();
            this.panel_menu.repaint();
        }
    }

    private void stopZoneDrawingMode() {
        if (this.drawingPanel instanceof DrawingPanel dp) {
            dp.setModeAjoutZone(false);
        }
        
        this.typeZoneEstChoisi = false;
        this.formeZoneEstChoisie = false;
        
        if (this.btnStopZoneDrawing != null) {
            this.panel_menu.remove(this.btnStopZoneDrawing);
            this.btnStopZoneDrawing = null;
            this.panel_menu.revalidate();
            this.panel_menu.repaint();
        }
    }
    
    private void bindLabelFocusToField(JLabel label, JTextField field) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                field.requestFocusInWindow();
            }
        });
    }
    
    private void initIconesBoutons() {
        this.btn_deplacerZone.setIcon(loadIcon("move_icon.svg", 25));
        this.btn_dimensionZone.setIcon(loadIcon("resize_icon.svg", 25));
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
    
        private FlatSVGIcon loadIcon(String name,int size){
        return new FlatSVGIcon("icones/" + name, size, size);
    }
    
    private void initPlaceholders() {
        this.txt_largeurBloc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 3'6\"1/3");
        this.txt_hauteurBloc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 1'2\"4/5");
        this.txt_prixParBloc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 5,00");
        this.txt_echelle.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: 3'6\"1/3");
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
        btn_changerLangue = new javax.swing.JButton();
        panel_menu = new javax.swing.JPanel();
        lbl_blocsTitre = new javax.swing.JLabel();
        panel_dimensionsBloc = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lbl_largeurBloc = new javax.swing.JLabel();
        txt_largeurBloc = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lbl_hauteurBloc = new javax.swing.JLabel();
        txt_hauteurBloc = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lbl_prixParBloc = new javax.swing.JLabel();
        txt_prixParBloc = new javax.swing.JTextField();
        lbl_actionsTitre = new javax.swing.JLabel();
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
        panel_information = new javax.swing.JPanel();
        lbl_blocsTotaux = new javax.swing.JLabel();
        lbl_coutTotal = new javax.swing.JLabel();
        panel_principal = new javax.swing.JPanel();
        panel_echelle = new javax.swing.JPanel();
        btn_vuePrecedente = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
        btn_vueSuivante = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        lbl_vueCourante = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        btn_appliquerEchelle = new javax.swing.JButton();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        lbl_echelle = new javax.swing.JLabel();
        txt_echelle = new javax.swing.JTextField();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        btn_zoomIn = new javax.swing.JButton();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0));
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

        btn_changerLangue.setMaximumSize(new java.awt.Dimension(60, 30));
        btn_changerLangue.setMinimumSize(new java.awt.Dimension(60, 30));
        btn_changerLangue.setPreferredSize(new java.awt.Dimension(60, 30));
        panel_boutons.add(btn_changerLangue);

        getContentPane().add(panel_boutons, java.awt.BorderLayout.PAGE_START);

        panel_menu.setMaximumSize(new java.awt.Dimension(275, 360));
        panel_menu.setMinimumSize(new java.awt.Dimension(275, 360));
        panel_menu.setPreferredSize(new java.awt.Dimension(275, 360));

        lbl_blocsTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_blocsTitre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_blocsTitre.setToolTipText("");
        lbl_blocsTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_blocsTitre.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_blocsTitre.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_blocsTitre.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_menu.add(lbl_blocsTitre);

        panel_dimensionsBloc.setMaximumSize(new java.awt.Dimension(250, 100));
        panel_dimensionsBloc.setMinimumSize(new java.awt.Dimension(250, 100));
        panel_dimensionsBloc.setPreferredSize(new java.awt.Dimension(250, 100));
        panel_dimensionsBloc.setLayout(new javax.swing.BoxLayout(panel_dimensionsBloc, javax.swing.BoxLayout.Y_AXIS));

        lbl_largeurBloc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_largeurBloc.setMaximumSize(new java.awt.Dimension(65, 30));
        lbl_largeurBloc.setMinimumSize(new java.awt.Dimension(65, 30));
        lbl_largeurBloc.setPreferredSize(new java.awt.Dimension(65, 30));
        jPanel1.add(lbl_largeurBloc);

        txt_largeurBloc.setMaximumSize(new java.awt.Dimension(80, 30));
        txt_largeurBloc.setMinimumSize(new java.awt.Dimension(80, 30));
        txt_largeurBloc.setPreferredSize(new java.awt.Dimension(80, 30));
        jPanel1.add(txt_largeurBloc);

        panel_dimensionsBloc.add(jPanel1);

        lbl_hauteurBloc.setMaximumSize(new java.awt.Dimension(65, 30));
        lbl_hauteurBloc.setMinimumSize(new java.awt.Dimension(65, 30));
        lbl_hauteurBloc.setPreferredSize(new java.awt.Dimension(65, 30));
        jPanel2.add(lbl_hauteurBloc);

        txt_hauteurBloc.setMaximumSize(new java.awt.Dimension(80, 30));
        txt_hauteurBloc.setMinimumSize(new java.awt.Dimension(80, 30));
        txt_hauteurBloc.setPreferredSize(new java.awt.Dimension(80, 30));
        jPanel2.add(txt_hauteurBloc);

        panel_dimensionsBloc.add(jPanel2);

        lbl_prixParBloc.setMaximumSize(new java.awt.Dimension(110, 30));
        lbl_prixParBloc.setMinimumSize(new java.awt.Dimension(110, 30));
        lbl_prixParBloc.setPreferredSize(new java.awt.Dimension(110, 30));
        jPanel3.add(lbl_prixParBloc);

        txt_prixParBloc.setMaximumSize(new java.awt.Dimension(80, 30));
        txt_prixParBloc.setMinimumSize(new java.awt.Dimension(80, 30));
        txt_prixParBloc.setPreferredSize(new java.awt.Dimension(80, 30));
        jPanel3.add(txt_prixParBloc);

        panel_dimensionsBloc.add(jPanel3);

        panel_menu.add(panel_dimensionsBloc);

        lbl_actionsTitre.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_actionsTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_actionsTitre.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_actionsTitre.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_actionsTitre.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_menu.add(lbl_actionsTitre);

        panel_boutonAction.setMaximumSize(new java.awt.Dimension(250, 40));
        panel_boutonAction.setMinimumSize(new java.awt.Dimension(250, 40));
        panel_boutonAction.setPreferredSize(new java.awt.Dimension(250, 40));

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
        lbl_typeZoneTitre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_typeZoneTitre.setMaximumSize(new java.awt.Dimension(100, 30));
        lbl_typeZoneTitre.setMinimumSize(new java.awt.Dimension(100, 30));
        lbl_typeZoneTitre.setPreferredSize(new java.awt.Dimension(100, 30));
        panel_menu.add(lbl_typeZoneTitre);

        panel_typeZone.setMaximumSize(new java.awt.Dimension(250, 40));
        panel_typeZone.setMinimumSize(new java.awt.Dimension(250, 40));
        panel_typeZone.setPreferredSize(new java.awt.Dimension(250, 40));

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

        panel_formeZone.setMaximumSize(new java.awt.Dimension(250, 40));
        panel_formeZone.setMinimumSize(new java.awt.Dimension(250, 40));
        panel_formeZone.setPreferredSize(new java.awt.Dimension(250, 40));

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

        panel_information.setMaximumSize(new java.awt.Dimension(275, 40));
        panel_information.setMinimumSize(new java.awt.Dimension(275, 40));
        panel_information.setName(""); // NOI18N
        panel_information.setPreferredSize(new java.awt.Dimension(275, 40));

        lbl_blocsTotaux.setMaximumSize(new java.awt.Dimension(110, 30));
        lbl_blocsTotaux.setMinimumSize(new java.awt.Dimension(110, 30));
        lbl_blocsTotaux.setPreferredSize(new java.awt.Dimension(110, 30));
        panel_information.add(lbl_blocsTotaux);

        lbl_coutTotal.setMaximumSize(new java.awt.Dimension(110, 30));
        lbl_coutTotal.setMinimumSize(new java.awt.Dimension(110, 30));
        lbl_coutTotal.setPreferredSize(new java.awt.Dimension(110, 30));
        panel_information.add(lbl_coutTotal);

        panel_menu.add(panel_information);

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

        btn_appliquerEchelle.setMaximumSize(new java.awt.Dimension(150, 30));
        btn_appliquerEchelle.setMinimumSize(new java.awt.Dimension(150, 30));
        btn_appliquerEchelle.setPreferredSize(new java.awt.Dimension(150, 30));
        panel_echelle.add(btn_appliquerEchelle);
        panel_echelle.add(filler11);

        lbl_echelle.setMaximumSize(new java.awt.Dimension(50, 30));
        lbl_echelle.setMinimumSize(new java.awt.Dimension(50, 30));
        lbl_echelle.setPreferredSize(new java.awt.Dimension(50, 30));
        panel_echelle.add(lbl_echelle);

        txt_echelle.setMaximumSize(new java.awt.Dimension(150, 30));
        txt_echelle.setMinimumSize(new java.awt.Dimension(150, 30));
        txt_echelle.setPreferredSize(new java.awt.Dimension(150, 30));
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
            .addGap(0, 883, Short.MAX_VALUE)
        );
        panel_vuesLayout.setVerticalGroup(
            panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
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
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lbl_actionsTitre;
    private javax.swing.JLabel lbl_blocsTitre;
    private javax.swing.JLabel lbl_blocsTotaux;
    private javax.swing.JLabel lbl_coutTotal;
    private javax.swing.JLabel lbl_echelle;
    private javax.swing.JLabel lbl_formeZoneTitre;
    private javax.swing.JLabel lbl_hauteurBloc;
    private javax.swing.JLabel lbl_largeurBloc;
    private javax.swing.JLabel lbl_prixParBloc;
    private javax.swing.JLabel lbl_typeZoneTitre;
    private javax.swing.JLabel lbl_vueCourante;
    private javax.swing.JPanel panel_boutonAction;
    private javax.swing.JPanel panel_boutons;
    private javax.swing.JPanel panel_dimensionsBloc;
    private javax.swing.JPanel panel_echelle;
    private javax.swing.JPanel panel_formeZone;
    private javax.swing.JPanel panel_information;
    private javax.swing.JPanel panel_menu;
    private javax.swing.JPanel panel_principal;
    private javax.swing.JPanel panel_typeZone;
    private javax.swing.JPanel panel_vues;
    private javax.swing.JTextField txt_echelle;
    private javax.swing.JTextField txt_hauteurBloc;
    private javax.swing.JTextField txt_largeurBloc;
    private javax.swing.JTextField txt_prixParBloc;
    // End of variables declaration//GEN-END:variables
}

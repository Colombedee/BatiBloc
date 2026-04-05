package BatiBloc.Vues;

import javax.swing.ImageIcon; 
import javax.swing.JLabel;
import javax.swing.*;
import BatiBloc.Domaine.Controleur.MainController;
import BatiBloc.Domaine.Dto.TypeForme;
import BatiBloc.Domaine.Dto.TypeZone;
import BatiBloc.Domaine.Dto.VueDTO;
//import BatiBloc.langue.TrComposant;
import BatiBloc.Domaine.Dto.RognerVueDTO;
//import BatiBloc.langue.Langage;
//import BatiBloc.langue.LangueManager;
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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.border.TitledBorder;

public class MainWindow extends JFrame {
    
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());
    private final MainController controleur;
    private IPanelAffichage drawingPanel;
    private boolean modeRognage = false;
    private String actionCourante = null;
    private JPanel panel_miniatures;
    private JPanel panel_selectionVues;
    private JCheckBox btn_toutSelectionner;
    private JButton btn_confirmerModification;
    private JButton btn_annulerModification;
    private List<JCheckBox> checkboxes;
    //pour le rognage inverse @roneh
    private Rectangle selectionRognageEnAttente = null;
    private JPanel panelChoixRognage;
    private JButton btn_appliquerRognageNormal;
    private JButton btn_appliquerRognageInverse;
    private JButton btn_annulerRognage;
            
    public MainWindow(MainController controleur) {
        this.controleur = controleur;
        this.initComponents();
        this.initialisationFormulaire();        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void initialisationFormulaire(){
        //this.controleur.changerLangue();
        this.configurationPanel3D();
        this.configurerSpinnerEchelle();
        this.configurerActionBoutons();
        this.configurationLiaisonsTraduction();
    }
    
    private void configurerSpinnerEchelle(){
        SpinnerNumberModel modeleEchelle = new SpinnerNumberModel(
        1.0 / 96.0,
        null,
        null,
        0.001
        );
        
        this.spi_echelle.setModel(modeleEchelle);
        this.spi_echelle.addChangeListener(e -> {
            double nouvelleEchelle = ((Number) this.spi_echelle.getValue()).doubleValue();
            if (nouvelleEchelle > 0) {
                this.controleur.setEchelleCourante(nouvelleEchelle);
                this.rafraichirVueCourante();
            }
        });
    }
    
    private void configurationPanel3D(){
        this.drawingPanel = new DrawingPanel(this.controleur);
        //this.drawingPanel.setOnRognageTermine(rect -> this.rognerVueCourante(rect));
        this.drawingPanel.setOnRognageTermine(rect -> this.preparerChoixRognage(rect));  // @roneh
        
        this.drawingPanel.setOnZoneAjoutee(() -> this.rafraichirVueCourante());
        if (this.drawingPanel instanceof DrawingPanel dp) //Colombe
        {
            dp.setModeAjoutZone(true);
        }
        
        this.jPanel1.setLayout(new BorderLayout());
        this.jPanel1.add((Component) this.drawingPanel, BorderLayout.CENTER);
        
        JPanel panelZoom = new JPanel();
        panelZoom.setOpaque(false);
        panelZoom.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelZoom.add(this.btn_zoomer);
        panelZoom.add(this.btn_dezoomer);
        
        this.jPanel1.add(panelZoom, BorderLayout.SOUTH);
        this.initialiserPanelChoixRognage(); // @roneh
    }
    
    private void configurerActionBoutons() {      
        this.btn_precedent.addActionListener(e -> {
            this.controleur.naviguerVuePrecedente();
            this.rafraichirVueCourante();
        });
        
        this.btn_suivant.addActionListener(e -> {
            this.controleur.naviguerVueSuivante();
            this.rafraichirVueCourante();
        });
        
        this.btn_importer.addActionListener(e -> {
            //this.controleur.importerFichierPdf();
            this.rafraichirVueCourante();
        });
        
        this.btn_rogner.addActionListener(e -> {
            this.modeRognage = !this.modeRognage;
            this.drawingPanel.setModeRognage(this.modeRognage);
            this.btn_rogner.setText(this.modeRognage ? "Rognage actif" : "button.crop");
        });
        
        //this.btn_changerLangue.addActionListener(e -> this.controleur.changerLangue());
        this.btn_exporter.addActionListener(e -> this.afficherPanelVues("panel.export"));
        this.btn_supprimer.addActionListener(e -> this.afficherPanelVues("panel.delete"));
    
    this.cbo_typeZone.addActionListener(e -> {
        int index = this.cbo_typeZone.getSelectedIndex();
        TypeZone type;
        switch (index) {
            case 1: type = TypeZone.ARMATURE_BLOCS; break;
            case 2: type = TypeZone.OUVERTURE; break;
            default: type = TypeZone.ARMATURE_CLASSIQUE; break;        
        }
        if (this.drawingPanel instanceof DrawingPanel dp){
            dp.setTypeZoneEnCours(type);
        }
    });
    
    this.cbo_formeZone.addActionListener(e -> {
        int index = this.cbo_formeZone.getSelectedIndex();
        TypeForme forme;
        switch (index) {
            case 1: forme = TypeForme.TRIANGLE; break;
            case 2: forme = TypeForme.TRIANGLE_TRONQUE; break;
            default: forme = TypeForme.RECTANGLE; break;        
        }
        if (this.drawingPanel instanceof DrawingPanel dp) {
            dp.setFormeEnCours(forme);
            dp.setModeAjoutZone(true);
        }
    });
    }
    
    private void configurationLiaisonsTraduction(){
        //new TrComposant<>(this.btn_importer, "button.import");
        //new TrComposant<>(this.btn_exporter, "button.export");
        //new TrComposant<>(this.btn_changerLangue, "button.changeLanguage");
        //new TrComposant<>(this.btn_supprimer, "button.delete");
        //new TrComposant<>(this.btn_rogner, "button.crop");
                
        //new TrComposant<>(this.lbl_echelle, "label.scale");
        //new TrComposant<>(this.lbl_type, "label.type");
        //new TrComposant<>(this.lbl_forme, "label.form");
        //new TrComposant<>(this.lbl_largeurBloc, "label.width");
        //new TrComposant<>(this.lbl_hauteurBloc, "label.height");
        //new TrComposant<>(this.lbl_prixParBloc, "label.pricePerBloc");
        //new TrComposant<>(this.lbl_nombreBloc, "label.numberBlock");
        //new TrComposant<>(this.lbl_prixTotal, "label.totalPrice");

        this.cbo_typeZone.addItem("button.type.classicFrame");
        this.cbo_typeZone.addItem("button.type.reinforcementBlocks");
        this.cbo_typeZone.addItem("button.type.opening");
        this.cbo_formeZone.addItem("button.form.rectangle");
        this.cbo_formeZone.addItem("button.form.triangle");
        this.cbo_formeZone.addItem("button.form.truncatedTriangle");

        this.ajouterRendererComboBox(this.cbo_typeZone);
        this.ajouterRendererComboBox(this.cbo_formeZone);
    }
    
    private void ajouterRendererComboBox(JComboBox<String> comboBox) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof String key) {
                    //setText(LangueManager.tr(key));
                }
                
                return this;
            }
        });
    }
        
    private void rafraichirVueCourante() {
        VueDTO vue = this.controleur.getVueCourante();
        if (this.drawingPanel != null) {
            this.drawingPanel.afficherVue(vue);
        }
        
        double echelle = this.controleur.getEchelleCourante();
        this.spi_echelle.setValue(echelle);
    }
    
    private void changerLangueFenetreVues() {
        if (this.panel_selectionVues != null && this.panel_selectionVues.isShowing()) {
            //String action = LangueManager.tr(this.actionCourante);
            //String titre = MessageFormat.format(LangueManager.tr("panel.selectViews"), action);
            
            if (this.panel_selectionVues.getBorder() instanceof TitledBorder border) {
                //border.setTitle(titre);
            }
            
            this.panel_selectionVues.repaint();
            
            //this.btn_toutSelectionner.setText(LangueManager.tr("panel.selectAll"));
            //this.btn_confirmerModification.setText(LangueManager.tr("panel.confirm"));
            //this.btn_annulerModification.setText(LangueManager.tr("panel.cancel"));
        }
        this.changerLangueTitreFenetreVues();
    }
    
    private void changerLangueTitreFenetreVues() {
        if (this.panel_selectionVues == null || !this.panel_selectionVues.isShowing() || this.actionCourante == null) {
            return;
        }
        
        //String action = LangueManager.tr(this.actionCourante);
        //String titre = MessageFormat.format(LangueManager.tr("panel.selectViews"),action);
        
        if (this.panel_selectionVues.getBorder() instanceof TitledBorder tb) {
            //tb.setTitle(titre);
            this.panel_selectionVues.repaint();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_authentification = new javax.swing.JButton();
        btn_changerLangue = new javax.swing.JButton();
        btn_precedent = new javax.swing.JButton();
        btn_suivant = new javax.swing.JButton();
        btn_importer = new javax.swing.JButton();
        panel_blocs = new javax.swing.JPanel();
        lbl_largeurBloc = new javax.swing.JLabel();
        lbl_hauteurBloc = new javax.swing.JLabel();
        txt_largeurBloc = new javax.swing.JTextField();
        txt_hauteurBloc = new javax.swing.JTextField();
        lbl_prixParBloc = new javax.swing.JLabel();
        txt_prixParBloc = new javax.swing.JTextField();
        panel_vues = new javax.swing.JPanel();
        btn_supprimer = new javax.swing.JButton();
        btn_rogner = new javax.swing.JButton();
        lbl_echelle = new javax.swing.JLabel();
        spi_echelle = new javax.swing.JSpinner();
        panel_zones = new javax.swing.JPanel();
        lbl_type = new javax.swing.JLabel();
        cbo_typeZone = new javax.swing.JComboBox<>();
        lbl_forme = new javax.swing.JLabel();
        cbo_formeZone = new javax.swing.JComboBox<>();
        btn_exporter = new javax.swing.JButton();
        lbl_nombreBloc = new javax.swing.JLabel();
        lbl_prixTotal = new javax.swing.JLabel();
        txt_nbrTotalDeBloc = new javax.swing.JTextField();
        txt_prixTotal = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btn_zoomer = new javax.swing.JButton();
        btn_dezoomer = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_changerLangue.setName("btn_changerLangue"); // NOI18N

        btn_precedent.setText("<");

        btn_suivant.setText(">");

        btn_importer.setPreferredSize(new java.awt.Dimension(85, 25));

        panel_blocs.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Blocs", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 18))); // NOI18N
        panel_blocs.setPreferredSize(new java.awt.Dimension(150, 100));

        lbl_largeurBloc.setPreferredSize(new java.awt.Dimension(95, 25));

        lbl_hauteurBloc.setPreferredSize(new java.awt.Dimension(95, 25));

        txt_largeurBloc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_largeurBloc.setText("0");
        txt_largeurBloc.setPreferredSize(new java.awt.Dimension(95, 25));

        txt_hauteurBloc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_hauteurBloc.setText("0");
        txt_hauteurBloc.setPreferredSize(new java.awt.Dimension(95, 25));

        lbl_prixParBloc.setPreferredSize(new java.awt.Dimension(95, 25));

        txt_prixParBloc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_prixParBloc.setText("0");
        txt_prixParBloc.setPreferredSize(new java.awt.Dimension(95, 25));

        javax.swing.GroupLayout panel_blocsLayout = new javax.swing.GroupLayout(panel_blocs);
        panel_blocs.setLayout(panel_blocsLayout);
        panel_blocsLayout.setHorizontalGroup(
            panel_blocsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_blocsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_blocsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_prixParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panel_blocsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_prixParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_blocsLayout.setVerticalGroup(
            panel_blocsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_blocsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_blocsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_largeurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_blocsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hauteurBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_blocsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_prixParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_prixParBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_vues.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Vues", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 18))); // NOI18N
        panel_vues.setPreferredSize(new java.awt.Dimension(150, 100));

        btn_supprimer.setPreferredSize(new java.awt.Dimension(95, 25));

        btn_rogner.setPreferredSize(new java.awt.Dimension(95, 25));

        lbl_echelle.setLabelFor(spi_echelle);
        lbl_echelle.setPreferredSize(new java.awt.Dimension(95, 25));

        spi_echelle.setPreferredSize(new java.awt.Dimension(95, 25));

        javax.swing.GroupLayout panel_vuesLayout = new javax.swing.GroupLayout(panel_vues);
        panel_vues.setLayout(panel_vuesLayout);
        panel_vuesLayout.setHorizontalGroup(
            panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_vuesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_echelle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spi_echelle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_rogner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_vuesLayout.setVerticalGroup(
            panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_vuesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_rogner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_vuesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_echelle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spi_echelle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_zones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Zones", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 18))); // NOI18N
        panel_zones.setPreferredSize(new java.awt.Dimension(150, 100));

        lbl_type.setPreferredSize(new java.awt.Dimension(95, 25));

        lbl_forme.setPreferredSize(new java.awt.Dimension(95, 25));

        javax.swing.GroupLayout panel_zonesLayout = new javax.swing.GroupLayout(panel_zones);
        panel_zones.setLayout(panel_zonesLayout);
        panel_zonesLayout.setHorizontalGroup(
            panel_zonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_zonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_zonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_forme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel_zonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbo_typeZone, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbo_formeZone, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel_zonesLayout.setVerticalGroup(
            panel_zonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_zonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_zonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbo_typeZone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_zonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_forme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbo_formeZone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_exporter.setPreferredSize(new java.awt.Dimension(85, 25));

        lbl_nombreBloc.setPreferredSize(new java.awt.Dimension(120, 25));

        lbl_prixTotal.setPreferredSize(new java.awt.Dimension(120, 25));

        txt_nbrTotalDeBloc.setEditable(false);
        txt_nbrTotalDeBloc.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txt_prixTotal.setEditable(false);
        txt_prixTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        btn_zoomer.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btn_zoomer.setText("+");

        btn_dezoomer.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btn_dezoomer.setText("-");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_zoomer, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_dezoomer, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 392, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_dezoomer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_zoomer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btn_precedent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_suivant)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_importer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_exporter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 232, Short.MAX_VALUE)
                .addComponent(btn_changerLangue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_authentification, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(panel_blocs, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                        .addComponent(panel_vues, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                        .addComponent(panel_zones, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_nombreBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_prixTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_nbrTotalDeBloc, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_prixTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_changerLangue, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_authentification, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_precedent, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_suivant, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_importer, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_exporter, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_vues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_zones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_blocs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_nombreBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nbrTotalDeBloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_prixTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_prixTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_zoomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_zoomerActionPerformed
        // @ roneh . Zoomer avec le bouton +
        //drawingPanel.zoomInCenter();
    }//GEN-LAST:event_btn_zoomerActionPerformed

    private void btn_dezoomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_dezoomerActionPerformed
        //@roneh pour dezoomer avec le bouton -
         //drawingPanel.zoomOutCenter();
    }//GEN-LAST:event_btn_dezoomerActionPerformed

    private void afficherPanelVues(String actionKey) {
        this.actionCourante = actionKey;
        
        if (this.panel_selectionVues != null) {
            this.jPanel1.remove(this.panel_selectionVues);
            this.panel_selectionVues = null;
        }

        List<VueDTO> vues = this.controleur.getVues();
        if (vues.isEmpty()) return;

        //Locale locale = (LangueManager.getLangageCourant() == Langage.FR)? Locale.FRENCH : Locale.ENGLISH;

        //ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
        //String action = bundle.getString(this.actionCourante);
        //String titre = MessageFormat.format(bundle.getString("panel.selectViews"), action);

        this.panel_selectionVues = new JPanel();
        this.panel_selectionVues.setLayout(new BorderLayout());
        //this.panel_selectionVues.setBorder(BorderFactory.createTitledBorder(titre));
        this.panel_selectionVues.setBackground(new Color(240, 240, 240));

        this.panel_miniatures = new JPanel();
        this.panel_miniatures.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.panel_miniatures.setBackground(new Color(240, 240, 240));

        this.checkboxes = new ArrayList<>();

        for (int i = 0; i < vues.size(); i++) {
            JPanel panelVue = new JPanel();
            panelVue.setLayout(new BorderLayout());
            panelVue.setBackground(new Color(240, 240, 240));

            BufferedImage img = vues.get(i).getImage();
            BufferedImage miniature = creerMiniature(img, 100, 80);
            
            JLabel lblMiniature = new JLabel(new ImageIcon(miniature));
            
            lblMiniature.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            //JCheckBox cb = new JCheckBox(vues.get(i).getNomVue());
            //cb.addActionListener(e -> majBoutonToutSelectionnerVues());
            //cb.setBackground(new Color(240, 240, 240));
            //this.checkboxes.add(cb);
            
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
            //panelVue.add(cb, BorderLayout.SOUTH);
            this.panel_miniatures.add(panelVue);
        }

        JPanel panelBoutons = new JPanel();
        //this.btn_toutSelectionner = new JCheckBox(LangueManager.tr("panel.selectAll"));
        //this.btn_confirmerModification = new JButton(LangueManager.tr("panel.confirm"));
        //this.btn_annulerModification = new JButton(LangueManager.tr("panel.cancel"));

        this.btn_toutSelectionner.addActionListener(e -> this.onToggleSelectionnerVues());
        this.btn_confirmerModification.addActionListener(e -> this.confirmerSupressionOuExportVues());
        this.btn_annulerModification.addActionListener(e -> this.annulerSupressionOuExportVues());

        panelBoutons.add(this.btn_toutSelectionner);
        panelBoutons.add(this.btn_confirmerModification);
        panelBoutons.add(this.btn_annulerModification);

        this.panel_selectionVues.add(new JScrollPane(panel_miniatures), BorderLayout.CENTER);
        this.panel_selectionVues.add(panelBoutons, BorderLayout.SOUTH);
        this.panel_selectionVues.setPreferredSize(new Dimension(getWidth(), 180));

        this.jPanel1.add(this.panel_selectionVues, BorderLayout.SOUTH);
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
        this.btn_toutSelectionner.setSelected(tousCoches);
    }

    private void onToggleSelectionnerVues(){
        boolean etat = this.btn_toutSelectionner.isSelected();

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
        if (!indices.isEmpty() && this.actionCourante.equalsIgnoreCase("panel.delete")) {
            this.controleur.supprimerVues(indices);
        }
        else if (!indices.isEmpty() && this.actionCourante.equalsIgnoreCase("panel.export")){
            this.controleur.exporterVues(indices);
        }
        
        this.annulerSupressionOuExportVues();
        this.rafraichirVueCourante();
    }

    private void annulerSupressionOuExportVues() {
        this.jPanel1.remove(this.panel_selectionVues);
        revalidate();
        repaint();
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainWindow(new MainController()).setVisible(true));
    }
    

   //@roneh  Methode du rognage de la vue Courante 
   private void rognerVueCourante(Rectangle zoneAffichage) {
    VueDTO vue = this.controleur.getVueCourante();

    if (vue == null || vue.getImage() == null) {
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
            new RognerVueDTO(
                    zoneImage.x,
                    zoneImage.y,
                    zoneImage.width,
                    zoneImage.height
            )
    );

    this.modeRognage = false;
    this.drawingPanel.setModeRognage(false);
    //this.btn_rogner.setText(LangueManager.tr("button.crop"));

    this.rafraichirVueCourante();
}
   
   // @roneh pour le rognage inverse
    private void initialiserPanelChoixRognage() {
    this.panelChoixRognage = new JPanel(new FlowLayout(FlowLayout.CENTER));
    this.panelChoixRognage.setOpaque(false);

    this.btn_appliquerRognageNormal = new JButton("Rognage normal");
    this.btn_appliquerRognageInverse = new JButton("Rognage inverse");
    this.btn_annulerRognage = new JButton("Annuler");

    this.btn_appliquerRognageNormal.addActionListener(e -> appliquerRognageNormalEnAttente());
    this.btn_appliquerRognageInverse.addActionListener(e -> appliquerRognageInverseEnAttente());
    this.btn_annulerRognage.addActionListener(e -> annulerRognageEnAttente());

    this.panelChoixRognage.add(this.btn_appliquerRognageNormal);
    this.panelChoixRognage.add(this.btn_appliquerRognageInverse);
    this.panelChoixRognage.add(this.btn_annulerRognage);

    this.panelChoixRognage.setVisible(false);
    this.jPanel1.add(this.panelChoixRognage, BorderLayout.NORTH);
}
   
   private void preparerChoixRognage(Rectangle zoneAffichage) {
    if (zoneAffichage == null || zoneAffichage.width <= 0 || zoneAffichage.height <= 0) {
        return;
    }

    this.selectionRognageEnAttente = zoneAffichage;
    this.panelChoixRognage.setVisible(true);

    this.jPanel1.revalidate();
    this.jPanel1.repaint();
}

private void appliquerRognageNormalEnAttente() {
    if (this.selectionRognageEnAttente == null) {
        return;
    }

    Rectangle zoneAffichage = this.selectionRognageEnAttente;
    this.selectionRognageEnAttente = null;
    this.panelChoixRognage.setVisible(false);

    this.rognerVueCourante(zoneAffichage);
}

private void appliquerRognageInverseEnAttente() {
    if (this.selectionRognageEnAttente == null) {
        return;
    }

    VueDTO vue = this.controleur.getVueCourante();
    if (vue == null || vue.getImage() == null) {
        return;
    }

    if (!(this.drawingPanel instanceof DrawingPanel panel)) {
        return;
    }

    Rectangle zoneAffichage = this.selectionRognageEnAttente;
    Rectangle zoneImage = panel.convertirSelectionEcranVersImage(zoneAffichage);

    if (zoneImage == null) {
        return;
    }

    this.selectionRognageEnAttente = null;
    this.panelChoixRognage.setVisible(false);

    this.modeRognage = false;
    this.drawingPanel.setModeRognage(false);
    //this.btn_rogner.setText(LangueManager.tr("button.crop"));

    this.rafraichirVueCourante();
}

private void annulerRognageEnAttente() {
    this.selectionRognageEnAttente = null;
    this.panelChoixRognage.setVisible(false);

    this.modeRognage = false;
    this.drawingPanel.setModeRognage(false);
    //this.btn_rogner.setText(LangueManager.tr("button.crop"));

    this.jPanel1.revalidate();
    this.jPanel1.repaint();
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_authentification;
    private javax.swing.JButton btn_changerLangue;
    private javax.swing.JButton btn_dezoomer;
    private javax.swing.JButton btn_exporter;
    private javax.swing.JButton btn_importer;
    private javax.swing.JButton btn_precedent;
    private javax.swing.JButton btn_rogner;
    private javax.swing.JButton btn_suivant;
    private javax.swing.JButton btn_supprimer;
    private javax.swing.JButton btn_zoomer;
    private javax.swing.JComboBox<String> cbo_formeZone;
    private javax.swing.JComboBox<String> cbo_typeZone;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbl_echelle;
    private javax.swing.JLabel lbl_forme;
    private javax.swing.JLabel lbl_hauteurBloc;
    private javax.swing.JLabel lbl_largeurBloc;
    private javax.swing.JLabel lbl_nombreBloc;
    private javax.swing.JLabel lbl_prixParBloc;
    private javax.swing.JLabel lbl_prixTotal;
    private javax.swing.JLabel lbl_type;
    private javax.swing.JPanel panel_blocs;
    private javax.swing.JPanel panel_vues;
    private javax.swing.JPanel panel_zones;
    private javax.swing.JSpinner spi_echelle;
    private javax.swing.JTextField txt_hauteurBloc;
    private javax.swing.JTextField txt_largeurBloc;
    private javax.swing.JTextField txt_nbrTotalDeBloc;
    private javax.swing.JTextField txt_prixParBloc;
    private javax.swing.JTextField txt_prixTotal;
    // End of variables declaration//GEN-END:variables
}
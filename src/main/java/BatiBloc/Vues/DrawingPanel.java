package BatiBloc.Vues;
import BatiBloc.Controleur.MainController;
import BatiBloc.Dto.ZoneDTO; //AJouter par Emmanuella 

import BatiBloc.Dto.VueDTO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.RenderingHints; // Ajouter par Emmanuella
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List; // Ajouter par Emmanuella
import java.util.function.Consumer; //Ajouter par Emmanuella
import javax.swing.JPanel;
import javax.swing.SwingUtilities;  // @roneh
import java.awt.Cursor; //@roneh
import java.awt.geom.Point2D; //@roneh
import java.awt.event.MouseWheelEvent; //@roneh

public class DrawingPanel extends JPanel implements IPanelAffichage{

    private final MainController controller;

    private VueDTO vueCourante;
    private boolean modeRognage = false;
    private boolean modeCalibration = false;
    private boolean modeAjoutZone = false; // Ajouter par Emmanuella 


    private Point pointDepart;
    private Point pointFin;
    
    private Consumer<Rectangle> onRognageTermine; // C'est une variable qui stock une fonction qui prend un rectangle en paramètre 
    
    // @roneh Attributs pour la navigation(comportement map) et zoom
    private boolean enDeplacement = false;
    private Point dernierPointSouris;

    private double zoom = 1.0;
    private double offsetX = 0.0;
    private double offsetY = 0.0;

    private boolean vueInitialisee = false;
                                                  


    public DrawingPanel(MainController controller){ //Ajouter par Emmanuella -prend MainController en paramètre {
        this.controller = controller;
 
        setBackground(new Color(153, 153, 153));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (vueCourante == null || vueCourante.getImage() == null ) {
                    return;
                }

                // Rognage / calibration / ajout zone au clic gauche
                if (SwingUtilities.isLeftMouseButton(e)) {
                    pointDepart = e.getPoint();
                    pointFin = e.getPoint();
                    repaint();
                    return;
                }

                // Déplacement avec clic molette
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    enDeplacement = true;
                    dernierPointSouris = e.getPoint();
                    setCursor(new Cursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (vueCourante == null || vueCourante.getImage() == null) {
                    return;
                }
                                                                                   // deplacemnt avec le clic de la molette
                if (enDeplacement && dernierPointSouris != null) {
                    int dx = e.getX() - dernierPointSouris.x;
                    int dy = e.getY() - dernierPointSouris.y;

                    offsetX += dx;
                    offsetY += dy;

                    dernierPointSouris = e.getPoint();
                    repaint();
                    return;
                }

                
                if (pointDepart == null) {
                    return;
                }

                pointFin = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (vueCourante == null || vueCourante.getImage() == null) { 
                    enDeplacement = false;
                    dernierPointSouris = null;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));                            //@roneh
                    return;
                }

                if (SwingUtilities.isMiddleMouseButton(e)) {
                    enDeplacement = false;
                    dernierPointSouris = null;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
      
                if (pointDepart == null) {
                    return;
                }

                pointFin = e.getPoint();
                
                
                //Modification Emmmanuella - Mode Rognage : on va calculer le rectangle 
                // sélectionner par l'utilisateur et on envoie le rectangle à MainWIndow via le callback
                if (modeRognage && onRognageTermine != null) {
                    int x = Math.min(pointDepart.x, pointFin.x);
                    int y = Math.min(pointDepart.y, pointFin.y);
                    int w = Math.abs(pointDepart.x - pointFin.x);
                    int h = Math.abs(pointDepart.y - pointFin.y);
                    onRognageTermine.accept(new Rectangle(x, y, w, h));
                    
                } else if (modeCalibration) {
                    double distance = pointDepart.distance(pointFin);
                    //TODO : Il faudrait le connecter à CalibrerEchelle() plus tard
                    
                } else if (modeAjoutZone) {
                    /*controller.ajouterZoneRectangle(pointDepart, pointFin, true);*/
                }
                
                pointDepart = null;
                pointFin = null;
                repaint();
            }
            
             @Override  // roneh
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (vueCourante == null || vueCourante.getImage() == null) {
                    return;
                }

                double facteur = (e.getPreciseWheelRotation() < 0) ? 1.1 : (1.0 / 1.1);
                appliquerZoom(facteur, e.getX(), e.getY());
            }
        };
        
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler); // @roneh
        
        //@roneh
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (vueCourante != null && vueCourante.getImage() != null && !vueInitialisee) {
                    ajusterImageAuPanel();
                    vueInitialisee = true;
                    repaint();
                }
            }
        });
    }

    @Override
    public void afficherVue(VueDTO vue) {
        boolean nouvelleVue = this.vueCourante != vue;
        this.vueCourante = vue;
        
        if (nouvelleVue) {
            this.pointDepart = null;
            this.pointFin = null;
            this.enDeplacement = false;
            this.dernierPointSouris = null;

            this.vueInitialisee = false;
            ajusterImageAuPanel();
            this.vueInitialisee = true;
        }

        repaint();
    }
    
    @Override
    public void setOnRognageTermine(Consumer<Rectangle> callback) {
        this.onRognageTermine = callback;
    }

    @Override
    public void setModeRognage(boolean actif) {
        
        //Modification par Emmanuella
        this.modeRognage = actif;
        this.modeCalibration = false;
        this.modeAjoutZone = false;
            repaint();
        }
    

      
      //Emmanuella - Ajout d'une nouvelle fonction
      // Permet à l'utilisateur au logiciel d'estimer l'echelle 
      // du plan lorsque l'utilisateur forme un zone par exemple 

      
    @Override
    public void setModeCalibration(boolean actif){
      this.modeCalibration = actif;
      this.modeRognage = false;
      this.modeAjoutZone = false;
      repaint();
    }

      
    @Override
    public void setModeAjoutZone(boolean actif) {
      this.modeAjoutZone = actif;
      this.modeRognage = false;
      this.modeCalibration = false;
      repaint();
    }
    
    @Override
    public void zoomInCenter() {
        appliquerZoom(1.1, getWidth() / 2.0, getHeight() / 2.0);
    }

    @Override
    public void zoomOutCenter() {
        appliquerZoom(1.0 / 1.1, getWidth() / 2.0, getHeight() / 2.0);
    }
    
     public Rectangle convertirSelectionEcranVersImage(Rectangle zoneEcran) {
        if (vueCourante == null || vueCourante.getImage() == null || zoneEcran == null) {
            return null;
        }

        Point2D p1 = screenToImage(zoneEcran.x, zoneEcran.y);
        Point2D p2 = screenToImage(zoneEcran.x + zoneEcran.width, zoneEcran.y + zoneEcran.height);

        int x = (int) Math.round(Math.min(p1.getX(), p2.getX()));
        int y = (int) Math.round(Math.min(p1.getY(), p2.getY()));
        int w = (int) Math.round(Math.abs(p2.getX() - p1.getX()));
        int h = (int) Math.round(Math.abs(p2.getY() - p1.getY()));

        int imageWidth = vueCourante.getImage().getWidth();
        int imageHeight = vueCourante.getImage().getHeight();

        x = Math.max(0, x);
        y = Math.max(0, y);

        if (x + w > imageWidth) {
            w = imageWidth - x;
        }

        if (y + h > imageHeight) {
            h = imageHeight - y;
        }

        if (w <= 0 || h <= 0) {
            return null;
        }

        return new Rectangle(x, y, w, h);
    }

    private void ajusterImageAuPanel() {
        if (vueCourante == null || vueCourante.getImage() == null) {
            return;
        }

        int imageWidth = vueCourante.getImage().getWidth();
        int imageHeight = vueCourante.getImage().getHeight();
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (imageWidth <= 0 || imageHeight <= 0 || panelWidth <= 0 || panelHeight <= 0) {
            return;
        }

        double scaleX = (double) panelWidth / imageWidth;
        double scaleY = (double) panelHeight / imageHeight;

        zoom = Math.min(scaleX, scaleY);

        offsetX = (panelWidth - imageWidth * zoom) / 2.0;
        offsetY = (panelHeight - imageHeight * zoom) / 2.0;
    }

    private void appliquerZoom(double facteur, double pivotEcranX, double pivotEcranY) {
        if (vueCourante == null || vueCourante.getImage() == null) {
            return;
        }

        double ancienZoom = zoom;
        double nouveauZoom = zoom * facteur;

        nouveauZoom = Math.max(0.000001, Math.min(nouveauZoom, 1000000.0));

        Point2D pointImageAvant = screenToImage(
                pivotEcranX,
                pivotEcranY,
                ancienZoom,
                offsetX,
                offsetY
        );

        zoom = nouveauZoom;

        offsetX = pivotEcranX - pointImageAvant.getX() * zoom;
        offsetY = pivotEcranY - pointImageAvant.getY() * zoom;

        repaint();
    }

    public Point2D screenToImage(double screenX, double screenY) {
        return screenToImage(screenX, screenY, zoom, offsetX, offsetY);
    }

    private Point2D screenToImage(
            double screenX,
            double screenY,
            double currentZoom,
            double currentOffsetX,
            double currentOffsetY
    ) {
        double imageX = (screenX - currentOffsetX) / currentZoom;
        double imageY = (screenY - currentOffsetY) / currentZoom;
        return new Point2D.Double(imageX, imageY);
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.vueCourante == null || this.vueCourante.getImage() == null) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        //Dessiner le PDF
        g2d.translate(offsetX, offsetY);  //@roneh
        g2d.scale(zoom, zoom); //@roneh
        g2d.drawImage(this.vueCourante.getImage(), 0, 0, this);  // modifier par @roneh en supprimant {getWidth() et getHeignt()}

        // 2. Dessiner les zones        
        //Activer cette aprtie lorsque ZoneDTO sera implementé
        /*List<ZoneDTO> zones = this.vueCourante.getZones();
        for (ZoneDTO zone : zones) {
            g2d.setColor(new Color(34, 139, 34, 100));
            g2d.fill(zone.getFormeAEcran());
            g2d.setColor(new Color(34, 139, 34));
            g2d.draw(zone.getFormeAEcran());
        }*/
        g2d.dispose();

        // 3. Dessiner l'action en cours
        if (pointDepart != null && pointFin != null) {
            Graphics2D overlay = (Graphics2D) g.create();

            
            if (modeCalibration) {
                overlay.setColor(Color.BLUE);
                overlay.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[]{5}, 0));
                overlay.drawLine(pointDepart.x, pointDepart.y, pointFin.x, pointFin.y);
            } else {
                overlay.setColor(Color.RED);
                int x = Math.min(pointDepart.x, pointFin.x);
                int y = Math.min(pointDepart.y, pointFin.y);
                int w = Math.abs(pointDepart.x - pointFin.x);
                int h = Math.abs(pointDepart.y - pointFin.y);
                overlay.drawRect(x, y, w, h);
            }
            overlay.dispose();  
        }

    }
}
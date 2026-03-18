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

public class DrawingPanel extends JPanel {

    private final MainController controller;

    private VueDTO vueCourante;
    private boolean modeRognage = false;
    private boolean modeCalibration = false;
    private boolean modeAjoutZone = false; // Ajouter par Emmanuella 


    private Point pointDepart;
    private Point pointFin;
    
    private Consumer<Rectangle> onRognageTermine; // C'est une variable qui stock une fonction qui prend un rectangle en paramètre 
                                                  


    public DrawingPanel(MainController controller) //Ajouter par Emmanuella -prend MainController en paramètre {
        this.controller = controller;
 
        setBackground(new Color(153, 153, 153));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (vueCourante == null ) {
                    return;
                }

                pointDepart = e.getPoint();
                pointFin = e.getPoint();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (pointDepart == null) {
                    return;
                }

                pointFin = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
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
                    
                } else if (modeAjoutZone) {
                    controller.ajouterZoneRectangle(pointDepart, pointFin, true);
                }
                
                pointDepart = null;
                pointFin = null;
                repaint();
            }
        };
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    public void afficherVue(VueDTO vue) {
        this.vueCourante = vue;
        repaint();
    }
    
    public void setOnRognageTermine(Consumer<Rectangle> callback) {
        this.onRognageTermine = callback;
    }


    public void setModeRognage(Consumer<Rectangle> callback) {
        this.onRognageTermine = callback;

        //Modification par Emmanuella
        this.modeRognage = actif;
        this.modeCalibration = false;
        this.modeAjoutZone = false;
            repaint();
        }
    }

      
      //Emmanuella - Ajout d'une nouvelle fonction
      // Permet à l'utilisateur au logiciel d'estimer l'echelle 
      // du plan lorsque l'utilisateur forme un zone par exemple 

      public void setModeCalibration(boolean actif) {
        this.modeCalibration = actif;
        this.modeRognage = false;
        this.modeAjoutZone = false;
        repaint();
    }

      
      public void setModeAjoutZone(boolean actif) {
        this.modeAjoutZone = actif;
        this.modeRognage = false;
        this.modeCalibration = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.vueCourante != null && this.vueCourante.getImage() != null) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Dessiner le PDF
        g.drawImage(this.vueCourante.getImage(), 0, 0, getWidth(), getHeight(), this);

        // 2. Dessiner les zones
        List<ZoneDTO> zones = this.vueCourante.getZones();
        for (ZoneDTO zone : zones) {
            g2d.setColor(new Color(34, 139, 34, 100));
            g2d.fill(zone.getFormeAEcran());
            g2d.setColor(new Color(34, 139, 34));
            g2d.draw(zone.getFormeAEcran());
        }

        // 3. Dessiner l'action en cours
        if (pointDepart != null && pointFin != null) {
            if (modeCalibration) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[]{5}, 0));
                g2d.drawLine(pointDepart.x, pointDepart.y, pointFin.x, pointFin.y);
            } else {
                g2d.setColor(Color.RED);
                int x = Math.min(pointDepart.x, pointFin.x);
                int y = Math.min(pointDepart.y, pointFin.y);
                int w = Math.abs(pointDepart.x - pointFin.x);
                int h = Math.abs(pointDepart.y - pointFin.y);
                g2d.drawRect(x, y, w, h);
            }
        }

        g2d.dispose();
    }
}
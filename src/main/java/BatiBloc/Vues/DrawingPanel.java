package BatiBloc.Vues;

import BatiBloc.Dto.VueDTO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class DrawingPanel extends JPanel {

    private VueDTO vueCourante;
    private boolean modeRognage = false;
    private Point pointDepart;
    private Point pointFin;
    private Rectangle selection;
    private RognageListener rognageListener;

    public DrawingPanel() {
        setBackground(new Color(153, 153, 153));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!modeRognage || vueCourante == null || vueCourante.getImage() == null) {
                    return;
                }

                pointDepart = e.getPoint();
                pointFin = e.getPoint();
                selection = new Rectangle(pointDepart);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!modeRognage || pointDepart == null) {
                    return;
                }

                pointFin = e.getPoint();
                updateSelection();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!modeRognage || pointDepart == null) {
                    return;
                }

                pointFin = e.getPoint();
                updateSelection();
                repaint();

                if (rognageListener != null && selection != null
                        && selection.width > 0 && selection.height > 0) {
                    rognageListener.onZoneSelectionnee(selection);
                }

                pointDepart = null;
                pointFin = null;
            }
        };
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    public void afficherVue(VueDTO vue) {
        this.vueCourante = vue;
        this.selection = null;
        repaint();
    }

    public void setModeRognage(boolean modeRognage) {
        this.modeRognage = modeRognage;

        if (!modeRognage) {
            this.selection = null;
            this.pointDepart = null;
            this.pointFin = null;
            repaint();
        }
    }

    public void setRognageListener(RognageListener rognageListener) {
        this.rognageListener = rognageListener;
    }

    private void updateSelection() {
        int x = Math.min(this.pointDepart.x, this.pointFin.x);
        int y = Math.min(this.pointDepart.y, this.pointFin.y);
        int largeur = Math.abs(this.pointDepart.x - this.pointFin.x);
        int hauteur = Math.abs(this.pointDepart.y - this.pointFin.y);

        this.selection = new Rectangle(x, y, largeur, hauteur);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.vueCourante != null && this.vueCourante.getImage() != null) {
            g.drawImage(this.vueCourante.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        if (this.modeRognage && this.selection != null) {
            Graphics2D graphique2D = (Graphics2D) g.create();
            graphique2D.setColor(Color.RED);
            graphique2D.setStroke(new BasicStroke(2));
            graphique2D.draw(this.selection);
            graphique2D.dispose();
        }
    }

    public interface RognageListener {
        void onZoneSelectionnee(Rectangle zone);
    }
}
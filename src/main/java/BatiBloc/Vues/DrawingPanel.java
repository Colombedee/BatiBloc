/*
 *Class Drawing Panel
 * @author Emmanuella Iris Andréa Lehe
 * crée le 06 mars 2026 à 12h52
 * puis modifier/changer par @author roneh le 07 mars 2026
 */

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
        setBackground(new java.awt.Color(153, 153, 153));

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

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void afficherVue(VueDTO vue) {
        this.vueCourante = vue;
        this.selection = null;
        repaint();
    }

    public void setModeRognage(boolean modeRognage) {
        this.modeRognage = modeRognage;

        if (!modeRognage) {
            selection = null;
            pointDepart = null;
            pointFin = null;
            repaint();
        }
    }

    public void setRognageListener(RognageListener rognageListener) {
        this.rognageListener = rognageListener;
    }

    private void updateSelection() {
        int x = Math.min(pointDepart.x, pointFin.x);
        int y = Math.min(pointDepart.y, pointFin.y);
        int width = Math.abs(pointDepart.x - pointFin.x);
        int height = Math.abs(pointDepart.y - pointFin.y);

        selection = new Rectangle(x, y, width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (vueCourante != null && vueCourante.getImage() != null) {
            g.drawImage(vueCourante.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        if (modeRognage && selection != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            g2.draw(selection);
            g2.dispose();
        }
    }

    public interface RognageListener {
        void onZoneSelectionnee(Rectangle zone);
    }
}
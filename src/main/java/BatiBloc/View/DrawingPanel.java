/*
 *Class Drawing Panel
 * @author Emmanuella Iris Andréa Lehe
 * crée le 06 mars 2026 à 12h52
 */

package BatiBloc.View;

import BatiBloc.Dto.VueDTO;
import java.awt.Graphics;          
import javax.swing.JPanel;

public class DrawingPanel extends JPanel {

    private VueDTO vueCourante;
    
    public DrawingPanel(){
        setBackground(new java.awt.Color(153, 153, 153));
    }

    public void afficherVue(VueDTO vue) {
        this.vueCourante = vue;
        repaint();
    }

    @Override                       
    protected void paintComponent(Graphics g) {   
        super.paintComponent(g);
        if (vueCourante != null && vueCourante.getImage() != null) {
            g.drawImage(vueCourante.getImage(), 0, 0,
                        getWidth(), getHeight(), this);
        }
    }
}

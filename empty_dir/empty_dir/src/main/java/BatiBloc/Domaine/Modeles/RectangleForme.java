package BatiBloc.Domaine.Modeles;

import java.awt.Point;
import java.util.List;

public class RectangleForme extends Forme {

    public RectangleForme(List<Point> listePoints) {
        super(listePoints);
        if (listePoints.size() != 4) {
            throw new IllegalArgumentException("Un rectangle doit avoir exactement 4 sommets.");
        }
    }

    @Override
    public double getLargeur() {
        double minX = this.position.stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = this.position.stream().mapToDouble(p -> p.x).max().orElse(0);
        return maxX - minX;
    }

    @Override
    public double getHauteur() {
        double minY = this.position.stream().mapToDouble(p -> p.y).min().orElse(0);
        double maxY = this.position.stream().mapToDouble(p -> p.y).max().orElse(0);
        return maxY - minY;
    }

    @Override
    public double getAire() {
        return this.getLargeur() * this.getHauteur();
    }

    @Override
    public boolean contientPoint(Point p) {
        double minX = this.position.stream().mapToDouble(pt -> pt.x).min().orElse(0);
        double maxX = this.position.stream().mapToDouble(pt -> pt.x).max().orElse(0);
        double minY = this.position.stream().mapToDouble(pt -> pt.y).min().orElse(0);
        double maxY = this.position.stream().mapToDouble(pt -> pt.y).max().orElse(0);

        return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
    }
}
/**
 *
 * @author roneh
 */

package BatiBloc.Domaine.Controleur;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UndoRedoManager<T> {

    private final Deque<T> pileAnnulation = new ArrayDeque<>();
    private final Deque<T> pileRetablissement = new ArrayDeque<>();
    private boolean modificationComposeeEnCours = false;
    private boolean etatComposeDejaSauvegarde = false;

    public void commencerModificationComposee() {
        this.modificationComposeeEnCours = true;
        this.etatComposeDejaSauvegarde = false;
    }

    public void terminerModificationComposee() {
        this.modificationComposeeEnCours = false;
        this.etatComposeDejaSauvegarde = false;
    }

    // enregistre l'état courant avant une modification réelle.
    public void enregistrerEtatAvantModification(Supplier<T> createurEtat) {
        if (this.modificationComposeeEnCours) {
            if (this.etatComposeDejaSauvegarde) {
                return;
            }
            this.etatComposeDejaSauvegarde = true;
        }

        this.pileAnnulation.push(createurEtat.get());
        this.pileRetablissement.clear();
    }

    public void annuler(Supplier<T> createurEtatCourant, Consumer<T> restaurateurEtat) {
        if (this.pileAnnulation.isEmpty()) {
            return;
        }

        this.pileRetablissement.push(createurEtatCourant.get());
        restaurateurEtat.accept(this.pileAnnulation.pop());
        this.terminerModificationComposee();
    }

    public void retablir(Supplier<T> createurEtatCourant, Consumer<T> restaurateurEtat) {
        if (this.pileRetablissement.isEmpty()) {
            return;
        }

        this.pileAnnulation.push(createurEtatCourant.get());
        restaurateurEtat.accept(this.pileRetablissement.pop());
        this.terminerModificationComposee();
    }
}


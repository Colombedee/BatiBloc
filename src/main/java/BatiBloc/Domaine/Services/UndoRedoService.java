package BatiBloc.Domaine.Services;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UndoRedoService<T> {

    private final Deque<T> pileAnnulation = new ArrayDeque<>();
    private final Deque<T> pileRetablissement = new ArrayDeque<>();
    private boolean modificationComposeeEnCours = false;
    private boolean etatComposeDejaSauvegarde = false;


    public void modificationComposee() {
        if (!this.modificationComposeeEnCours) {
            this.modificationComposeeEnCours = true;
            this.etatComposeDejaSauvegarde = false;
        } else {
            this.modificationComposeeEnCours = false;
            this.etatComposeDejaSauvegarde = false;
        }
    }


    public void commencerModificationComposee(Supplier<T> createurEtat) {
        if (this.modificationComposeeEnCours) {
            return;
        }

        this.modificationComposeeEnCours = true;
        this.etatComposeDejaSauvegarde = false;

        if (createurEtat != null) {
            this.pileAnnulation.push(createurEtat.get());
            this.pileRetablissement.clear();
            this.etatComposeDejaSauvegarde = true;
        }
    }


    public void terminerModificationComposee() {
        this.modificationComposeeEnCours = false;
        this.etatComposeDejaSauvegarde = false;
    }

    public void enregistrerEtatAvantModification(Supplier<T> createurEtat) {
        if (createurEtat == null) {
            return;
        }

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
        if (this.pileAnnulation.isEmpty() || createurEtatCourant == null || restaurateurEtat == null) {
            return;
        }

        this.pileRetablissement.push(createurEtatCourant.get());
        restaurateurEtat.accept(this.pileAnnulation.pop());

        this.modificationComposeeEnCours = false;
        this.etatComposeDejaSauvegarde = false;
    }

    public void retablir(Supplier<T> createurEtatCourant, Consumer<T> restaurateurEtat) {
        if (this.pileRetablissement.isEmpty() || createurEtatCourant == null || restaurateurEtat == null) {
            return;
        }

        this.pileAnnulation.push(createurEtatCourant.get());
        restaurateurEtat.accept(this.pileRetablissement.pop());

        this.modificationComposeeEnCours = false;
        this.etatComposeDejaSauvegarde = false;
    }

    public boolean canUndo() {
        return !this.pileAnnulation.isEmpty();
    }

    public boolean canRedo() {
        return !this.pileRetablissement.isEmpty();
    }
}

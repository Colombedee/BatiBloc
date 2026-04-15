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
        this.modificationComposeeEnCours = !this.modificationComposeeEnCours;
        this.etatComposeDejaSauvegarde = false;
    }

    public void enregistrerEtatAvantModification(Supplier<T> createurEtat) {
        if (this.modificationComposeeEnCours) {
            if (this.etatComposeDejaSauvegarde) return;
            
            this.etatComposeDejaSauvegarde = true;
        }

        this.pileAnnulation.push(createurEtat.get());
        this.pileRetablissement.clear();
    }

    public void annuler(Supplier<T> createurEtatCourant, Consumer<T> restaurateurEtat) {
        if (this.pileAnnulation.isEmpty()) return;

        this.pileRetablissement.push(createurEtatCourant.get());
        restaurateurEtat.accept(this.pileAnnulation.pop());
        this.modificationComposee();
    }

    public void retablir(Supplier<T> createurEtatCourant, Consumer<T> restaurateurEtat) {
        if (this.pileRetablissement.isEmpty()) return;

        this.pileAnnulation.push(createurEtatCourant.get());
        restaurateurEtat.accept(this.pileRetablissement.pop());
        this.modificationComposee();
    }
}
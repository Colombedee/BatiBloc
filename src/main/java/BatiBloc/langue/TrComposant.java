package BatiBloc.langue;

import BatiBloc.Services.Observers.LangueObserver;
import java.lang.reflect.Method;

public class TrComposant<T> implements LangueObserver {

    private final T composant;
    private final String cle;
    private final Method setTextMethod;

    public TrComposant(T composant, String cle) {
        this.composant = composant;
        this.cle = cle;

        try {
            this.setTextMethod = this.composant.getClass().getMethod("setText", String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Le composant n'a pas de méthode setText(String)");
        }

        this.onLangueChanged();
        LangueManager.addObserver(this);
    }

    @Override
    public void onLangueChanged() {
        try {
            String translated = LangueManager.tr(this.cle);
            this.setTextMethod.invoke(this.composant, translated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
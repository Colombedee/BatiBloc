package BatiBloc.langue;

import java.lang.reflect.Method;

public class TrComponent<T> implements LangueObserver {

    private final T component;
    private final String key;
    private final Method setTextMethod;

    public TrComponent(T component, String key) {
        this.component = component;
        this.key = key;

        try {
            this.setTextMethod = component.getClass().getMethod("setText", String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Le composant n'a pas de méthode setText(String)");
        }

        updateText();
        Langue.addObserver(this);
    }

    private void updateText() {
        try {
            setTextMethod.invoke(component, Langue.tr(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLangueChanged() {
        updateText();
    }
}
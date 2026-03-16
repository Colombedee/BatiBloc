package BatiBloc.langue;

import BatiBloc.Services.Observers.LangueObserver;
import java.util.*;

public class LangueManager {

    private static Langage langageCourant = Langage.FR;
    private static final List<LangueObserver> observers = new ArrayList<>();
    private static ResourceBundle bundle;

    static {
        loadBundle();
    }

    public static void addObserver(LangueObserver obs) {
        observers.add(obs);
    }

    public static Langage getLangageCourant() {
        return langageCourant;
    }

    public static void setLangage(Langage langage) {
        langageCourant = langage;
        loadBundle();
        notifyObservers();
    }

    public static String tr(String cle) {
        try {
            return bundle.getString(cle);
        } catch (MissingResourceException e) {
            return "??" + cle + "??";
        }
    }

    private static void loadBundle() {
        String nomBase = "i18n.messages";
        Locale locale = (langageCourant == Langage.FR) ? Locale.FRENCH : Locale.ENGLISH;
        bundle = ResourceBundle.getBundle(nomBase, locale);
    }

    private static void notifyObservers() {
        for (LangueObserver obs : observers) {
            obs.onLangueChanged();
        }
    }
}
package BatiBloc.langue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Langue {

    public enum Langage { FR, EN }

    private static Langage langageCourant = Langage.FR;
    private static final Map<String, Map<Langage, String>> translations = new HashMap<>();
    private static final List<LangueObserver> observers = new ArrayList<>();

    static {
        add("button.import", "Importer", "Import");
        add("button.export", "Exporter", "Export");
        add("button.changeLanguage", "FR", "EN");
        add("button.delete", "Supprimer", "Delete");
        add("button.crop", "Rogner", "Crop");

        add("combo.type.classicFrame", "Armature classique", "Classic frame");
        add("combo.type.reinforcementBlocks", "Armatures en blocs", "Reinforcement Blocks");
        add("combo.type.opening", "Ouverture", "Opening");

        add("combo.form.rectangle", "Rectangle", "Rectangle");
        add("combo.form.triangle", "Triangle", "Triangle");
        add("combo.form.truncatedTriangle", "Triangle tronqué", "Truncated Triangle");

        add("panel.view", "Vues", "Views");
        add("label.scale", "Échelle" + " :", "Scale" + " :");
        add("panel.area", "Zones", "Areas");
        add("label.type", "Type" + " :", "Type" + " :");
        add("label.form", "Forme" + " :", "Form" + " :");
        add("panel.block", "Blocs", "Blocks");
        add("label.width", "Largeur" + " :", "Width" + " :");
        add("label.height", "Hauteur" + " :", "Height" + " :");
        add("label.pricePerBloc", "Prix par bloc" + " :", "Price per block" + " :");
        add("label.numberBlock", "Nombre de blocs" + " :", "Number of blocks" + " :");
        add("label.totalPrice", "Prix total" + " :", "Total price" + " :");
    }

    private static void add(String key, String fr, String en) {
        Map<Langage, String> map = new HashMap<>();
        map.put(Langage.FR, fr);
        map.put(Langage.EN, en);
        translations.put(key, map);
    }

    public static void addObserver(LangueObserver obs) {
        observers.add(obs);
    }

    private static void notifyObservers() {
        for (LangueObserver obs : observers) {
            obs.onLangueChanged();
        }
    }

    public static Langage getLangageCourant() {
        return langageCourant;
    }

    public static void setLangage(Langage langage) {
        langageCourant = langage;
        notifyObservers();
    }

    public static String tr(String key) {
        Map<Langage, String> map = translations.get(key);
        if (map == null) return "??" + key + "??";
        return map.getOrDefault(langageCourant, "??" + key + "??");
    }
}
package BatiBloc.langue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class langue {

    private static Locale courant = Locale.FRENCH;

    private static final Map<String, String> fr = new HashMap<>();
    private static final Map<String, String> en = new HashMap<>();

    static {
        // FR
        fr.put("app.title",      "BatiBloc");
        fr.put("menu.file",      "Fichier");
        fr.put("menu.file.exit", "Quitter");
        fr.put("button.import",  "Importer");
        fr.put("button.crop",    "Rogner");
        fr.put("label.views",    "Vues");
        fr.put("label.zones",    "Zones");

        // EN
        en.put("app.title",      "BatiBloc");
        en.put("menu.file",      "File");
        en.put("menu.file.exit", "Exit");
        en.put("button.import",  "Import");
        en.put("button.crop",    "Crop");
        en.put("label.views",    "Views");
        en.put("label.zones",    "Zones");
    }

    public static void setLocale(Locale locale) {
        courant = locale;
    }

    public static String tr(String key) {
        Map<String, String> dict =
                (Locale.ENGLISH.getLanguage().equals(courant.getLanguage())) ? en : fr;
        return dict.getOrDefault(key, key);
    }
}


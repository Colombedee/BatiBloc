package BatiBloc.Domaine.Dto;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public final class VueDTO {

    private final BufferedImage image;
    private final double echelle;
    private final int index;
    private final List<ZoneDTO> zones;

    public VueDTO(BufferedImage image, double echelle, int index, List<ZoneDTO> zones) {
        this.image = image;
        this.echelle = echelle;
        this.index = index;
        this.zones = zones != null ? List.copyOf(zones) : List.of();
    }

    public int getIndex() {
        return this.index;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public List<ZoneDTO> getZones() {
        return this.zones;
    }

    public double getEchelle() {
        return this.echelle;
    }

    public String getNomVue(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
        return MessageFormat.format(
                bundle.getString("panel.viewName"),
                this.index + 1
        );
    }
}
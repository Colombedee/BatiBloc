package BatiBloc.Dto;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import BatiBloc.langue.LangueManager;

public class VueDTO {

    private final int index;
    private final BufferedImage image;

    //Construsteur -- Décocher quand ZoneDTO sera implémenter
    public VueDTO(int index, BufferedImage image /*,List<ZoneDTO> zones*/) {
        this.index = index;
        this.image = image;
        //this.zones = zones;//
    }

    public String getNomVue() {
        return MessageFormat.format(
            LangueManager.tr("panel.viewName"),
            this.index + 1
        );
    }

    public BufferedImage getImage() {
        return this.image;
    }
}
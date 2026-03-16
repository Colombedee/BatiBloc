package BatiBloc.Dto;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import BatiBloc.langue.LangueManager;

public class VueDTO {

    private final int index;
    private final BufferedImage image;

    public VueDTO(int index, BufferedImage image) {
        this.index = index;
        this.image = image;
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
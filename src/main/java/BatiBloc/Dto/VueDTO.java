package BatiBloc.Dto;

import java.awt.image.BufferedImage;

public class VueDTO {
    
    private final String nomVue;
    private final BufferedImage image;

    public VueDTO(String nomVue, BufferedImage image) {
        this.nomVue = nomVue;
        this.image = image;
    }

    public String getNomVue() {
        return nomVue;
    }

    public BufferedImage getImage() {
        return image;
    }
}
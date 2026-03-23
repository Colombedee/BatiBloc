package BatiBloc.Dto;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.List;
import BatiBloc.langue.LangueManager;

public class VueDTO 
{
    private final int index;
    private final BufferedImage image;
    private final List<ZoneDTO> zones;

    public VueDTO(int index, BufferedImage image, List<ZoneDTO> zones) 
    {
        this.index = index;
        this.image = image;
        this.zones = zones != null ? List.copyOf(zones) : List.of();
    }

    public int getIndex()
    {
        return this.index;
    }

    public String getNomVue() 
    {
        return MessageFormat.format(
            LangueManager.tr("panel.viewName"),
            this.index + 1
        );
    }

    public BufferedImage getImage() 
    {
        return this.image;
    }
    
    public List<ZoneDTO> getZones()
    {
        return this.zones;
    }
}
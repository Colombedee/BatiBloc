package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.ProjetDTO;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 *
 * @author nicky
 */
public final class ProjetPersistenceService {

    private ProjetPersistenceService() {}

    public static void sauvegarder(ProjetDTO projet, File fichier) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fichier))) {
            out.writeObject(projet);
        }
    }

    public static ProjetDTO charger(File fichier) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fichier))) {
            return (ProjetDTO) in.readObject();
        }
    }

    public static byte[] imageVersBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    public static BufferedImage bytesVersImage(byte[] data) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            return ImageIO.read(bais);
        }
    }
}


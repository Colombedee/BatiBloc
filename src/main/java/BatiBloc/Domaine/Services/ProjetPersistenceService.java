package BatiBloc.Domaine.Services;

import BatiBloc.Domaine.Dto.ProjetDTO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;
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
        if (image == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    public static BufferedImage bytesVersImage(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            return ImageIO.read(bais);
        }
    }
}

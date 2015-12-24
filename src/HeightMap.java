import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class HeightMap {
    private long[][] heightMap = null;

    public HeightMap(int width, int height) {
        this.heightMap = new long[width][height];
    }

    public HeightMap(String filename) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(filename));
        }
        catch (IOException exception) {
            
        }

        this.heightMap = new long[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getRGB(x, y);
                // Store red channel as height
                this.setHeight(x, y, (long)(color >> 16));
            }
        }
    }

    public void setHeight(int x, int y, long height) {
        // @todo Do some sanity checks and throw exception
        this.heightMap[x][y] = height;
    }

    public long getHeight(int x, int y) {
        // @todo Do some sanity checks and throw exception
        return this.heightMap[x][y];
    }
}

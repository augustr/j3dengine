package gfx;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class HeightMap {
    private float[][] heightMap = null;
    private int       width     = 0;
    private int       height    = 0;

    public HeightMap(int width, int height) {
        this.heightMap = new float[width][height];
    }

    public HeightMap(String filename) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(filename));
        }
        catch (IOException exception) {
            
        }

        this.width  = image.getWidth();
        this.height = image.getHeight();

        this.heightMap = new float[this.width][this.height];

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                int color = image.getRGB(x, y);
                // Store red channel as Z value
                this.setZ(x, y, (float)((color & 0xff0000) >> 16));
            }
        }
    }

    public void setZ(int x, int y, float height) {
        // @todo Do some sanity checks and throw exception
        this.heightMap[x][y] = height;
    }

    public float getZ(int x, int y) {
        // @todo Do some sanity checks and throw exception
        return this.heightMap[x][y];
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

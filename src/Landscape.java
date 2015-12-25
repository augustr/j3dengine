import gfx.*;

public class Landscape implements IRenderable {
    LandscapeSection[][] landscapeSections = null;

    public Landscape(String filename) {
        HeightMap heightMap = new HeightMap(filename);
    }

    private void createVertexBuffer(HeightMap heightMap) {

    }

    public void render() {

    }
}

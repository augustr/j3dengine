import gfx.*;

public class Landscape {
    LandscapeSection[][] landscapeSections = null;
    private Surface      surface           = null;
    private Material     grassMaterial     = null;
    private Material     stoneMaterial     = null;
    private Material     sandMaterial      = null;

    public Landscape(Renderer renderer, String filename) {
        HeightMap heightMap = new HeightMap(filename);

        this.surface = renderer.createSurface();

        try {
            this.grassMaterial = renderer.createMaterial("../../res/GX_10_Grass_05.jpg");
            this.stoneMaterial = renderer.createMaterial("../../res/rock5_512.bmp");
            this.sandMaterial  = renderer.createMaterial("../../res/out_snd2c_2.BMP");

            this.surface.setMaterialDefault(this.grassMaterial);
            this.surface.setMaterialSlope(this.stoneMaterial, 0.5f);
            this.surface.setMaterialLow(this.sandMaterial, 0.1f);
        }
        catch (java.io.IOException exception) {
            System.out.println("Material not found");
        }

        this.surface.initialize(heightMap.getWidth(), heightMap.getHeight(), heightMap.getHeightMapArray());
    }

    public void render() {
        this.surface.render();
    }
}

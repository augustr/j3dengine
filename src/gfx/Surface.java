package gfx;

public interface Surface {
    void setMaterialDefault(Material material);
    void setMaterialSlope(Material material, float slope);
    void setMaterialLow(Material material, float elavation);
    void initialize(int width, int height, float[][] heightMap);
    void render();
}

package gfx;

public abstract class AbstractSprite {
    public AbstractSprite(AbstractRenderer renderer, String filename, int width, int height) { };

    public abstract void render(float x, float y);
    public abstract void render(float x, float y, float alpha, float rotation, float scaleX, float scaleY);

    private Rectangle rect;
}

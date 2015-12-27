package gfx;

import java.lang.String;

public interface Sprite {
    void initialize(Renderer renderer, String filename, int width, int height);
    void render(float x, float y);
    void render(float x, float y, float alpha, float rotation, float scaleX, float scaleY);
}

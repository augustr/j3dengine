package gfx;

import java.lang.String;

public interface Renderer {
    void        initialize(String title, int width, int height);
    void        addAnimateCallbackListener(AnimateCallbackListener listener);
    void        dispose();
    void        beginRender();
    void        endRender();
    Sprite      createSprite(String filename, int width, int height);
    Material    createMaterial(String filename) throws java.io.IOException;
    Surface     createSurface();
}
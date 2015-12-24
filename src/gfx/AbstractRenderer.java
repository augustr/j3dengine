package gfx;

import java.lang.String;
import java.awt.*;

public abstract class AbstractRenderer {
    public AbstractRenderer(int width, int height) { };

    public abstract void           addRenderThreadCallbackListener(IAbstractRenderThreadCallbackListener listener);
    public abstract void           close();
    public abstract AbstractSprite createSprite(String filename, int width, int height);
    public abstract void           beginRender();
    public abstract void           endRender();
}
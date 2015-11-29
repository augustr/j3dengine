package gfx;

import java.lang.String;

import java.awt.*;

/**
 * Created by august on 2015-04-05.
 */
public abstract class AbstractRenderer extends Canvas
{
    public AbstractRenderer(int width, int height) { super(); };
    public abstract void close();

    public abstract AbstractSprite createSprite(String filename, int width, int height);

    public abstract void beginRender();
    public abstract void endRender();
}
package gfx.opengl;

import gfx.*;

/**
 * Created by august on 2015-04-05.
 */
public class OpenGLRenderer extends AbstractRenderer
{
    public OpenGLRenderer(int width, int height)
    {
        super(width, height);
        this.setupOpenGL(width, height);
    }

    protected void setupOpenGL(int width, int height)
    {

    }

    public void close()
    {

    }

    public AbstractSprite createSprite(String filename, int width, int height)
    {
        return new OpenGLSprite(this, filename, width, height);
    }

    public void beginRender()
    {
        System.out.println("beginRender");

        System.out.println("glClear");
    }

    public void endRender()
    {
        System.out.println("endRender");
    }
}

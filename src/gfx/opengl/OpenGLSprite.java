package gfx.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import gfx.*;

/**
 * Created by august on 2015-04-05.
 */
public class OpenGLSprite extends AbstractSprite
{
    protected OpenGLRenderer renderer = null;

    public OpenGLSprite(OpenGLRenderer renderer, String filename, int width, int height)
    {
        super(renderer, filename, width, height);
        this.renderer = renderer;
    }

    public void close()
    {

    }

    public void render(float x, float y)
    {
        this.render(x,y,1.0f,0.0f,1.0f,1.0f);
    }

    public void render(float x, float y, float alpha, float rotation, float scaleX, float scaleY)
    {
        System.out.println("render sprite");
        GL2 gl2 = this.renderer.getGL().getGL2();

        // Simple triangle
        gl2.glBegin(GL.GL_TRIANGLES);
        gl2.glColor3f(1, 0, 0);
        //gl2.glVertex2f((float)Math.cos(rotation), (float)Math.sin(rotation));
        gl2.glVertex2f(-1.0f, -1.0f);
        gl2.glColor3f(0, 1, 0);
        gl2.glVertex2f(0, 1);
        gl2.glColor3f(0, 0, 1);
        gl2.glVertex2f(1, -1);
        gl2.glEnd();
    }
}

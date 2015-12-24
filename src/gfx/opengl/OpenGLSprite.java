package gfx.opengl;

import com.jogamp.opengl.*;

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
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();

        // Simple triangle
        gl2.glBegin(GL.GL_TRIANGLES);
        gl2.glColor3f(1, 0, 0);
        gl2.glVertex2f((float)Math.cos(rotation), (float)Math.sin(rotation));
        gl2.glColor3f(0, 1, 0);
        gl2.glVertex2f((float)Math.cos(rotation+Math.PI/3*2), (float)Math.sin(rotation+Math.PI/3*2));
        gl2.glColor3f(0, 0, 1);
        gl2.glVertex2f((float)Math.cos(rotation+Math.PI/3*4), (float)Math.sin(rotation+Math.PI/3*4));
        gl2.glEnd();
    }
}

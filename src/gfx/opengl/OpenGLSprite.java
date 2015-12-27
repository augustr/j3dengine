package gfx.opengl;

import com.jogamp.opengl.*;

import gfx.*;

/**
 * Meant to represent a rotatable, scalable 2D sprite. Curently only renders a simple triangle.
 */
public class OpenGLSprite implements Sprite {
    protected OpenGLRenderer renderer = null;

    public OpenGLSprite(Renderer renderer) {
        this.renderer = (OpenGLRenderer) renderer;
    }

    public void initialize(String filename, int width, int height) {

    }

    public void render(float x, float y) {
        this.render(x,y,1.0f,0.0f,1.0f,1.0f);
    }

    public void render(float x, float y, float alpha, float rotation, float scaleX, float scaleY) {
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

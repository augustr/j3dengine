package gfx.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;

import gfx.*;

public class OpenGLRenderer extends AbstractRenderer implements GLEventListener
{
    private IAbstractRenderThreadCallbackListener _renderThreadListener = null;
    private GLCanvas canvas;
    private GLAutoDrawable glad;
    private int imageWidth = 800;
    private int imageHeight = 600;

    public OpenGLRenderer(int width, int height)
    {
        super(width, height);
        this.setupOpenGL(width, height);
    }

    protected void setupOpenGL(int width, int height)
    {
        GLProfile profile = GLProfile.get(GLProfile.GL2);

        GLCapabilities capabilities = new GLCapabilities(profile);

        canvas = new GLCanvas(capabilities);

        canvas.setSize(imageWidth, imageHeight);

        canvas.addGLEventListener(this);

        canvas.setAutoSwapBufferMode(false);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GLAutoDrawable getGLAutoDrawable() {
        return this.glad;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        this.glad = glad;
        this._renderThreadListener.renderThread();
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        System.out.println("dispose");
    }

    @Override
    public void display(GLAutoDrawable glad) {

    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
        GL2 gl2 = glad.getGL().getGL2();

        gl2.glViewport(x, y, w, h);
    }

    public void addRenderThreadCallbackListener(IAbstractRenderThreadCallbackListener listener)
    {
        this._renderThreadListener = listener;
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
        GL2 gl2 = this.glad.getGL().getGL2();

        gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);
    }

    public void endRender()
    {
        GL2 gl2 = this.glad.getGL().getGL2();
        this.glad.swapBuffers();
    }
}

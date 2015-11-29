package gfx.opengl;

import gfx.*;

import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.String;
import javax.media.nativewindow.NativeSurface;
import javax.media.nativewindow.NativeWindowFactory;
import com.jogamp.nativewindow.awt.*;

/**
 * Created by august on 2015-04-05.
 */
public class OpenGLRenderer extends AbstractRenderer implements GLEventListener
{
    private GLDrawable drawable;
    private GLContext  context;
    private GL         gl;
    private GLCanvas   canvas;
    private Boolean    renderActive = false;
    private Boolean    isRealized = false;
    private int        panelWidth;
    private int        panelHeight;

    public OpenGLRenderer(int width, int height)
    {
        super(width, height);
        this.setupOpenGL(width, height);
    }

    public void addNotify()
    {
        super.addNotify();
        drawable.setRealized(true);

        this.renderActive = true;
    }

    public void reshape(int width, int height)
    {
        this.isRealized = true;
        if (height == 0) { height = 1; }

        this.panelWidth = width;
        this.panelHeight = height;
    }

    public void update(Graphics g) { }

    public void paint(Graphics g) { }

    protected void setupOpenGL(int width, int height)
    {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        //AWTGraphicsConfiguration config = AWTGraphicsConfiguration.create(this, caps, caps);
        //NativeSurface nativeSurface = NativeWindowFactory.getNativeWindow(this, config);
        //this.drawable = GLDrawableFactory.getFactory(glp).createGLDrawable(nativeSurface);

        /*GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        AWTGraphicsDevice dev = new AWTGraphicsDevice(device, 0);
        AWTGraphicsConfiguration awtConfig = (AWTGraphicsConfiguration)
                GLDrawableFactory.getFactory(glp).chooseGraphicsConfiguration(caps, null, dev);

        GraphicsConfiguration config = null;
        if (awtConfig != null)
            config = awtConfig.getGraphicsConfiguration();*/

        this.context = GLDrawableFactory.getFactory(glp).createExternalGLContext();

        /*NativeSurface nativeSurface = NativeWindowFactory.getNativeWindow(this, config);
        this.drawable = GLDrawableFactory.getFactory(glp).createGLDrawable(nativeSurface);

        this.context = this.drawable.createContext(null);*/

        this.gl = this.context.getGL();
    }

    public void close()
    {
        this.context.destroy();
    }

    public AbstractSprite createSprite(String filename, int width, int height)
    {
        return new OpenGLSprite(this, filename, width, height);
    }

    public void beginRender()
    {
        System.out.println("beginRender");
        this.makeContentCurrent();
        System.out.println("Content is current");

        this.gl = this.context.getGL();

        this.resizeView();

        GL2 gl2 = this.gl.getGL2();

        System.out.println("glClear");
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    public void endRender()
    {
        System.out.println("endRender");
        this.context.release();
        //this.canvas.swapBuffers();
    }

    private void resizeView()
    {

    }

    private void makeContentCurrent()
    {
        try {
            while (this.context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public GL getGL()
    {
        return this.gl;
    }
}

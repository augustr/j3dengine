package gfx.opengl;

import com.jogamp.opengl.*;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

import gfx.*;

public class OpenGLRenderer extends AbstractRenderer implements GLEventListener {
    private IAbstractRenderThreadCallbackListener renderThreadListener = null;
    private GLAutoDrawable                        glad                 = null;
    private int                                   width                = 800;
    private int                                   height               = 600;

    public OpenGLRenderer(int width, int height) {
        super(width, height);
        this.setupOpenGL(width, height);
    }

    protected void setupOpenGL(int width, int height) {
        this.width  = width;
        this.height = height;

        GLProfile      profile      = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        GLWindow window = GLWindow.create(capabilities);
        window.setSize(width, height);
        window.setVisible(true);
        window.setTitle("J3DEngine");

        window.addWindowListener(new WindowAdapter() {
            public void windowDestroyNotify(WindowEvent arg0) {
                System.out.println("destroy");
                System.exit(0);
            };
        });

        window.setAutoSwapBufferMode(false);
    }

    public GLAutoDrawable getGLAutoDrawable() {
        return this.glad;
    }

    public void addRenderThreadCallbackListener(IAbstractRenderThreadCallbackListener listener) {
        this.renderThreadListener = listener;
    }

    public void close() {

    }

    public AbstractSprite createSprite(String filename, int width, int height) {
        return new OpenGLSprite(this, filename, width, height);
    }

    public void beginRender() {
        GL2 gl2 = this.glad.getGL().getGL2();

        gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);
    }

    public void endRender() {
        GL2 gl2 = this.glad.getGL().getGL2();
        this.glad.swapBuffers();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        this.glad = glad;
        this.renderThreadListener.renderThread();
    }

    @Override
    public void dispose(GLAutoDrawable glad) { }

    @Override
    public void display(GLAutoDrawable glad) { }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
        GL2 gl2 = glad.getGL().getGL2();

        gl2.glViewport(x, y, w, h);
    }
}

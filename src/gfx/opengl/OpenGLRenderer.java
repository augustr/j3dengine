package gfx.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.KeyEvent;
import java.util.Random;

import gfx.*;

public class OpenGLRenderer extends AbstractRenderer implements GLEventListener, MouseListener, KeyListener {
    private IAbstractRenderThreadCallbackListener renderThreadListener = null;
    private GLAutoDrawable                        glad                 = null;
    private int                                   width                = 800;
    private int                                   height               = 600;
    private float theta = 0.0f;
    private float phi   = 0.0f;
    private float x     = 3.25f*2.0f;
    private float y     = 8.88f*2.0f;
    private float z     = 3.24f*2.0f;

    static { GLProfile.initSingleton(); }

    public OpenGLRenderer(int width, int height) {
        super(width, height);

        this.width  = width;
        this.height = height;
    }

    public void create() {
        this.setupOpenGL();
    }

    protected void setupOpenGL() {
        GLProfile      profile      = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLWindow window = GLWindow.create(capabilities);
        window.addGLEventListener(this);
        window.setSize(this.width, this.height);
        window.setTitle("OpenGLRenderer");

        window.addWindowListener(new WindowAdapter() {
            public void windowDestroyNotify(WindowEvent arg0) {
                System.exit(0);
            };
        });

        window.addMouseListener(this);
        window.addKeyListener(this);

        window.setVisible(true);

        window.setAutoSwapBufferMode(false);

        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start();
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
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl2.glLoadIdentity();

        /*theta += Math.PI/180*0.5f;
        phi   += Math.PI/360*0.5f;

        theta = 10.629146f;
        phi = 5.314573f;*/

        float r = 10f;
        GLU glu = new GLU();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        glu.gluPerspective(90, this.width/this.height, 0.1, 1000.0);
        glu.gluLookAt(x,
                      y,
                      z,
                      1.0f*(x+r*Math.sin(phi)*Math.cos(theta)),
                      1.0f*(y+r*Math.sin(theta)*Math.sin(phi)),
                      1.0f*(z+r*Math.cos(phi)),
                      0, 0, 1);
    }

    public void endRender() {
        this.glad.swapBuffers();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        this.glad = glad;
        GL2 gl2 = this.glad.getGL().getGL2();
        gl2.glFrontFace​(GL.GL_CCW);
        gl2.glEnable(GL.GL_CULL_FACE);
        gl2.glCullFace(GL.GL_BACK);
        gl2.glEnable(GL.GL_DEPTH_TEST);
        this.renderThreadListener.initialize();
    }

    @Override
    public void dispose(GLAutoDrawable glad) { }

    @Override
    public void display(GLAutoDrawable glad) {
        this.renderThreadListener.renderThread();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
        GL2 gl2 = glad.getGL().getGL2();

        gl2.glViewport(x, y, w, h);
    }

    // MouseListener
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        theta = (float) e.getX() * -0.01f;
        phi   = (float) e.getY() * -0.01f;
    }

    public void keyTyped(KeyEvent arg0) {}

    public void keyPressed(KeyEvent arg0)
    {
        switch(arg0.getKeyCode())
        {
            case KeyEvent.VK_LEFT:  x=x-0.1f     ; break;
            case KeyEvent.VK_RIGHT: x=x+0.1f     ; break;
            case KeyEvent.VK_UP:    z=z-0.1f     ; break;
            case KeyEvent.VK_DOWN:  z=z+0.1f     ; break;
        }
    }

    public void keyReleased(KeyEvent arg0) {}
}

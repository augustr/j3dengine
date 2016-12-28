package gfx.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.KeyEvent;

import gfx.*;

/**
 * OpenGL Renderer implementation
 *
 * @todo: The input functionality (Mouse, Keyboard) and "camera features" shouldn't be done here.
 */
public class OpenGLRenderer implements Renderer, GLEventListener, MouseListener, KeyListener {
    private AnimateCallbackListener animateListener = null;
    private GLAutoDrawable          glad            = null;
    private int                     width           = 800;
    private int                     height          = 600;
    private int                     lastMouseX      = 0;
    private int                     lastMouseY      = 0;
    private float                   theta           = (float) Math.PI/4.0f;
    private float                   phi             = (float) Math.PI/4.0f*3.0f;
    private float                   x               = 3.25f*2.0f;
    private float                   y               = 8.88f*2.0f;
    private float                   z               = 3.24f*2.0f;

    static { GLProfile.initSingleton(); }

    public OpenGLRenderer() {

    }

    public void initialize(String title, int width, int height) {
        this.width  = width;
        this.height = height;

        GLProfile      profile      = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLWindow window = GLWindow.create(capabilities);
        window.addGLEventListener(this);
        window.setSize(this.width, this.height);
        window.setTitle(title);

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

    public void addAnimateCallbackListener(AnimateCallbackListener listener) {
        this.animateListener = listener;
    }

    public void dispose() {

    }

    public Sprite createSprite(String filename, int width, int height) {
        Sprite sprite = new OpenGLSprite(this);
        sprite.initialize(filename, width, height);
        return sprite;
    }

    public Material createMaterial(String filename) throws java.io.IOException {
        Material material = new OpenGLMaterial(this);
        material.initialize(filename);
        return material;
    }

    public Surface createSurface() {
        return new OpenGLSurface(this);
    }

    public void beginRender() {
        GL2 gl2 = this.glad.getGL().getGL2();

        gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl2.glLoadIdentity();

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
        gl2.glShadeModel(GL2.GL_SMOOTH);
        this.animateListener.initialize();
    }

    @Override
    public void dispose(GLAutoDrawable glad) { }

    @Override
    public void display(GLAutoDrawable glad) {
        this.animateListener.animate();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
        GL2 gl2 = glad.getGL().getGL2();

        this.width  = w;
        this.height = h;

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
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        theta += (float) (e.getX()-lastMouseX) * 0.002f;
        phi   -= (float) (e.getY()-lastMouseY) * 0.002f;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    public void keyTyped(KeyEvent arg0) {}

    public void keyPressed(KeyEvent arg0)
    {
        switch(arg0.getKeyCode())
        {
            case KeyEvent.VK_LEFT:  x=x-0.1f; break;
            case KeyEvent.VK_RIGHT: x=x+0.1f; break;
            case KeyEvent.VK_UP:    z=z-0.1f; break;
            case KeyEvent.VK_DOWN:  z=z+0.1f; break;
            case KeyEvent.VK_PAGE_UP: y=y+0.1f; break;
            case KeyEvent.VK_PAGE_DOWN: y=y-0.1f; break;
        }
    }

    public void keyReleased(KeyEvent arg0) {}
}

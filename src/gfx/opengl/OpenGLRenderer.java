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
import gfx.math.Vector;

/**
 * OpenGL Renderer implementation
 *
 * @todo: The input functionality (Mouse, Keyboard) and "camera features" shouldn't be done here.
 */
public class OpenGLRenderer implements Renderer, GLEventListener, MouseListener, KeyListener {
    private AnimateCallbackListener animateListener           = null;
    private GLAutoDrawable          glad                      = null;
    private GLWindow                window                    = null;
    private FPSAnimator             animator                  = null;
    private int                     width                     = 800;
    private int                     height                    = 600;
    private int                     lastMouseX                = 0;
    private int                     lastMouseY                = 0;
    private float                   theta                     = (float) Math.PI/4.0f;
    private float                   phi                       = (float) Math.PI/4.0f*3.0f;
    private float                   x                         = 3.25f*2.0f;
    private float                   y                         = 8.88f*2.0f;
    private float                   z                         = 3.24f*2.0f;
    private float[]                 modelViewProjectionMatrix = null;

    private float test = 0.0f;

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

        this.window = window;

        animator = new FPSAnimator(window, 60);
        animator.setUpdateFPSFrames(3, null);
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

    public float[] getModelViewProjectionMatrix() {
        return this.modelViewProjectionMatrix;
    }

    protected void calculateAndSetModelViewProjectionMatrix(float theta,   float phi,
                                                            float centerX, float centerY, float centerZ,
                                                            float upX,     float upY,     float upZ) {
        float[] identityMatrix = {
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
        };

        float[] modelViewProjectionMatrix = identityMatrix;

        float zNear = 0.1f;
        float zFar  = 1000.0f;
        float fovX  = (float) Math.PI/2;
        float fovY  = (float) Math.PI/2;

        float[] projectionMatrix = {
                1.0f/(float) Math.tan(fovX/2),  0.0f,                          0.0f,                           0.0f,
                0.0f,                           1.0f/(float) Math.tan(fovY/2), 0.0f,                           0.0f,
                0.0f,                           0.0f,                          -(zFar + zNear)/(zFar - zNear), -2*(zNear*zFar)/(zFar-zNear),
                0.0f,                           0.0f,                          -1.0f,                          0.0f,
        };

        float r = 10f;
        Vector center = new Vector(centerX, centerY, centerZ);
        Vector lookAt = new Vector((float) (1.0f*(centerX+r*Math.sin(phi)*Math.cos(theta))),
                                   (float) (1.0f*(centerY+r*Math.sin(theta)*Math.sin(phi))),
                                   (float) (1.0f*(centerZ+r*Math.cos(phi))));
        Vector up = new Vector(0.0f, 0.0f, 1.0f);

        float[] lookAtMatrix = lookAtMatrix(center, lookAt, up);
        modelViewProjectionMatrix = multiplyMatrix(lookAtMatrix, modelViewProjectionMatrix);
        modelViewProjectionMatrix = multiplyMatrix(projectionMatrix, modelViewProjectionMatrix);

        this.modelViewProjectionMatrix = modelViewProjectionMatrix;
    }

    protected float[] lookAtMatrix(Vector center, Vector lookAt, Vector up) {
        Vector zaxis = Vector.subtract(lookAt, center);
        zaxis.normalize();
        Vector xaxis = Vector.cross(up, zaxis);
        xaxis.normalize();
        Vector yaxis = Vector.cross(zaxis, xaxis);

        float[] translationMatrix = {
                1.0f, 0.0f, 0.0f, -center.getX(),
                0.0f, 1.0f, 0.0f, -center.getY(),
                0.0f, 0.0f, 1.0f, -center.getZ(),
                0,    0,    0,    1.0f,
        };

        float[] rotationMatrix = {
                xaxis.getX(), xaxis.getY(), xaxis.getZ(), 0.0f,
                yaxis.getX(), yaxis.getY(), yaxis.getZ(), 0.0f,
                zaxis.getX(), zaxis.getY(), zaxis.getZ(), 0.0f,
                0.0f        , 0.0f        , 0.0f        , 1.0f,
        };

        return multiplyMatrix(rotationMatrix, translationMatrix);
    }

    protected float[] transposeMatrix(float[] matrix) {
        float[] transposedMatrix = {
                matrix[0*4+0], matrix[1*4+0], matrix[2*4+0], matrix[3*4+0],
                matrix[0*4+1], matrix[1*4+1], matrix[2*4+1], matrix[3*4+1],
                matrix[0*4+2], matrix[1*4+2], matrix[2*4+2], matrix[3*4+2],
                matrix[0*4+3], matrix[1*4+3], matrix[2*4+3], matrix[3*4+3],
        };
        return transposedMatrix;
    }

    protected float[] translateMatrix(float[] matrix, float x, float y, float z) {
        float[] translateTransform = {
                1.0f, 0.0f, 0.0f, x,
                0.0f, 1.0f, 0.0f, y,
                0.0f, 0.0f, 1.0f, z,
                0,    0,    0,    1.0f,
        };

        return multiplyMatrix(matrix, translateTransform);
    }

    protected float[] rotateMatrixXAxis(float[] matrix, float angle) {
        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);
        float[] rotateTransform = {
                1.0f, 0.0f,     0.0f,      0.0f,
                0.0f, cosAngle, -sinAngle, 0.0f,
                0.0f, sinAngle, cosAngle,  0.0f,
                0.0f, 0.0f,     0.0f,      1.0f,
        };
        return multiplyMatrix(matrix, rotateTransform);
    }

    protected float[] rotateMatrixYAxis(float[] matrix, float angle) {
        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);
        float[] rotateTransform = {
                cosAngle,  0.0f, sinAngle, 0.0f,
                0.0f,      1.0f, 0.0f,     0.0f,
                -sinAngle, 0.0f, cosAngle, 0.0f,
                0.0f,      0.0f, 0.0f,     1.0f,
        };
        return multiplyMatrix(matrix, rotateTransform);
    }

    protected float[] rotateMatrixZAxis(float[] matrix, float angle) {
        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);
        float[] rotateTransform = {
                cosAngle, -sinAngle, 0.0f, 0.0f,
                sinAngle, cosAngle,  0.0f, 0.0f,
                0.0f,     0.0f,      1.0f, 0.0f,
                0.0f,     0.0f,      0.0f, 1.0f,
        };
        return multiplyMatrix(matrix, rotateTransform);
    }

    protected float[] multiplyMatrix(float[] matrixA, float[] matrixB) {
        float[] result = new float[16];
        for (int r=0; r<4; r++) {
            for (int c=0; c<4; c++) {
                result[r*4+c] = matrixA[r*4+0]*matrixB[0*4+c] +
                                matrixA[r*4+1]*matrixB[1*4+c] +
                                matrixA[r*4+2]*matrixB[2*4+c] +
                                matrixA[r*4+3]*matrixB[3*4+c];
            }
        }
        return result;
    }

    public void beginRender() {
        GL2 gl2 = this.glad.getGL().getGL2();

        gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Calculate and set model view projection matrix
        calculateAndSetModelViewProjectionMatrix(theta,
                                                 phi,x,
                                                 y,
                                                 z,
                                                 0, 0, 1);
    }

    public void endRender() {
        this.glad.swapBuffers();
        if (window != null) {
            window.setTitle("OpenGL Engine - FPS: " + Float.toString(animator.getLastFPS()));
        }
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

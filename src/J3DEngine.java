/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
//import glsl.GLSLProgramObject;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author gbarbieri
 */
public class J3DEngine implements GLEventListener {

    private int imageWidth = 800;
    private int imageHeight = 600;
    private GLCanvas canvas;
    //private GLSLProgramObject programObject;
    private int[] positionBufferObject = new int[1];
    private int[] vertexArrayObject = new int[1];
    private float[] vertexPositions = new float[]{
            0.75f, 0.75f, 0.0f, 1.0f,
            0.75f, -0.75f, 0.0f, 1.0f,
            -0.75f, -0.75f, 0.0f, 1.0f,};
    private String shadersFilepath = "/tut01/shaders/";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        J3DEngine tut01 = new J3DEngine();

        Frame frame = new Frame("Tutorial 01");

        frame.add(tut01.getCanvas());

        frame.setSize(tut01.getCanvas().getWidth(), tut01.getCanvas().getHeight());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

    public J3DEngine() {
        initGL();
    }

    private void initGL() {
        GLProfile profile = GLProfile.get(GLProfile.GL2);

        GLCapabilities capabilities = new GLCapabilities(profile);

        canvas = new GLCanvas(capabilities);

        canvas.setSize(imageWidth, imageHeight);

        canvas.addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable glad) {
        System.out.println("init");

        canvas.setAutoSwapBufferMode(false);

        /*GL3 gl3 = glad.getGL().getGL3();

        initializeVertexBuffer(gl3);

        gl3.glGenVertexArrays(1, IntBuffer.wrap(vertexArrayObject));
        gl3.glBindVertexArray(vertexArrayObject[0]);*/
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        System.out.println("dispose");
    }

    @Override
    public void display(GLAutoDrawable glad) {
        System.out.println("display");

        GL2 gl2 = glad.getGL().getGL2();

        gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);

        gl2.glBegin(GL2.GL_TRIANGLES);
        gl2.glColor3f(1, 0, 0);
        //gl2.glVertex2f((float)Math.cos(rotation), (float)Math.sin(rotation));
        gl2.glVertex2f(-1.0f, -1.0f);
        gl2.glColor3f(0, 1, 0);
        gl2.glVertex2f(0, 1);
        gl2.glColor3f(0, 0, 1);
        gl2.glVertex2f(1, -1);
        gl2.glEnd();

        glad.swapBuffers();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
        System.out.println("reshape() x: " + x + " y: " + y + " width: " + w + " height: " + h);

        GL2 gl2 = glad.getGL().getGL2();

        gl2.glViewport(x, y, w, h);
    }

    private void initializeVertexBuffer(GL3 gl3) {
        gl3.glGenBuffers(1, IntBuffer.wrap(positionBufferObject));

        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, positionBufferObject[0]);
        {
            FloatBuffer buffer = GLBuffers.newDirectFloatBuffer(vertexPositions);

            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, vertexPositions.length * 4, buffer, GL3.GL_STATIC_DRAW);
        }
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    }

    public GLCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(GLCanvas canvas) {
        this.canvas = canvas;
    }
}
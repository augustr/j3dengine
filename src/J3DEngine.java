/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Canvas;

import gfx.*;
import gfx.opengl.*;

/**
 *
 * @author gbarbieri
 */
public class J3DEngine implements IAbstractRenderThreadCallbackListener {

    private AbstractRenderer renderer = null;
    private boolean initialized = false;
    private int imageWidth = 800;
    private int imageHeight = 600;
    private float rotation = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        J3DEngine engine = new J3DEngine();

        Frame frame = new Frame("Tutorial 01");

        frame.add(engine.getCanvas());

        frame.setSize(engine.getCanvas().getWidth(), engine.getCanvas().getHeight());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

    public J3DEngine() {

        this.renderer = new OpenGLRenderer(800, 600);
        this.renderer.addRenderThreadCallbackListener(this);
    }

    public Canvas getCanvas()
    {
        return this.renderer.getCanvas();
    }

    public void renderThread() {
        // Setup
        AbstractSprite sprite = this.renderer.createSprite("filename", 640, 480);
        float rotation = 0.0f;

        // Main loop
        while(true) {
            rotation += Math.PI/180;
            if (rotation > 2*Math.PI) { rotation = 0.0f; }

            this.renderer.beginRender();
            sprite.render(0.0f, 0.0f,1.0f,rotation,1.0f,1.0f);
            this.renderer.endRender();
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
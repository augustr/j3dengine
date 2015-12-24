import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Canvas;

import gfx.*;
import gfx.opengl.*;

public class J3DEngine implements IAbstractRenderThreadCallbackListener {

    private AbstractRenderer renderer = null;
    private int              width    = 800;
    private int              height   = 600;
    private float            rotation = 0;
    private boolean          running  = false;

    public static void main(String[] args) {
        final J3DEngine engine = new J3DEngine();

        final Frame frame = new Frame("J3DEngine");

        frame.add(engine.getCanvas());
        frame.setSize(engine.getCanvas().getWidth(), engine.getCanvas().getHeight());
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                engine.stop();
                frame.remove(engine.getCanvas());
                frame.dispose();
                System.exit(0);
            }
        });
    }

    public J3DEngine() {

        this.renderer = new OpenGLRenderer(this.width, this.height);
        this.renderer.addRenderThreadCallbackListener(this);
    }

    public void stop() {
        this.running = false;
    }

    public Canvas getCanvas() {
        return this.renderer.getCanvas();
    }

    public void renderThread() {
        // Setup
        AbstractSprite sprite = this.renderer.createSprite("filename", 640, 480);
        float rotation = 0.0f;

        // Main loop
        this.running = true;
        while(this.running) {
            rotation += Math.PI/180*2;
            if (rotation > 2*Math.PI) { rotation = 0.0f; }

            this.renderer.beginRender();
            sprite.render(0.0f, 0.0f, 1.0f, rotation, 1.0f, 1.0f);
            this.renderer.endRender();
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.renderer.close();
    }
}
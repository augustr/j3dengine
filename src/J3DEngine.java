import gfx.*;
import gfx.opengl.*;

import com.jogamp.opengl.*;

public class J3DEngine implements IAbstractRenderThreadCallbackListener {

    private AbstractRenderer renderer = null;
    private int              width    = 800;
    private int              height   = 600;
    private float            rotation = 0;
    private boolean          running  = false;

    public static void main(String[] args) {
        GLProfile.initSingleton();
        final J3DEngine engine = new J3DEngine();
    }

    public J3DEngine() {
        this.renderer = new OpenGLRenderer(this.width, this.height);
        this.renderer.addRenderThreadCallbackListener(this);
        this.renderer.create();
    }

    public void stop() {
        this.running = false;
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
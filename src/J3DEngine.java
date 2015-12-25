import gfx.*;
import gfx.opengl.*;

public class J3DEngine implements IAbstractRenderThreadCallbackListener {

    private OpenGLRenderer   renderer  = null;
    private int              width     = 800;
    private int              height    = 600;
    private float            rotation  = 0;
    private boolean          running   = false;
    private OpenGLLandscape  landscape = null;

    public static void main(String[] args) {
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
        // Main loop
        //this.running = true;
        //while(this.running) {
            rotation += Math.PI/180*2;
            if (rotation > 2*Math.PI) { rotation = 0.0f; }

            this.renderer.beginRender();

            //sprite.render(0.0f, 0.0f, 1.0f, rotation, 1.0f, 1.0f);

            this.landscape.render();

            this.renderer.endRender();
        //}

        //this.renderer.close();
    }

    public void initialize() {
        HeightMap heightMap = new HeightMap("../res/test6.bmp");

        this.landscape = new OpenGLLandscape(this.renderer, heightMap);
    }
}
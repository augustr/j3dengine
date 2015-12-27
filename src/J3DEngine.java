import gfx.*;
import gfx.opengl.*;

public class J3DEngine implements AnimateCallbackListener {

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
        this.renderer = new OpenGLRenderer();
        this.renderer.addAnimateCallbackListener(this);
        this.renderer.initialize("J3DEngine", this.width, this.height);
    }

    public void stop() {
        this.running = false;
    }

    public void initialize() {
        HeightMap heightMap = new HeightMap("../res/test6.bmp");

        this.landscape = new OpenGLLandscape(this.renderer, heightMap);
    }

    public void animate() {
        this.renderer.beginRender();

        this.landscape.render();

        this.renderer.endRender();
    }
}
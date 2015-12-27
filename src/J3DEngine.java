import gfx.*;
import gfx.opengl.*;

public class J3DEngine implements AnimateCallbackListener {
    private int       width     = 800;
    private int       height    = 600;
    private Renderer  renderer  = null;
    private Landscape landscape = null;

    public static void main(String[] args) {
        final J3DEngine engine = new J3DEngine();
    }

    public J3DEngine() {
        this.renderer = RendererFactory.singleton().getRenderer(RendererFactory.RenderBackend.OPENGL);
        this.renderer.addAnimateCallbackListener(this);
        this.renderer.initialize("J3DEngine", this.width, this.height);
    }

    public void stop() {

    }

    public void initialize() {
        this.landscape = new Landscape(this.renderer, "res/test6.bmp");
    }

    public void animate() {
        this.renderer.beginRender();

        this.landscape.render();

        this.renderer.endRender();
    }
}
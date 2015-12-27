package gfx;

import gfx.opengl.*;

public class RendererFactory {
    public enum RenderBackend {
        OPENGL,
    }

    private static RendererFactory instance = null;

    private RendererFactory() {

    }

    public static synchronized RendererFactory singleton() {
        if (instance == null) {
            if (instance == null) {
                instance = new RendererFactory();
            }
        }
        return instance;
    }

    public Renderer getRenderer(RenderBackend backend) {
        switch(backend) {
            case OPENGL:
                return new OpenGLRenderer();
        }

        return null;
    }
}

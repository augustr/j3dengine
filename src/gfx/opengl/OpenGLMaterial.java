package gfx.opengl;

import com.jogamp.opengl.*;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import gfx.*;

public class OpenGLMaterial implements Material {
    private OpenGLRenderer renderer = null;
    private Texture        texture  = null;

    public OpenGLMaterial(OpenGLRenderer renderer) {
        this.renderer = renderer;
    }

    public void initialize(String filename) throws java.io.IOException {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();

        texture = TextureIO.newTexture(getClass().getResourceAsStream(filename),
                                       false,
                                       this.getFileExtension(filename));

        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
    }

    public void enable() {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();
        texture.bind(gl2);
        texture.enable(gl2);
    }

    public void disable() {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();
        texture.disable(gl2);
    }

    private static String getFileExtension(String filename) {
        if(filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return null;
    }
}

package gfx.opengl;

import com.jogamp.opengl.*;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import gfx.*;

public class OpenGLMaterial implements Material {
    private OpenGLRenderer renderer  = null;
    private Texture        texture   = null;
    private static int     count     = 0;
    private int            id        = 0;

    public OpenGLMaterial(OpenGLRenderer renderer) {
        this.renderer = renderer;
    }

    public void initialize(String filename) throws java.io.IOException {
        this.id = count++;
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();

        texture = TextureIO.newTexture(getClass().getResourceAsStream(filename),
                                       false,
                                       this.getFileExtension(filename));

        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
    }

    public String getName() {
        return "material" + this.id;
    }

    public void enable() {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();
        gl2.glActiveTexture(GL.GL_TEXTURE0 + this.id);
        texture.bind(gl2);
    }

    public void disable() {

    }

    public void bindShader(OpenGLShader shader) {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();
        gl2.glUniform1i(gl2.glGetUniformLocation(shader.getId(), this.getName()), this.id);
    }

    private static String getFileExtension(String filename) {
        if(filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return null;
    }
}

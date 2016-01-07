package gfx.opengl;

import com.jogamp.opengl.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OpenGLShader {
    private OpenGLRenderer renderer         = null;
    private int            shaderId         = 0;
    private int            vertexShaderId   = 0;
    private int            fragmentShaderId = 0;

    public OpenGLShader(OpenGLRenderer renderer) {
        this.renderer = renderer;
    }

    public void loadVertexShaderFromFile(String filename) {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();

        // Create placeholder for shader
        this.vertexShaderId = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);

        // Load the shader source
        String[] shaderSource = this.loadShaderSource(filename);

        // Compile shader
        gl2.glShaderSource(this.vertexShaderId, 1, shaderSource, null, 0);
        gl2.glCompileShader(this.vertexShaderId);

        // Check for errors
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl2.glGetShaderiv(this.vertexShaderId, GL3.GL_COMPILE_STATUS, intBuffer);

        if (intBuffer.get(0) != 1) {
            gl2.glGetShaderiv(this.vertexShaderId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            System.err.println("Shader compile error: ");
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl2.glGetShaderInfoLog(this.vertexShaderId, size, intBuffer, byteBuffer);

                for (byte b:byteBuffer.array()) {
                    System.err.print((char)b);
                }
            }
            else {
                System.out.println("Unknown");
            }
            System.exit(1);
        }

        // Attach and link shader
        this.shaderId = gl2.glCreateProgram();
        gl2.glAttachShader(this.shaderId, this.vertexShaderId);
        gl2.glLinkProgram(this.shaderId);

        // Check for errors
        intBuffer = IntBuffer.allocate(1);
        gl2.glGetProgramiv(this.shaderId, GL2.GL_LINK_STATUS, intBuffer);

        if (intBuffer.get(0) != 1) {
            gl2.glGetProgramiv(this.shaderId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            System.err.println("Shader link error: ");
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl2.glGetProgramInfoLog(this.shaderId, size, intBuffer, byteBuffer);

                for (byte b:byteBuffer.array()) {
                    System.err.print((char)b);
                }
            }
            else {
                System.out.println("Unknown");
            }
            System.exit(1);
        }

        gl2.glValidateProgram(this.shaderId);
    }

    public void enable() {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();
        gl2.glUseProgram(this.shaderId);
    }

    private String[] loadShaderSource(String filename) {
        StringBuilder builder = new StringBuilder();

        try {
            InputStream stream = getClass().getResourceAsStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

            stream.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return new String[]{builder.toString()};
    }
}

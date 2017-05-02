package gfx.opengl;

import gfx.*;

import com.jogamp.opengl.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OpenGLShader {
    private OpenGLRenderer renderer                    = null;
    private int            shaderId                    = 0;
    private int            modelViewProjectionLocation = 0;

    public OpenGLShader(OpenGLRenderer renderer) {
        this.renderer = renderer;
    }

    public void loadVertexShaderFromFile(String filename) {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();

        int vertexShaderId   = compileVertexShader(filename);
        int fragmentShaderId = compileFragmentShader(filename);

        // Attach and link shader
        this.shaderId = gl2.glCreateProgram();
        gl2.glBindAttribLocation(this.shaderId, 0,  "in_position");
        gl2.glBindAttribLocation(this.shaderId, 1,  "in_normal");
        gl2.glBindAttribLocation(this.shaderId, 2,  "in_color");
        gl2.glBindAttribLocation(this.shaderId, 3,  "in_uv");
        gl2.glAttachShader(this.shaderId, vertexShaderId);
        gl2.glAttachShader(this.shaderId, fragmentShaderId);
        gl2.glLinkProgram(this.shaderId);

        // Get ModelViewProjection matrix location
        modelViewProjectionLocation = gl2.glGetUniformLocation(this.shaderId, "in_modelviewprojection");

        // Check for errors
        IntBuffer intBuffer = IntBuffer.allocate(1);
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

        System.out.println("Shader " + filename + " loaded successfully.");
    }

    private int compileVertexShader(String filename) {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();

        // Create placeholder for shader
        int vertexShaderId = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
        return compileShader(filename + "_vertex.glsl", vertexShaderId);
    }

    private int compileFragmentShader(String filename) {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();

        // Create placeholder for shader
        int fragmentShaderId = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        return compileShader(filename + "_fragment.glsl", fragmentShaderId);
    }

    private int compileShader(String filename, int vertexOrFragmentshaderId) {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();

        // Load the shader source
        String[] shaderSource = this.loadShaderSource(filename);

        // Compile shader
        gl2.glShaderSource(vertexOrFragmentshaderId, 1, shaderSource, null, 0);
        gl2.glCompileShader(vertexOrFragmentshaderId);

        // Check for errors
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl2.glGetShaderiv(vertexOrFragmentshaderId, GL3.GL_COMPILE_STATUS, intBuffer);

        if (intBuffer.get(0) != 1) {
            gl2.glGetShaderiv(vertexOrFragmentshaderId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            System.err.println("Shader compile error: ");
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl2.glGetShaderInfoLog(vertexOrFragmentshaderId, size, intBuffer, byteBuffer);

                for (byte b:byteBuffer.array()) {
                    System.err.print((char)b);
                }
            }
            else {
                System.out.println("Unknown");
            }
            System.exit(1);
        }

        return vertexOrFragmentshaderId;
    }

    public void enable() {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();
        gl2.glUseProgram(this.shaderId);
    }

    public void beginRender() {
        GL2 gl2 = this.renderer.getGLAutoDrawable().getGL().getGL2();
        float[] modelViewProjectionMatrix = this.renderer.getModelViewProjectionMatrix();
        gl2.glUniformMatrix4fv(modelViewProjectionLocation, 1, true, modelViewProjectionMatrix, 0);
    }

    public void endRender() {

    }

    public int getId() {
        return this.shaderId;
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

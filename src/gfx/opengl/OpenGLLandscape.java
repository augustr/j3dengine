package gfx.opengl;

import com.jogamp.common.nio.Buffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.io.File;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

import gfx.HeightMap;

public class OpenGLLandscape {
    private int[]          vertexHandle       = new int[1];
    private int            vertexCount        = 0;
    private int[]          indexHandle        = new int[1];
    private int            indexCount         = 0;
    private int[]          colorHandle        = new int[1];
    private int            colorCount         = 0;
    private Texture        grassTexture       = null;
    private OpenGLRenderer renderer           = null;

    public OpenGLLandscape(OpenGLRenderer renderer, HeightMap heightMap) {
        // @todo Split this function into many. One for each buffer type and then functions for calculating normals, shading etc.
        // @todo In the long run: Take out the buffer generation code and place it in generic Landscape class. Make this
        // a very generic OpenGLObject class inheriting an AbstractObject (or Abstract3DModel or similar)
        FloatBuffer vertexBuffer  = Buffers.newDirectFloatBuffer(heightMap.getWidth()*heightMap.getHeight()*8);
        FloatBuffer colorBuffer   = Buffers.newDirectFloatBuffer(heightMap.getWidth()*heightMap.getHeight()*3);
        IntBuffer   indexBuffer   = Buffers.newDirectIntBuffer(6*(heightMap.getWidth()-1)*(heightMap.getHeight()-1));
        GL2         gl2           = renderer.getGLAutoDrawable().getGL().getGL2();

        this.renderer = renderer;

        // Load textures
        try {
            grassTexture = TextureIO.newTexture(getClass().getResourceAsStream("../../res/GX_10_Grass_05.jpg"), false, "jpg");
        }
        catch (java.io.IOException exception) {
            System.out.println(exception.toString());
        }
        grassTexture.enable(gl2);
        grassTexture.bind(gl2);

        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);


        // Create buffers
        float scale    = 1.0f;
        float hScale   = 0.2f*scale;
        float vScale   = 10.0f*scale;
        float texScale = 0.5f*hScale;

        // Create vertex, normal and texture buffer
        for(int x = 0; x < heightMap.getWidth(); x++) {
            for(int y = 0; y < heightMap.getHeight(); y++) {
                float z = heightMap.getZ(x,y);

                // Vertex coordinates
                vertexBuffer.put((float)x*hScale);
                vertexBuffer.put((float)y*hScale);
                vertexBuffer.put(z/256.0f*vScale);

                // Normal coordinates
                // Only reserve space for normals. Calculate them later when index buffer is created
                vertexBuffer.put(0.0f);
                vertexBuffer.put(0.0f);
                vertexBuffer.put(0.0f);

                // Texture coordinates
                vertexBuffer.put(x*texScale);
                vertexBuffer.put(y*texScale);

                this.vertexCount++;
            }
        }
        vertexBuffer.flip();

        // Create and load data into GL buffers
        gl2.glGenBuffers(1, this.vertexHandle, 0);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexHandle[0]);
        gl2.glBufferData(GL.GL_ARRAY_BUFFER, this.vertexCount * 8 *
                Buffers.SIZEOF_FLOAT, vertexBuffer, GL.GL_STATIC_DRAW);

        // Create index buffer
        long xIndexes = heightMap.getWidth()-1;
        long yIndexes = heightMap.getHeight()-1;

        for (int x = 0; x < xIndexes; x++) {
            for (int y = 0; y < yIndexes; y++) {
                // First triangle
                int t11 = x * heightMap.getHeight() + y;
                int t12 = (x + 1) * heightMap.getHeight() + y;
                int t13 = x * heightMap.getHeight() + y + 1;

                indexBuffer.put(t11);
                indexBuffer.put(t12);
                indexBuffer.put(t13);

                // Normal


                // Second triangle
                int t21 = (x + 1) * heightMap.getHeight() + y;
                int t22 = (x + 1) * heightMap.getHeight() + y + 1;
                int t23 = x * heightMap.getHeight() + y + 1;

                indexBuffer.put(t21);
                indexBuffer.put(t22);
                indexBuffer.put(t23);

                this.indexCount += 6;
            }
        }

        indexBuffer.flip();

        // Create and load index buffer data into GL buffer
        gl2.glGenBuffers(1, this.indexHandle, 0);
        gl2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, this.indexHandle[0]);
        gl2.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, this.indexCount * Buffers.SIZEOF_INT, indexBuffer, GL.GL_STATIC_DRAW);

        // Create color buffer
        for(int x = 0; x < heightMap.getWidth(); x++) {
            for(int y = 0; y < heightMap.getHeight(); y++) {
                float z = heightMap.getZ(x,y);
                colorBuffer.put(z/256.0f*2.0f);
                colorBuffer.put(z/256.0f*2.0f);
                colorBuffer.put(z/256.0f*2.0f);

                this.colorCount++;
            }
        }
        colorBuffer.flip();

        // Create and load data into GL buffer
        gl2.glGenBuffers(1, this.colorHandle, 0);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.colorHandle[0]);
        gl2.glBufferData(GL.GL_ARRAY_BUFFER, this.colorCount * 3 *
                Buffers.SIZEOF_FLOAT, colorBuffer, GL.GL_STATIC_DRAW);

        // Data is now copied to GPU memory. Clear local data
        vertexBuffer  = null;
        colorBuffer   = null;
        indexBuffer   = null;
    }

    public void render() {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();

        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexHandle[0]);
        gl2.glVertexPointer(3, GL.GL_FLOAT, 8*Buffers.SIZEOF_FLOAT, 0);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexHandle[0]);
        gl2.glTexCoordPointer(2, GL.GL_FLOAT, 8*Buffers.SIZEOF_FLOAT, 6*Buffers.SIZEOF_FLOAT);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.colorHandle[0]);
        gl2.glColorPointer(3, GL.GL_FLOAT, 0, 0);

        gl2.glDrawElements(GL.GL_TRIANGLES, this.indexCount, GL.GL_UNSIGNED_INT, 0);

        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    }
}

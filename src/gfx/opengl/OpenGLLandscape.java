package gfx.opengl;

import com.jogamp.common.nio.Buffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import com.jogamp.opengl.*;

import gfx.HeightMap;

public class OpenGLLandscape {
    private int[]          vertexHandle  = new int[1];
    private int            vertexCount   = 0;
    private int[]          indexHandle   = new int[1];
    private int            indexCount    = 0;
    private int[]          colorHandle   = new int[1];
    private int            colorCount    = 0;
    private OpenGLRenderer renderer      = null;

    public OpenGLLandscape(OpenGLRenderer renderer, HeightMap heightMap) {
        FloatBuffer vertexBuffer  = Buffers.newDirectFloatBuffer(heightMap.getWidth()*heightMap.getHeight()*3);
        FloatBuffer colorBuffer   = Buffers.newDirectFloatBuffer(heightMap.getWidth()*heightMap.getHeight()*3);
        IntBuffer   indexBuffer   = Buffers.newDirectIntBuffer(6*(heightMap.getWidth()-1)*(heightMap.getHeight()-1));
        GL2         gl2           = renderer.getGLAutoDrawable().getGL().getGL2();

        this.renderer = renderer;

        // Create vertice buffer
        for(int x = 0; x < heightMap.getWidth(); x++) {
            for(int y = 0; y < heightMap.getHeight(); y++) {
                float z = heightMap.getZ(x,y);
                vertexBuffer.put((float)x*0.2f);
                vertexBuffer.put((float)y*0.2f);
                vertexBuffer.put(z/256.0f*10.0f);

                this.vertexCount++;
            }
        }
        vertexBuffer.flip();

        // Create and load data into GL buffer
        gl2.glGenBuffers(1, this.vertexHandle, 0);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexHandle[0]);
        gl2.glBufferData(GL.GL_ARRAY_BUFFER, this.vertexCount * 3 *
                Buffers.SIZEOF_FLOAT, vertexBuffer, GL.GL_STATIC_DRAW);

        // Create color buffer
        for(int x = 0; x < heightMap.getWidth(); x++) {
            for(int y = 0; y < heightMap.getHeight(); y++) {
                float z = heightMap.getZ(x,y);
                colorBuffer.put(0.0f);
                colorBuffer.put(z/256.0f*2.0f);
                colorBuffer.put(0.0f);

                this.colorCount++;
            }
        }
        colorBuffer.flip();

        // Create and load data into GL buffer
        gl2.glGenBuffers(1, this.colorHandle, 0);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.colorHandle[0]);
        gl2.glBufferData(GL.GL_ARRAY_BUFFER, this.colorCount * 3 *
                Buffers.SIZEOF_FLOAT, colorBuffer, GL.GL_STATIC_DRAW);

        // Create index buffer
        long xIndexes = heightMap.getWidth()-1;
        long yIndexes = heightMap.getHeight()-1;

        for (int x = 0; x < xIndexes; x++) {
            for (int y = 0; y < yIndexes; y++) {
                // First triangle
                indexBuffer.put(x * heightMap.getHeight() + y);
                indexBuffer.put((x + 1) * heightMap.getHeight() + y);
                indexBuffer.put(x * heightMap.getHeight() + y + 1);

                // Second triangle
                indexBuffer.put((x + 1) * heightMap.getHeight() + y);
                indexBuffer.put((x + 1) * heightMap.getHeight() + y + 1);
                indexBuffer.put(x * heightMap.getHeight() + y + 1);

                this.indexCount += 6;
            }
        }

        indexBuffer.flip();

        // Create and load index buffer data into GL buffer
        gl2.glGenBuffers(1, this.indexHandle, 0);
        gl2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, this.indexHandle[0]);
        gl2.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, this.indexCount * Buffers.SIZEOF_INT, indexBuffer, GL.GL_STATIC_DRAW);

        // Data is now copied to GPU memory. Clear local data
        vertexBuffer = null;
        colorBuffer  = null;
        indexBuffer  = null;
    }

    public void render() {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();

        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexHandle[0]);
        gl2.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.colorHandle[0]);
        gl2.glColorPointer(3, GL.GL_FLOAT, 0, 0);

        gl2.glDrawElements(GL.GL_TRIANGLES, this.indexCount, GL.GL_UNSIGNED_INT, 0);

        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
    }
}

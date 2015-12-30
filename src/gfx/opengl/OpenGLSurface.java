package gfx.opengl;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.io.File;

import gfx.*;
import gfx.math.*;

public class OpenGLSurface implements Surface {
    private int[]    vertexHandle    = new int[1];
    private int      vertexCount     = 0;
    private int[]    indexHandle     = new int[1];
    private int      indexCount      = 0;
    private Material defaultMaterial = null;
    private Material slopeMaterial   = null;
    private Material lowMaterial     = null;
    private float    slope           = 0.5f;
    private float    elavation       = 0.1f;

    private OpenGLRenderer renderer     = null;

    public OpenGLSurface(OpenGLRenderer renderer) {
        this.renderer = renderer;
    }

    public void setMaterialDefault(Material material) {
        this.defaultMaterial = material;
    }

    public void setMaterialSlope(Material material, float slope) {
        this.slopeMaterial = material;
        this.slope = slope;
    }

    public void setMaterialLow(Material material, float elavation) {
        this.lowMaterial = material;
        this.elavation = elavation;
    }

    public void initialize(int width, int height, float[][] heightMap) {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();

        this.renderer = renderer;

        float scale    = 5.0f;
        float hScale   = 0.2f*scale;
        float vScale   = 10.0f*scale;
        float texScale = 0.05f*hScale;

        // Create vertex buffer with space for normals, colors and texture coordinates
        FloatBuffer vertexBuffer = this.createVertexBuffer(width, height, heightMap, scale, hScale, vScale, texScale);

        // Create index buffer and at the same time add some normals to the vertex buffer from the triangles calculated
        IntBuffer indexBuffer = this.createIndexBuffer(width, height, heightMap, vertexBuffer);

        // Create and load vertex buffer data into GL buffer
        gl2.glGenBuffers(1, this.vertexHandle, 0);
        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexHandle[0]);
        gl2.glBufferData(GL.GL_ARRAY_BUFFER, this.vertexCount * 12 * Buffers.SIZEOF_FLOAT, vertexBuffer, GL.GL_STATIC_DRAW);

        // Create and load index buffer data into GL buffer
        gl2.glGenBuffers(1, this.indexHandle, 0);
        gl2.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, this.indexHandle[0]);
        gl2.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, this.indexCount * Buffers.SIZEOF_INT, indexBuffer, GL.GL_STATIC_DRAW);

        // Data is now copied to GPU memory. Clear local data
        vertexBuffer  = null;
        indexBuffer   = null;
    }

    private FloatBuffer createVertexBuffer(int width, int height, float[][] heightMap, float scale, float hScale, float vScale, float texScale) {
        FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(width*height*12);

        // Create vertex, normal, color and texture buffer
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                float z = heightMap[x][y];

                // Vertex coordinates
                vertexBuffer.put((float)x*hScale);
                vertexBuffer.put((float)y*hScale);
                vertexBuffer.put(z/256.0f*vScale);

                // Normal coordinates
                // Only reserve space for normals and color. Calculate them later when index buffer is created
                vertexBuffer.put(0.0f);
                vertexBuffer.put(0.0f);
                vertexBuffer.put(0.0f);

                // Color components
                vertexBuffer.put(0.0f);
                vertexBuffer.put(0.0f);
                vertexBuffer.put(0.0f);
                vertexBuffer.put(1.0f);

                // Texture coordinates
                vertexBuffer.put(x*texScale);
                vertexBuffer.put(y*texScale);

                this.vertexCount++;
            }
        }
        vertexBuffer.flip();

        return vertexBuffer;
    }

    private IntBuffer createIndexBuffer(int width, int height, float[][] heightMap, FloatBuffer vertexBuffer) {
        IntBuffer indexBuffer = Buffers.newDirectIntBuffer(6*(width-1)*(height-1));

        // Create index buffer
        long xIndexes = width-1;
        long yIndexes = height-1;

        for (int x = 0; x < xIndexes; x++) {
            for (int y = 0; y < yIndexes; y++) {
                // First triangle
                int t11 = x * height + y;
                int t12 = (x + 1) * height + y;
                int t13 = x * height + y + 1;

                indexBuffer.put(t11);
                indexBuffer.put(t12);
                indexBuffer.put(t13);

                // Calculate triangle normal and fill in vertex buffer
                this.addNormalAndShading(vertexBuffer, width, height, t11, t12, t13, false);

                // Second triangle
                int t21 = (x + 1) * height + y;
                int t22 = (x + 1) * height + y + 1;
                int t23 = x * height + y + 1;

                indexBuffer.put(t21);
                indexBuffer.put(t22);
                indexBuffer.put(t23);

                // Calculate triangle normal and fill in vertex buffer
                this.addNormalAndShading(vertexBuffer, width, height, t21, t22, t23, true);

                this.indexCount += 6;
            }
        }

        indexBuffer.flip();

        return indexBuffer;
    }

    private void addNormalAndShading(FloatBuffer vertexBuffer, int width, int height, int t1, int t2, int t3, boolean invert) {
        Vector v1 = new Vector(vertexBuffer.get(t1*12), vertexBuffer.get(t1*12+1), vertexBuffer.get(t1*12+2));
        Vector v2 = new Vector(vertexBuffer.get(t2*12), vertexBuffer.get(t2*12+1), vertexBuffer.get(t2*12+2));
        Vector v3 = new Vector(vertexBuffer.get(t3*12), vertexBuffer.get(t3*12+1), vertexBuffer.get(t3*12+2));

        Vector normal = Vector.normal(v1, v2, v3);

        if (invert) {
            //normal.scale(-1.0f);
        }

        Vector sun = new Vector(-0.4f, -0.4f, 0.4f);
        Vector up  = new Vector(0.0f, 0.0f, 1.0f);

        float shading = Vector.dot(normal, sun);
        float slope   = 1.0f - (float) Math.pow(Vector.dot(normal, up), 10);
        slope = 1.0f;

        vertexBuffer.put(t1*12+3, normal.getX());
        vertexBuffer.put(t1*12+4, normal.getY());
        vertexBuffer.put(t1*12+5, normal.getZ());

        vertexBuffer.put(t2*12+3, normal.getX());
        vertexBuffer.put(t2*12+4, normal.getY());
        vertexBuffer.put(t2*12+5, normal.getZ());

        vertexBuffer.put(t3*12+3, normal.getX());
        vertexBuffer.put(t3*12+4, normal.getY());
        vertexBuffer.put(t3*12+5, normal.getZ());

        vertexBuffer.put(t1*12+6, shading);
        vertexBuffer.put(t1*12+7, shading);
        vertexBuffer.put(t1*12+8, shading);
        vertexBuffer.put(t1*12+9, slope);

        vertexBuffer.put(t2*12+6, shading);
        vertexBuffer.put(t2*12+7, shading);
        vertexBuffer.put(t2*12+8, shading);
        vertexBuffer.put(t2*12+9, slope);

        vertexBuffer.put(t3*12+6, shading);
        vertexBuffer.put(t3*12+7, shading);
        vertexBuffer.put(t3*12+8, shading);
        vertexBuffer.put(t3*12+9, slope);
    }

    public void render() {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();

        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

        gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexHandle[0]);
        gl2.glVertexPointer(3, GL.GL_FLOAT, 12*Buffers.SIZEOF_FLOAT, 0);
        gl2.glNormalPointer(GL.GL_FLOAT, 12*Buffers.SIZEOF_FLOAT, 3*Buffers.SIZEOF_FLOAT);
        gl2.glColorPointer(3, GL.GL_FLOAT, 12*Buffers.SIZEOF_FLOAT, 6*Buffers.SIZEOF_FLOAT);
        gl2.glTexCoordPointer(2, GL.GL_FLOAT, 12*Buffers.SIZEOF_FLOAT, 10*Buffers.SIZEOF_FLOAT);

        this.defaultMaterial.enable();

        gl2.glDrawElements(GL.GL_TRIANGLES, this.indexCount, GL.GL_UNSIGNED_INT, 0);

        this.defaultMaterial.disable();

        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    }
}

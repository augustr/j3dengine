package gfx.opengl;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import gfx.*;
import gfx.math.*;

public class OpenGLSurface implements Surface {
    private int[]          vertexHandle              = new int[1];
    private int            vertexCount               = 0;
    private int[]          indexHandle               = new int[1];
    private int            indexCount                = 0;
    private Material       defaultMaterial           = null;
    private Material       slopeMaterial             = null;
    private Material       lowMaterial               = null;
    private float          slope                     = 0.5f;
    private float          elavation                 = 0.1f;
    private OpenGLRenderer renderer                  = null;
    private OpenGLShader   shader                    = null;

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

        float scale    = 0.5f;
        float hScale   = 0.2f*scale;
        float vScale   = 10.0f*scale;
        float texScale = 0.5f*hScale;

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

        // Create and load shaders
        this.shader = new OpenGLShader(this.renderer);
        this.shader.loadVertexShaderFromFile("shaders/surface");

        // Enable the shader
        this.shader.enable();

        // Bind shader to materials
        ((OpenGLMaterial) this.defaultMaterial).bindShader(this.shader);
        ((OpenGLMaterial) this.slopeMaterial).bindShader(this.shader);
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
        long xIndexes = width;
        long yIndexes = height-1;

        for (int y = 0; y < yIndexes; y++) {
            for (int x = 0; x < xIndexes; x++) {
                int v1 = x * height + y;
                int v2 = x * height + y + 1;

                // Add degenerate triangle for opengl triangle strip
                if (x == 0 && y > 0 && y < yIndexes-1) {
                    indexBuffer.put(v1);
                    this.indexCount += 1;
                }

                indexBuffer.put(v1);
                indexBuffer.put(v2);
                this.indexCount += 2;

                // Add degenerate triangle for opengl triangle strip
                if (x == xIndexes-1 && y > 0 && y < yIndexes-1) {
                    indexBuffer.put(v2);
                    this.indexCount += 1;
                }
            }
        }

        calculateNormalsAndShading(xIndexes, yIndexes, width, height, heightMap, vertexBuffer);

        // Interpolate the normals to get a smooth rendering of the terrain
        interpolateNormals(xIndexes, yIndexes, width, height, vertexBuffer);

        indexBuffer.flip();

        return indexBuffer;
    }

    private void calculateNormalsAndShading(long xIndexes, long yIndexes, int width, int height, float[][] heightMap, FloatBuffer vertexBuffer) {
        // TODO: Handle edge cases
        for (int x = 1; x < xIndexes-1; x++) {
            for (int y = 1; y < yIndexes-1; y++) {
                // Get indexes for left, right, top and bottom vertice with respect to this one
                int vertex  = x*height + y;
                int vertexL = (x-1)*height + y;
                int vertexR = (x+1)*height + y;
                int vertexT = x*height + (y-1);
                int vertexB = x*height + (y+1);

                Vector v  = new Vector(vertexBuffer.get(vertex*12),  vertexBuffer.get(vertex*12+1),  vertexBuffer.get(vertex*12+2));
                Vector vL = new Vector(vertexBuffer.get(vertexL*12), vertexBuffer.get(vertexL*12+1), vertexBuffer.get(vertexL*12+2));
                Vector vR = new Vector(vertexBuffer.get(vertexR*12), vertexBuffer.get(vertexR*12+1), vertexBuffer.get(vertexR*12+2));
                Vector vT = new Vector(vertexBuffer.get(vertexT*12), vertexBuffer.get(vertexT*12+1), vertexBuffer.get(vertexT*12+2));
                Vector vB = new Vector(vertexBuffer.get(vertexB*12), vertexBuffer.get(vertexB*12+1), vertexBuffer.get(vertexB*12+2));

                float dfdxL = (v.getZ() - vL.getZ())/(v.getX() - vL.getX());
                float dfdxR = (v.getZ() - vR.getZ())/(v.getX() - vR.getX());
                float dfdyT = (v.getZ() - vT.getZ())/(v.getY() - vT.getY());
                float dfdyB = (v.getZ() - vB.getZ())/(v.getY() - vB.getY());

                float dfdx = (dfdxL + dfdxR)/2.0f;
                float dfdy = (dfdyT + dfdyB)/2.0f;

                Vector normal = new Vector(-dfdx, -dfdy, 1.0f);
                normal.normalize();
                vertexBuffer.put(vertex*12+3, normal.getX());
                vertexBuffer.put(vertex*12+4, normal.getY());
                vertexBuffer.put(vertex*12+5, normal.getZ());

                // Calculate light shading and slope
                Vector sun = new Vector(0.4f, -0.4f, 0.4f);
                Vector up  = new Vector(0.0f, 0.0f, 1.0f);
                sun.normalize();

                float shading = Vector.dot(normal, sun);
                float slope   = 1.0f - (float) Math.pow(Vector.dot(normal, up), 10);

                vertexBuffer.put(vertex*12+6, shading);
                vertexBuffer.put(vertex*12+7, shading);
                vertexBuffer.put(vertex*12+8, shading);
                vertexBuffer.put(vertex*12+9, slope);
            }
        }
    }

    private void interpolateNormals(long xIndexes, long yIndexes, int width, int height, FloatBuffer vertexBuffer) {
        // Create temporary hashmap for storing intermediate results
        HashMap<Integer, Vector> newNormals = new HashMap<Integer, Vector>();

        // Interpolate normals to get smooth terrain
        for (int x = 1; x < xIndexes-1; x++) {
            for (int y = 1; y < yIndexes-1; y++) {
                // Get indexes for left, right, top and bottom vertice with respect to this one
                int vertice  = x*height + y;
                int verticeL = (x-1)*height + y;
                int verticeR = (x+1)*height + y;
                int verticeT = x*height + (y-1);
                int verticeB = x*height + (y+1);

                // For left, right, top and bottom vertices outside the range, assign the zero vector which then won't
                // add anything to the total sum.
                Vector normalL = x > 0      ? new Vector(vertexBuffer.get(verticeL*12+3), vertexBuffer.get(verticeL*12+4), vertexBuffer.get(verticeL*12+5)) : new Vector();
                Vector normalR = x < width  ? new Vector(vertexBuffer.get(verticeR*12+3), vertexBuffer.get(verticeR*12+4), vertexBuffer.get(verticeR*12+5)) : new Vector();
                Vector normalT = y > 0      ? new Vector(vertexBuffer.get(verticeT*12+3), vertexBuffer.get(verticeT*12+4), vertexBuffer.get(verticeT*12+5)) : new Vector();
                Vector normalB = y < height ? new Vector(vertexBuffer.get(verticeB*12+3), vertexBuffer.get(verticeB*12+4), vertexBuffer.get(verticeB*12+5)) : new Vector();

                // Calculate the interpolated normal and assign
                Vector newNormal = Vector.add(normalL, normalR, normalT, normalB);
                newNormal.normalize();
                newNormals.put(vertice, newNormal);
            }
        }

        // Overwrite all normals
        for (Map.Entry<Integer, Vector> entry : newNormals.entrySet()) {
            vertexBuffer.put(entry.getKey()*12+3, entry.getValue().getX());
            vertexBuffer.put(entry.getKey()*12+4, entry.getValue().getY());
            vertexBuffer.put(entry.getKey()*12+5, entry.getValue().getZ());
        }
    }

    public void render() {
        GL2 gl2 = renderer.getGLAutoDrawable().getGL().getGL2();

        this.defaultMaterial.enable();
        this.slopeMaterial.enable();

        this.shader.beginRender();

        gl2.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 12*Buffers.SIZEOF_FLOAT, 0);
        gl2.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 12*Buffers.SIZEOF_FLOAT, 3*Buffers.SIZEOF_FLOAT);
        gl2.glVertexAttribPointer(2, 4, GL.GL_FLOAT, false, 12*Buffers.SIZEOF_FLOAT, 6*Buffers.SIZEOF_FLOAT);
        gl2.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 12*Buffers.SIZEOF_FLOAT, 10*Buffers.SIZEOF_FLOAT);
        gl2.glEnableVertexAttribArray(0);
        gl2.glEnableVertexAttribArray(1);
        gl2.glEnableVertexAttribArray(2);
        gl2.glEnableVertexAttribArray(3);

        gl2.glDrawElements(GL.GL_TRIANGLE_STRIP, this.indexCount, GL.GL_UNSIGNED_INT, 0);

        this.shader.endRender();

        this.slopeMaterial.disable();
        this.defaultMaterial.disable();
    }
}

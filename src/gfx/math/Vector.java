package gfx.math;

public class Vector {
    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;

    public Vector() {

    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return new String("(" + getX() + "," + getY() + "," + getZ() + ")");
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float length() {
        return (float) Math.sqrt(Vector.dot(this,this));
    }

    public void scale(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    public void normalize() {
        this.scale(1.0f / this.length());
    }

    public static Vector cross(Vector v1, Vector v2) {
        return new Vector(v1.getY() * v2.getZ() - v1.getZ() * v2.getY(),
                          v1.getZ() * v2.getX() - v1.getX() * v2.getZ(),
                          v1.getX() * v2.getY() - v1.getY() * v2.getX());
    }

    public static float dot(Vector v1, Vector v2) {
        return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
    }

    public static Vector add(Vector v1, Vector v2) {
        return new Vector(v1.getX() + v2.getX(),
                          v1.getY() + v2.getY(),
                          v1.getZ() + v2.getZ());
    }

    public static Vector add(Vector v1, Vector v2, Vector v3) {
        return Vector.add(Vector.add(v1, v2), v3);
    }

    public static Vector add(Vector v1, Vector v2, Vector v3, Vector v4) {
        return Vector.add(Vector.add(v1, v2), Vector.add(v3, v4));
    }

    /**
     * Subtracts vector v2 from vector v1
     *
     * @param Vector v1 Vector v1
     * @param Vector v2 Vector v2
     *
     * @return Vector The result of vector v1 subtracted by vector v2
     */
    public static Vector subtract(Vector v1, Vector v2) {
        return new Vector(v1.getX() - v2.getX(),
                          v1.getY() - v2.getY(),
                          v1.getZ() - v2.getZ());
    }

    /**
     * Calculcates the normal vector for the plane defined by vectors v1,v2 and v3
     *
     * @param Vector v1 A vector representing a point in the plane to calculate normal for
     * @param Vector v2 A vector representing a point in the plane to calculate normal for
     * @param Vector v3 A vector representing a point in the plane to calculate normal for
     *
     * @return Vector the plane normal for the plane defined by points v1, v2 and v3
     */
    public static Vector normal(Vector v1, Vector v2, Vector v3) {
        // The plane normal can be found by calculating the cross product between any two vectors inside the plane
        Vector normal = Vector.cross(Vector.subtract(v3, v2), Vector.subtract(v1, v2));
        normal.normalize();
        return normal;
    }
}

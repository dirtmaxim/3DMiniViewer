package viewer;

class Vector3D {
    private float x;
    private float y;
    private float z;

    public Vector3D() {
    }

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vertex startVertex, Vertex endVertex) {
        this.x = endVertex.getX() - startVertex.getX();
        this.y = endVertex.getY() - startVertex.getY();
        this.z = endVertex.getZ() - startVertex.getZ();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float norm() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        Vector3D vector3D = product(this, 1 / norm());
        this.x = vector3D.x;
        this.y = vector3D.y;
        this.z = vector3D.z;
    }

    public static Vector3D add(Vector3D a, Vector3D b) {
        return new Vector3D(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector3D subtract(Vector3D a, Vector3D b) {
        return new Vector3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector3D product(Vector3D a, float b) {
        return new Vector3D(a.x * b, a.y * b, a.z * b);
    }

    public static float scalarProduct(Vector3D a, Vector3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector3D vectorProduct(Vector3D a, Vector3D b) {
        return new Vector3D(a.y * b.z - b.y * a.z, b.x * a.z - a.x * b.z, a.x * b.y - b.x * a.y);
    }
}

class Vector2D {
    private float x;
    private float y;

    public Vector2D() {
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vertex startVertex, Vertex endVertex) {
        this.x = endVertex.getX() - startVertex.getX();
        this.y = endVertex.getY() - startVertex.getY();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float norm() {
        return (float)Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        product(this, 1 / norm());
    }

    public static Vector2D add(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }

    public static Vector2D subtract(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }

    public static Vector2D product(Vector2D a, float b) {
        return new Vector2D(a.x * b, a.y * b);
    }

    public static float scalarProduct(Vector2D a, Vector2D b) {
        return a.x * b.x + a.y * b.y;
    }
}
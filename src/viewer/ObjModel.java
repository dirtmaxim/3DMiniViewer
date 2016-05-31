package viewer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ObjModel {
    public ArrayList<Vertex> v = new ArrayList<>();
    public ArrayList<Texture> vt = new ArrayList<>();
    public ArrayList<Normal> vn = new ArrayList<>();
    public ArrayList<Face> f = new ArrayList<>();
    // These points will not be related to one particular vertex.
    public Vertex minVertex;
    public Vertex maxVertex;
    private BufferedImage textureMap;
    public ObjModel() {
    }

    public void setTextureMap(String textureFileName) throws IOException {
        textureMap = ImageIO.read(new File(textureFileName));
    }

    public BufferedImage getTextureMap() {
        return textureMap;
    }
}

class Vertex {
    private float x;
    private float y;
    private float z;

    public Vertex() {
    }

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
}

class Texture {
    private float x;
    private float y;
    private float z = 0;

    public Texture() {
    }

    public Texture(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Texture(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
}

class Normal {
    private float x;
    private float y;
    private float z;

    public Normal() {
    }

    public Normal(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
}

class Face {
    private int[] numberOfVertex = new int[0];
    private int[] numberOfTexture = new int[0];
    private int[] numberOfNormal = new int[0];

    public Face() {
    }

    public Face(int[] numberOfVertex, int[] numberOfTexture, int[] numberOfNormal) {
        this.numberOfVertex = numberOfVertex;
        this.numberOfTexture = numberOfTexture;
        this.numberOfNormal = numberOfNormal;
    }

    public int[] getNumberOfVertex() {
        return numberOfVertex;
    }

    public void setNumberOfVertex(int[] numberOfVertex) {
        this.numberOfVertex = numberOfVertex;
    }

    public int[] getNumberOfTexture() {
        return numberOfTexture;
    }

    public void setNumberOfTexture(int[] numberOfTexture) {
        this.numberOfTexture = numberOfTexture;
    }

    public int[] getNumberOfNormal() {
        return numberOfNormal;
    }

    public void setNumberOfNormal(int[] numberOfNormal) {
        this.numberOfNormal = numberOfNormal;
    }
}
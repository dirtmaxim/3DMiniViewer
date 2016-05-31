package viewer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class ObjHandler {
    private BufferedImage bufferedImage;
    private Graphics2D graphics2D;
    private ObjModel objModel;
    private short[] zBuffer;
    // Approximate value cause buffer is able to expand when "matrixOfPerspectiveDistortion" is used. Also it is range and value in "zBuffer" can be negative.
    private short depthOfZBuffer = 255;
    private int modelWidth;
    private int modelHeight;
    // Matrices for transformations.
    private Matrix matrixOfView;
    private Matrix matrixOfPerspectiveDistortion;
    private Matrix matrixOfCamera;
    private Matrix matrixOfRotate;
    // Parameters for matrices.
    public Vector3D sourceOfLight = new Vector3D(0, 0, 1);
    public Vector3D centerOfModel = new Vector3D(0, 0, 0);
    public Vector3D eye = new Vector3D(0, 0, 3);
    public Vector3D up = new Vector3D(0, 1, 0);
    // Variables of state.
    private boolean useLight = true;
    private boolean useTexture = true;
    private boolean useShading = true;
    private boolean useCentralProjection = true;
    private float zoom = 0.3f;

    public ObjHandler(float width, float height, ObjModel objModel) {
        this.objModel = objModel;
        bufferedImage = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
        zBuffer = new short[bufferedImage.getHeight() * bufferedImage.getWidth()];
        graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        computeModelSize();
        eye.setZ((objModel.maxVertex.getZ() - objModel.minVertex.getZ()) * 2.2f);
    }

    public ObjModel getObjModel() {
        return objModel;
    }

    public Image getImage() {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public boolean isUseLight() {
        return useLight;
    }

    public void setUseLight(boolean useLight) {
        this.useLight = useLight;
    }

    public boolean isUseTexture() {
        return useTexture;
    }

    public void setUseTexture(boolean useTexture) {
        this.useTexture = useTexture;
    }

    public boolean isUseShading() {
        return useShading;
    }

    public void setUseShading(boolean useShading) {
        this.useShading = useShading;
    }

    public boolean isUseCentralProjection() {
        return useCentralProjection;
    }

    public void setUseCentralProjection(boolean useCentralProjection) {
        this.useCentralProjection = useCentralProjection;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    private void computeModelSize() {
        float width = bufferedImage.getWidth();
        float height = bufferedImage.getHeight();
        float coefficient;
        if (width < height) {
            coefficient = width / objModel.maxVertex.getX() - objModel.minVertex.getX();
            height = coefficient * objModel.maxVertex.getY() - objModel.minVertex.getY();
        }
        else {
            coefficient = height / objModel.maxVertex.getY() - objModel.minVertex.getY();
            width = coefficient * objModel.maxVertex.getX() - objModel.minVertex.getX();
        }
        modelWidth = (int)width;
        modelHeight = (int)height;
    }

    public Vertex toCenterOfAxis(Vertex worldCoordinates) {
        // It needs to rotate object in the center.
        Vertex screenCoordinates = new Vertex();
        screenCoordinates.setX(worldCoordinates.getX() - objModel.maxVertex.getX() / 2 - objModel.minVertex.getX() / 2);
        screenCoordinates.setY(worldCoordinates.getY() - objModel.maxVertex.getY() / 2 - objModel.minVertex.getY() / 2);
        screenCoordinates.setZ(worldCoordinates.getZ() - objModel.maxVertex.getZ() / 2 - objModel.minVertex.getZ() / 2);
        return screenCoordinates;
    }

    private void createMatrixOfView(float leftShift, float topShift) {
        Matrix matrix = Matrix.getIdentityMatrix(4);
        float width = objModel.maxVertex.getX() - objModel.minVertex.getX();
        float height = objModel.maxVertex.getY() - objModel.minVertex.getY();
        float length = objModel.maxVertex.getZ() - objModel.minVertex.getZ();
        matrix.data[0][3] = leftShift;
        matrix.data[1][3] = topShift;
        matrix.data[2][3] = 0;
        matrix.data[0][0] = modelWidth / width * zoom;
        matrix.data[1][1] = -modelHeight / height * zoom;
        matrix.data[2][2] = depthOfZBuffer / length * zoom;
        matrix.data[3][3] = 1;
        matrixOfView = matrix;
    }

    private void createMatrixOfPerspectiveDistortion(float distanceToProjection) {
        Matrix matrix = Matrix.getIdentityMatrix(4);
        matrix.data[3][2] = -1 / distanceToProjection;
        matrixOfPerspectiveDistortion = matrix;
    }

    private void createMatrixOfCamera(Vector3D camera, Vector3D center, Vector3D upVector) {
        Vector3D z = Vector3D.subtract(camera, center);
        z.normalize();
        Vector3D x = Vector3D.vectorProduct(upVector, z);
        x.normalize();
        Vector3D y = Vector3D.vectorProduct(z, x);
        y.normalize();
        Matrix a = Matrix.getIdentityMatrix(4);
        Matrix b = Matrix.getIdentityMatrix(4);
        a.data[0][0] = x.getX();
        a.data[0][1] = x.getY();
        a.data[0][2] = x.getZ();
        a.data[1][0] = y.getX();
        a.data[1][1] = y.getY();
        a.data[1][2] = y.getZ();
        a.data[2][0] = z.getX();
        a.data[2][1] = z.getY();
        a.data[2][2] = z.getZ();
        b.data[0][3] = -center.getX();
        b.data[1][3] = -center.getY();
        b.data[2][3] = -center.getZ();
        matrixOfCamera =  Matrix.product(a, b);
    }

    private void createMatrixOfRotate(float alongTheX, float alongTheY) {
        Matrix rotateAlongTheX  = Matrix.getIdentityMatrix(4);
        rotateAlongTheX.data[1][1] = (float)Math.cos(Math.toRadians(alongTheX));
        rotateAlongTheX.data[1][2] = (float)(Math.sin(Math.toRadians(alongTheX)));
        rotateAlongTheX.data[2][1] = -(float)Math.sin(Math.toRadians(alongTheX));
        rotateAlongTheX.data[2][2] = (float)Math.cos(Math.toRadians(alongTheX));
        rotateAlongTheX.data[3][3] = 1;
        Matrix rotateAlongTheY = Matrix.getIdentityMatrix(4);
        rotateAlongTheY.data[0][0] = (float)Math.cos(Math.toRadians(alongTheY));
        rotateAlongTheY.data[0][2] = (float)(-Math.sin(Math.toRadians(alongTheY)));
        rotateAlongTheY.data[1][1] = 1;
        rotateAlongTheY.data[2][0] = (float)Math.sin(Math.toRadians(alongTheY));
        rotateAlongTheY.data[2][2] = (float)Math.cos(Math.toRadians(alongTheY));
        rotateAlongTheY.data[3][3] = 1;
        matrixOfRotate =  Matrix.product(rotateAlongTheX, rotateAlongTheY);
    }

    private int setIntensityOfColor(int colorRGB, float intensityOfLight) {
        // Optimization.
        if (intensityOfLight == 0) {
            return 0;
        }
        int red = (colorRGB >> 16) & 0xFF;
        red *= intensityOfLight;
        int green = (colorRGB >> 8) & 0xFF;
        green *= intensityOfLight;
        int blue = colorRGB & 0xFF;
        blue *= intensityOfLight;
        return  (red << 16) | (green << 8) | blue;
    }

    private Vector3D findBarycentricCoordinatesOfTriangle(Vector3D[] arrayOfVertices, Vector2D currentPoint) {
        // Solution of system of equations by matrix method.
        Vector3D vector3D = Vector3D.vectorProduct(new Vector3D(arrayOfVertices[2].getX() - arrayOfVertices[0].getX(), arrayOfVertices[1].getX() - arrayOfVertices[0].getX(), arrayOfVertices[0].getX() - currentPoint.getX()), new Vector3D(arrayOfVertices[2].getY() - arrayOfVertices[0].getY(), arrayOfVertices[1].getY() - arrayOfVertices[0].getY(), arrayOfVertices[0].getY() - currentPoint.getY()));
        if (Math.abs(vector3D.getZ()) < 1) {
            // If triangle is degenerate then return solution with negative coordinates in order to exclude this point.
           return new Vector3D(-1, -1, -1);
        }
        // Division by "vector3D.getZ()" as a part of system solution.
        return new Vector3D(1 - (vector3D.getX() + vector3D.getY()) / vector3D.getZ(), vector3D.getY() / vector3D.getZ(), vector3D.getX() / vector3D.getZ());
    }

    private void fillTriangle(Vector3D[] arrayOfVertices, Texture[] textures, Normal[] normals, float intensityOfLight) {
        Vector2D minSizeOfImage = new Vector2D(bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1);
        Vector2D maxSizeOfImage = new Vector2D(0, 0);
        for (int i = 0; i < arrayOfVertices.length; i++) {
            minSizeOfImage.setX(Math.max(0, Math.min(minSizeOfImage.getX(), arrayOfVertices[i].getX())));
            minSizeOfImage.setY(Math.max(0, Math.min(minSizeOfImage.getY(), arrayOfVertices[i].getY())));
            maxSizeOfImage.setX(Math.min(bufferedImage.getWidth() - 1, Math.max(maxSizeOfImage.getX(), arrayOfVertices[i].getX())));
            maxSizeOfImage.setY(Math.min(bufferedImage.getHeight() - 1, Math.max(maxSizeOfImage.getY(), arrayOfVertices[i].getY())));
        }
        float[] intensityOfVertices = null;
        if (useShading) {
            // It needs for Gouraud shading. Preparing for interpolating.
            Vector3D[] fromNormals = new Vector3D[3];
            fromNormals[0] = new Vector3D(normals[0].getX(), normals[0].getY(), normals[0].getZ());
            fromNormals[0].normalize();
            fromNormals[1] = new Vector3D(normals[1].getX(), normals[1].getY(), normals[1].getZ());
            fromNormals[1].normalize();
            fromNormals[2] = new Vector3D(normals[2].getX(), normals[2].getY(), normals[2].getZ());
            fromNormals[2].normalize();
            sourceOfLight.normalize();
            intensityOfVertices= new float[3];
            intensityOfVertices[0] = Vector3D.scalarProduct(fromNormals[0], sourceOfLight);
            intensityOfVertices[1] = Vector3D.scalarProduct(fromNormals[1], sourceOfLight);
            intensityOfVertices[2] = Vector3D.scalarProduct(fromNormals[2], sourceOfLight);
        }
        for (int y = (int)minSizeOfImage.getY(); y < maxSizeOfImage.getY(); y++) {
            for (int x = (int)minSizeOfImage.getX(); x < maxSizeOfImage.getX(); x++) {
               Vector3D barycentricCoordinates = findBarycentricCoordinatesOfTriangle(arrayOfVertices, new Vector2D(x, y));
                if (barycentricCoordinates.getX() < 0 || barycentricCoordinates.getY() < 0 || barycentricCoordinates.getZ() < 0) {
                    continue;
                }
                // Deduce z-coordinate using weighted arithmetic mean.
                short z = (short)(arrayOfVertices[0].getZ() * barycentricCoordinates.getX() + arrayOfVertices[1].getZ() * barycentricCoordinates.getY() + arrayOfVertices[2].getZ() * barycentricCoordinates.getZ());
                int index = x + y * bufferedImage.getWidth();
                if (zBuffer[index] < z) {
                    zBuffer[index] = z;
                    int color = 0;
                    if (useTexture && objModel.getTextureMap() != null && textures != null) {
                        // Deduce useTexture coordinates using weighted arithmetic mean.
                        short xTextureCoordinate = (short) ((textures[0].getX() * barycentricCoordinates.getX() + textures[1].getX() * barycentricCoordinates.getY() + textures[2].getX() * barycentricCoordinates.getZ()) * objModel.getTextureMap().getWidth());
                        short yTextureCoordinate = (short) ((objModel.getTextureMap().getHeight() - 1) - ((textures[0].getY() * barycentricCoordinates.getX() + textures[1].getY() * barycentricCoordinates.getY() + textures[2].getY() * barycentricCoordinates.getZ()) * objModel.getTextureMap().getHeight()));
                        color = objModel.getTextureMap().getRGB(xTextureCoordinate, yTextureCoordinate);
                    }
                    if (useLight) {
                        if (!useShading) {
                            if (intensityOfLight < 0.1f) {
                                intensityOfLight = 0.1f;
                            }
                            if (useTexture && objModel.getTextureMap() != null && textures != null) {
                                color = setIntensityOfColor(color, intensityOfLight);
                            } else {
                                color = new Color(intensityOfLight, intensityOfLight, intensityOfLight).getRGB();
                            }
                            bufferedImage.setRGB(x, y, color);
                        }
                        else {
                            // Performing shading.
                            float intensityOfPointOfLight = intensityOfVertices[0] * barycentricCoordinates.getX() + intensityOfVertices[1] * barycentricCoordinates.getY() + intensityOfVertices[2] * barycentricCoordinates.getZ();
                            if (intensityOfPointOfLight < 0.1f) {
                                intensityOfPointOfLight = 0.1f;
                            }
                            // In order to except rounding error.
                            else if (intensityOfPointOfLight > 1) {
                                intensityOfPointOfLight = 1;
                            }
                            if (useTexture && objModel.getTextureMap() != null && textures != null) {
                                color = setIntensityOfColor(color, intensityOfPointOfLight);
                            } else {
                                color = new Color(intensityOfPointOfLight, intensityOfPointOfLight, intensityOfPointOfLight).getRGB();
                            }
                            bufferedImage.setRGB(x, y, color);
                        }
                    }
                    else {
                        if (useTexture && objModel.getTextureMap() != null && textures != null) {
                            bufferedImage.setRGB(x, y, color);
                        }
                        else {
                            bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }
            }
        }
    }

    public void fillPolygon(Vector3D[] pointsOfPolygon, Texture[] textures, Normal[] normals, float intensityOfLight) {
        if (pointsOfPolygon.length < 3) {
            return;
        }
        // Splitting the polygon into triangles, it works only for convex polygons.
        for (int i = 1, j = 2; j < pointsOfPolygon.length; i++, j++) {
            fillTriangle(new Vector3D[] {pointsOfPolygon[0], pointsOfPolygon[i], pointsOfPolygon[j]}, (textures == null) ? null : new Texture[] {textures[0], textures[i], textures[j]}, new Normal[] {normals[0], normals[i], normals[j]}, intensityOfLight);
        }
    }

    public void cordRender(double bottomSliderValue, double rightSliderValue) {
        // long start = System.currentTimeMillis();
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        float leftShift = (bufferedImage.getWidth() - modelWidth) / 2 + modelWidth / 2;
        float topShift = (bufferedImage.getHeight() - modelHeight) / 2 + modelHeight / 2;
        // Calculating distanse for perspective distortion.
        bottomSliderValue = 180 - bottomSliderValue;
        rightSliderValue = 180 - rightSliderValue;
        createMatrixOfView(leftShift, topShift);
        if (useCentralProjection) {
            createMatrixOfPerspectiveDistortion(Vector3D.subtract(eye, centerOfModel).norm());
        }
        createMatrixOfCamera(eye, centerOfModel, up);
        createMatrixOfRotate((float)rightSliderValue, (float)bottomSliderValue);
        graphics2D.setColor(Color.WHITE);
        for (int i = 0; i < objModel.f.size(); i++) {
            Face face = objModel.f.get(i);
            Vector3D[] screenCoordinates = new Vector3D[face.getNumberOfVertex().length];
            Polygon polygon = new Polygon();
            for (int j = 0; j < face.getNumberOfVertex().length; j++) {
                if (useCentralProjection) {
                    screenCoordinates[j] = Matrix.matrixToVector(Matrix.product(matrixOfView, Matrix.product(matrixOfPerspectiveDistortion, Matrix.product(matrixOfCamera, Matrix.product(matrixOfRotate, Matrix.vertexToMatrix(toCenterOfAxis(objModel.v.get(face.getNumberOfVertex()[j]))))))));
                }
                else {
                    screenCoordinates[j] = Matrix.matrixToVector(Matrix.product(matrixOfView, Matrix.product(matrixOfCamera, Matrix.product(matrixOfRotate, Matrix.vertexToMatrix(toCenterOfAxis(objModel.v.get(face.getNumberOfVertex()[j])))))));
                }
                polygon.addPoint((int)screenCoordinates[j].getX(), (int)screenCoordinates[j].getY());
            }
            graphics2D.drawPolygon(polygon);
        }
        // long finish = System.currentTimeMillis();
        // System.out.println("cordRender: " + (finish - start) + "ms.");
    }

    public void render(double bottomSliderValue, double rightSliderValue) {
        // long start = System.currentTimeMillis();
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        float leftShift = (bufferedImage.getWidth() - modelWidth) / 2 + modelWidth / 2;
        float topShift = (bufferedImage.getHeight() - modelHeight) / 2 + modelHeight / 2;
        // Calculating distanse for perspective distortion.
        bottomSliderValue = 180 - bottomSliderValue;
        rightSliderValue = 180 - rightSliderValue;
        createMatrixOfView(leftShift, topShift);
        if (useCentralProjection) {
            createMatrixOfPerspectiveDistortion(Vector3D.subtract(eye, centerOfModel).norm());
        }
        createMatrixOfCamera(eye, centerOfModel, up);
        createMatrixOfRotate((float)rightSliderValue, (float)bottomSliderValue);
        for (int i = 0; i < zBuffer.length; i++) {
            zBuffer[i] = Short.MIN_VALUE;
        }
        for (int i = 0; i < objModel.f.size(); i++) {
            Face face = objModel.f.get(i);
            Vector3D[] screenCoordinates = new Vector3D[face.getNumberOfVertex().length];
            for (int j = 0; j < face.getNumberOfVertex().length; j++) {
                if (useCentralProjection) {
                    screenCoordinates[j] = Matrix.matrixToVector(Matrix.product(matrixOfView, Matrix.product(matrixOfPerspectiveDistortion, Matrix.product(matrixOfCamera, Matrix.product(matrixOfRotate, Matrix.vertexToMatrix(toCenterOfAxis(objModel.v.get(face.getNumberOfVertex()[j]))))))));
                }
                else {
                    screenCoordinates[j] = Matrix.matrixToVector(Matrix.product(matrixOfView, Matrix.product(matrixOfCamera, Matrix.product(matrixOfRotate, Matrix.vertexToMatrix(toCenterOfAxis(objModel.v.get(face.getNumberOfVertex()[j])))))));
                }
            }
            // We need only two vectors to deduce normal to current polygon.
            Vertex[] verticesOfTwoVectors = new Vertex[3];
            // Common vertex.
            verticesOfTwoVectors[0] = objModel.v.get(face.getNumberOfVertex()[0]);
            // Vertex of first vector.
            verticesOfTwoVectors[1] = objModel.v.get(face.getNumberOfVertex()[1]);
            // Vertex of second vector.
            verticesOfTwoVectors[2] = objModel.v.get(face.getNumberOfVertex()[face.getNumberOfVertex().length - 1]);
            float intensityOfLight = 0;
            if (!useShading) {
                Vector3D normal = Vector3D.vectorProduct(new Vector3D(verticesOfTwoVectors[0], verticesOfTwoVectors[1]), new Vector3D(verticesOfTwoVectors[0], verticesOfTwoVectors[2]));
                normal.normalize();
                sourceOfLight.normalize();
                intensityOfLight = Vector3D.scalarProduct(normal, sourceOfLight);
            }
            Texture[] textures = null;
            if (objModel.vt.size() != 0) {
                textures = new Texture[face.getNumberOfTexture().length];
                for (int j = 0; j < face.getNumberOfTexture().length; j++) {
                    textures[j] = objModel.vt.get(face.getNumberOfTexture()[j]);
                }
            }
            Normal[] normals = null;
            if (objModel.vn.size() != 0) {
                normals = new Normal[face.getNumberOfNormal().length];
                for (int j = 0; j < face.getNumberOfNormal().length; j++) {
                    normals[j] = objModel.vn.get(face.getNumberOfNormal()[j]);
                }
            }
            fillPolygon(screenCoordinates, textures, normals, intensityOfLight);
        }
        // long finish = System.currentTimeMillis();
        // System.out.println("render: " + (finish - start) + "ms.");
    }
}
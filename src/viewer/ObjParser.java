package viewer;

import java.io.*;

public class ObjParser {
    private ObjModel objModel = new ObjModel();

    ObjParser() {
    }

    ObjParser(String objFileName) {
        setObjModel(objFileName);
    }

    ObjParser(String objFileName, String textureFileName) {
        setObjModel(objFileName, textureFileName);
    }

    public void setObjModel(String objFileName) {
        setObjModel(objFileName, null);
    }

    public void setObjModel(String objFileName, String textureFileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(objFileName));
            if (textureFileName != null) {
                objModel.setTextureMap(textureFileName);
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splittedLine;
                if (line.startsWith("v ")) {
                    splittedLine = line.split("\\s+");
                    float x = Float.parseFloat(splittedLine[1]);
                    float y = Float.parseFloat(splittedLine[2]);
                    float z = Float.parseFloat(splittedLine[3]);
                    if (objModel.maxVertex == null && objModel.minVertex == null) {
                        objModel.maxVertex = new Vertex(x, y, z);
                        objModel.minVertex = new Vertex(x, y, z);
                    }
                    else {
                        if (x > objModel.maxVertex.getX()) {
                            objModel.maxVertex.setX(x);
                        }
                        else if (x < objModel.minVertex.getX()) {
                            objModel.minVertex.setX(x);
                        }
                        if (y > objModel.maxVertex.getY()) {
                            objModel.maxVertex.setY(y);
                        }
                        else if (y < objModel.minVertex.getY()) {
                            objModel.minVertex.setY(y);
                        }
                        if (z > objModel.maxVertex.getZ()) {
                            objModel.maxVertex.setZ(z);
                        }
                        else if (z < objModel.minVertex.getZ()) {
                            objModel.minVertex.setZ(z);
                        }
                    }
                    Vertex vertex = new Vertex(x, y, z);
                    objModel.v.add(vertex);
                }
                else if (line.startsWith("vt ")) {
                    splittedLine = line.split("\\s+");
                    if (splittedLine.length == 3) {
                        float x = Float.parseFloat(splittedLine[1]);
                        float y = Float.parseFloat(splittedLine[2]);
                        Texture texture = new Texture(x, y);
                        objModel.vt.add(texture);
                    }
                    else {
                        float x = Float.parseFloat(splittedLine[1]);
                        float y = Float.parseFloat(splittedLine[2]);
                        float z = Float.parseFloat(splittedLine[3]);
                        Texture texture = new Texture(x, y, z);
                        objModel.vt.add(texture);
                    }
                }
                else if (line.startsWith("vn ")) {
                    splittedLine = line.split("\\s+");
                    float x = Float.parseFloat(splittedLine[1]);
                    float y = Float.parseFloat(splittedLine[2]);
                    float z = Float.parseFloat(splittedLine[3]);
                    Normal normal = new Normal(x, y, z);
                    objModel.vn.add(normal);
                }
                else if (line.startsWith("f ")) {
                    splittedLine = line.split("\\s+");
                    String[][] splittedBySlash = new String[splittedLine.length - 1][];
                    Face face = new Face();
                    for (int i = 0; i < splittedLine.length - 1; i++) {
                        splittedBySlash[i] = splittedLine[i + 1].split("/");
                    }
                    if (splittedBySlash[0].length == 1) {
                        int[] numberOfVertex = new int[splittedBySlash.length];
                        for (int i = 0; i < splittedBySlash.length; i++) {
                            numberOfVertex[i] = Integer.parseInt(splittedBySlash[i][0]) - 1;
                        }
                        face.setNumberOfVertex(numberOfVertex);
                    }
                    if (splittedBySlash[0].length == 2) {
                        int[] numberOfVertex = new int[splittedBySlash.length];
                        int[] numberOfTexture = new int[splittedBySlash.length];
                        for (int i = 0; i < splittedBySlash.length; i++) {
                            numberOfVertex[i] = Integer.parseInt(splittedBySlash[i][0]) - 1;
                            numberOfTexture[i] = Integer.parseInt(splittedBySlash[i][1]) - 1;
                        }
                        face.setNumberOfVertex(numberOfVertex);
                        face.setNumberOfTexture(numberOfTexture);
                    }
                    if (splittedBySlash[0].length == 3) {
                        if (splittedBySlash[0][1].isEmpty()) {
                            int[] numberOfVertex = new int[splittedBySlash.length];
                            int[] numberOfNormal = new int[splittedBySlash.length];
                            for (int i = 0; i < splittedBySlash.length; i++) {
                                numberOfVertex[i] = Integer.parseInt(splittedBySlash[i][0]) - 1;
                                numberOfNormal[i] = Integer.parseInt(splittedBySlash[i][2]) - 1;
                            }
                            face.setNumberOfVertex(numberOfVertex);
                            face.setNumberOfNormal(numberOfNormal);
                        }
                        else {
                            int[] numberOfVertex = new int[splittedBySlash.length];
                            int[] numberOfTexture = new int[splittedBySlash.length];
                            int[] numberOfNormal = new int[splittedBySlash.length];
                            for (int i = 0; i < splittedBySlash.length; i++) {
                                numberOfVertex[i] = Integer.parseInt(splittedBySlash[i][0]) - 1;
                                numberOfTexture[i] = Integer.parseInt(splittedBySlash[i][1]) - 1;
                                numberOfNormal[i] = Integer.parseInt(splittedBySlash[i][2]) - 1;
                            }
                            face.setNumberOfVertex(numberOfVertex);
                            face.setNumberOfTexture(numberOfTexture);
                            face.setNumberOfNormal(numberOfNormal);
                        }
                    }
                    objModel.f.add(face);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File was not found.");
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("Some troubles occurred during file parsing.");
            System.exit(0);
        }
    }
    public ObjModel getObjModel() {
        return objModel;
    }
}
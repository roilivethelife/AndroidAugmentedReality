package com.example.roi.climaar.modelo.figuras.ObjLoader;


import android.content.Context;
import android.util.Log;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.figuras.Textura;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjReader {
    // Tokens for parsing.
    private final static String OBJ_VERTEX_TEXTURE = "vt";
    private final static String OBJ_VERTEX_NORMAL = "vn";
    private final static String OBJ_VERTEX = "v";
    private final static String OBJ_FACE = "f";
    private final static String OBJ_GROUP_NAME = "g";
    private final static String OBJ_OBJECT_NAME = "o";
    private final static String OBJ_SMOOTHING_GROUP = "s";
    private final static String OBJ_POINT = "p";
    private final static String OBJ_LINE = "l";
    private final static String OBJ_MAPLIB = "maplib";
    private final static String OBJ_USEMAP = "usemap";
    private final static String OBJ_MTLLIB = "mtllib";
    private final static String OBJ_USEMTL = "usemtl";
    private final static String MTL_NEWMTL = "newmtl";
    private final static String MTL_KA = "Ka";
    private final static String MTL_KD = "Kd";
    private final static String MTL_KS = "Ks";
    private final static String MTL_TF = "Tf";
    private final static String MTL_ILLUM = "illum";
    private final static String MTL_D = "d";
    private final static String MTL_D_DASHHALO = "-halo";
    private final static String MTL_NS = "Ns";
    private final static String MTL_SHARPNESS = "sharpness";
    private final static String MTL_NI = "Ni";
    private final static String MTL_MAP_KA = "map_Ka";
    private final static String MTL_MAP_KD = "map_Kd";
    private final static String MTL_MAP_KS = "map_Ks";
    private final static String MTL_MAP_NS = "map_Ns";
    private final static String MTL_MAP_D = "map_d";
    private final static String MTL_DISP = "disp";
    private final static String MTL_DECAL = "decal";
    private final static String MTL_BUMP = "bump";
    private final static String MTL_REFL = "refl";
    public final static String MTL_REFL_TYPE_SPHERE = "sphere";
    public final static String MTL_REFL_TYPE_CUBE_TOP = "cube_top";
    public final static String MTL_REFL_TYPE_CUBE_BOTTOM = "cube_bottom";
    public final static String MTL_REFL_TYPE_CUBE_FRONT = "cube_front";
    public final static String MTL_REFL_TYPE_CUBE_BACK = "cube_back";
    public final static String MTL_REFL_TYPE_CUBE_LEFT = "cube_left";
    public final static String MTL_REFL_TYPE_CUBE_RIGHT = "cube_right";


    private ArrayList<Float> vertexCoord;
    private ArrayList<Float> vertexNormal;
    private ArrayList<Float> vertexTexture;
    private ArrayList<Integer> faceList;
    private float[] color;
    private boolean hasTexture;
    private String textureFilename;
    private Textura textura;
    private FloatBuffer vbo;
    private ShortBuffer ibo;
    private int numVertices;
    private int resourceId;

    private Context context;


    public ObjReader(Context context, int resourceId) throws IOException{
        vertexCoord = new ArrayList<>();
        vertexNormal = new ArrayList<>();
        vertexTexture = new ArrayList<>();
        faceList = new ArrayList<>();
        color=new float[4];
        hasTexture=false;
        this.resourceId =resourceId;
        this.context = context;
        parseObjFile(resourceId);
        getVboIbo();
    }

    public FloatBuffer getVertexBuffer() {
        return vbo;
    }
    public ShortBuffer getIndexBuffer() {
        return ibo;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public float[] getColor() {
        return color;
    }

    public Textura getTextura() {
        return textura;
    }

    private void parseObjFile(int resourceId) throws IOException{
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        int lineCount = 0;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            // NOTE: we don't check for the space after the char
            // because sometimes it's not there - most notab
            // ly in the
            // grouupname, we seem to get a lot of times where we have
            // "g\n", i.e. setting the group name to blank (or
            // default?)


            if (line.startsWith("#")) // comment
            {
                continue;
            } else if (line.startsWith(OBJ_VERTEX_TEXTURE)) {
                float[] values = StringUtils.parseFloatList(2, line, OBJ_VERTEX_TEXTURE.length());
                vertexTexture.add(values[0]);
                vertexTexture.add(values[1]);
            } else if (line.startsWith(OBJ_VERTEX_NORMAL)) {
                float[] values = StringUtils.parseFloatList(3, line, OBJ_VERTEX_NORMAL.length());
                vertexNormal.add(values[0]);
                vertexNormal.add(values[1]);
                vertexNormal.add(values[2]);
            } else if (line.startsWith(OBJ_VERTEX)) {
                float[] values = StringUtils.parseFloatList(3, line, OBJ_VERTEX.length());
                vertexCoord.add(values[0]);
                vertexCoord.add(values[1]);
                vertexCoord.add(values[2]);
            } else if (line.startsWith(OBJ_FACE)) {
                line = line.substring(OBJ_FACE.length()).trim();
                int[] verticeIndexAry = StringUtils.parseListVerticeNTuples(line, 3);
                for (int tmp : verticeIndexAry) {
                    faceList.add(tmp);
                }

            } else if (line.startsWith(OBJ_GROUP_NAME)) {
                //processGroupName(line);
                continue;
            } else if (line.startsWith(OBJ_OBJECT_NAME)) {
                //processObjectName(line);
                continue;
            } else if (line.startsWith(OBJ_SMOOTHING_GROUP)) {
                //processSmoothingGroup(line);
            } else if (line.startsWith(OBJ_POINT)) {
                //processPoint(line);
            } else if (line.startsWith(OBJ_LINE)) {
                //processLine(line);
            } else if (line.startsWith(OBJ_MAPLIB)) {
                //processMapLib(line);
            } else if (line.startsWith(OBJ_USEMAP)) {
                //processUseMap(line);
            } else if (line.startsWith(OBJ_USEMTL)) {
                //processUseMaterial(line);
            } else if (line.startsWith(OBJ_MTLLIB)) {
                processMaterialLib(line);
            } else {
                Log.d("ObjReadParseLine", "line " + lineCount + " unknown line |" + line + "|");
            }
            lineCount++;

        }

        //Por ultimo cargamos la textura si corresponde
        if(hasTexture){
            String textureFilenameWoExtension = textureFilename.substring(0, textureFilename.lastIndexOf('.'));
            int resID= context.getResources().getIdentifier(textureFilenameWoExtension,"drawable",context.getPackageName());
            textura=new Textura(context,resID);
        }else{
            textura=new Textura(context,R.drawable.texture_default);
        }
    }

    private void getVboIbo(){
        //Cada cara esta formada por 9 ints
        //Por lo tanto faceList.size/9 = numero de caras
        //osea que facelist/3 = numVertices


        /*
        Por cada cara 3 vertices:
            Cada vertice:
                3Coordenadas 0 1 2
                3Normales    3 4 5
                2Textura     6 7
                Total:8floats
        3*8floats=24floats por cara.
         */

        short[] indexBuffer = new short[faceList.size()/3];
        ArrayList<VerticePointer> verticePointers = new ArrayList<>();
        HashMap<VerticePointer,Vertice> vertices = new HashMap<>(faceList.size()/4);

        int vertexCount=0;
        //Recorremos las caras y vamos completando el buffer
        for (int i = 0,j=0; i < faceList.size(); i+=3,j++) {

            VerticePointer verticePointer= new VerticePointer();

            //hay que restar -1 a los indices para que empiecen en 0
            verticePointer.coord_i= faceList.get(i)-1;
            verticePointer.text_i= faceList.get(i+1);
            verticePointer.norm_i= faceList.get(i+2);
            if(verticePointer.text_i!=Integer.MIN_VALUE) verticePointer.text_i--;
            if(verticePointer.norm_i!=Integer.MIN_VALUE) verticePointer.norm_i--;

            //comprobar si el vertice ya existe:
            if(vertices.containsKey(verticePointer)){
                Vertice v = vertices.get(verticePointer);
                indexBuffer[j]=(short)v.i;
            }else {
                Vertice vertice = new Vertice(vertexCount);
                //añadimos vertices coord
                vertice.coord[0] = vertexCoord.get(verticePointer.coord_i * 3);
                vertice.coord[1] = vertexCoord.get(verticePointer.coord_i * 3 + 1);
                vertice.coord[2] = vertexCoord.get(verticePointer.coord_i * 3 + 2);
                //añadimos verticesNormales
                if (verticePointer.norm_i != Integer.MIN_VALUE) {
                    vertice.norm[0] = vertexNormal.get(verticePointer.norm_i * 3);
                    vertice.norm[1] = vertexNormal.get(verticePointer.norm_i * 3 + 1);
                    vertice.norm[2] = vertexNormal.get(verticePointer.norm_i * 3 + 2);
                } else {
                    vertice.norm[0] = 0.0f;
                    vertice.norm[1] = 1.0f;
                    vertice.norm[2] = 0.0f;
                    Log.d("OBJ", "Error cargando obj, no hay normal definida");
                }

                if (verticePointer.text_i != Integer.MIN_VALUE) {
                    vertice.tex[0] = vertexTexture.get(verticePointer.text_i * 2);
                    vertice.tex[1] = 1.0f - vertexTexture.get(verticePointer.text_i * 2 + 1);
                } else {
                    vertice.tex[0] = 0.0f;
                    vertice.tex[1] = 0.0f;
                }
                vertices.put(verticePointer,vertice);
                verticePointers.add(verticePointer);
                indexBuffer[j]=(short)vertexCount;
                vertexCount++;
            }
        }
        numVertices =vertexCount;

        //Creamos buffer de indices
        ByteBuffer bb = ByteBuffer.allocateDirect(indexBuffer.length * 2);//2bytes*short
        bb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        ibo = bb.asShortBuffer();//creamos buffer float
        ibo.put(indexBuffer);
        ibo.position(0);//reseteamos posicion

        //Creamos buffer de objetos
        float[] vboFloat = new float[verticePointers.size()*8];
        int p =0;
        for (VerticePointer vp :verticePointers) {
            Vertice v = vertices.get(vp);
            vboFloat[p++]=v.coord[0];
            vboFloat[p++]=v.coord[1];
            vboFloat[p++]=v.coord[2];
            vboFloat[p++]=v.norm[0];
            vboFloat[p++]=v.norm[1];
            vboFloat[p++]=v.norm[2];
            vboFloat[p++]=v.tex[0];
            vboFloat[p++]=v.tex[1];
        }
        bb = ByteBuffer.allocateDirect(vboFloat.length*4);
        bb.order(ByteOrder.nativeOrder());
        vbo = bb.asFloatBuffer();
        vbo.put(vboFloat);
        vbo.position(0);
        vertices.clear();
        verticePointers.clear();
        indexBuffer = null;
        vboFloat = null;
    }

    private void processMaterialLib(String line) throws IOException {
        String[] matlibnames = StringUtils.parseWhitespaceList(line.substring(OBJ_MTLLIB.length()).trim());

        if (null != matlibnames) {
            for (int loopi = 0; loopi < matlibnames.length; loopi++) {
                try {
                    parseMtlFile(matlibnames[loopi]);
                } catch (FileNotFoundException e) {
                    Log.d("ProcesMAterialLib", "Can't find material file name='" + matlibnames[loopi] + "', e=" + e);
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // material file processing
    // ----------------------------------------------------------------------
    private void parseMtlFile(String mtlFilename) throws IOException {
        int lineCount = 0;
        //String mtlFilenameWOExtension = mtlFilename.substring(0, mtlFilename.lastIndexOf('.'));
        InputStream inputStream = context.getResources().openRawResource(
                context.getResources().getIdentifier(mtlFilename,
                        "raw", context.getPackageName()));
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line = null;

        while (true) {
            line = bufferedReader.readLine();
            if (null == line) {
                break;
            }

            line = line.trim();

            if (line.length() == 0) {
                continue;
            }

            if (line.startsWith("#")) // comment
            {
                continue;
            } else if (line.startsWith(MTL_NEWMTL)) {
                //processNewmtl(line);
            } else if (line.startsWith(MTL_KA)) {
                //processReflectivityTransmissivity(MTL_KA, line);
            } else if (line.startsWith(MTL_KD)) {
                color = processReflectivityTransmissivity(MTL_KD, line);
            } else if (line.startsWith(MTL_KS)) {
                //processReflectivityTransmissivity(MTL_KS, line);
            } else if (line.startsWith(MTL_TF)) {
                //processReflectivityTransmissivity(MTL_TF, line);
            } else if (line.startsWith(MTL_ILLUM)) {
                //processIllum(line);
            } else if (line.startsWith(MTL_D)) {
                //processD(line);
            } else if (line.startsWith(MTL_NS)) {
                //processNs(line);
            } else if (line.startsWith(MTL_SHARPNESS)) {
                //processSharpness(line);
            } else if (line.startsWith(MTL_NI)) {
                //processNi(line);
            } else if (line.startsWith(MTL_MAP_KA)) {
                //processMapDecalDispBump(MTL_MAP_KA, line);
            } else if (line.startsWith(MTL_MAP_KD)) {
                //processMapDecalDispBump(MTL_MAP_KD, line);
                hasTexture=true;
                textureFilename = line.substring(MTL_MAP_KD.length()).trim();
            } else if (line.startsWith(MTL_MAP_KS)) {
                //processMapDecalDispBump(MTL_MAP_KS, line);
            } else if (line.startsWith(MTL_MAP_NS)) {
                //processMapDecalDispBump(MTL_MAP_NS, line);
            } else if (line.startsWith(MTL_MAP_D)) {
                //processMapDecalDispBump(MTL_MAP_D, line);
            } else if (line.startsWith(MTL_DISP)) {
                //processMapDecalDispBump(MTL_DISP, line);
            } else if (line.startsWith(MTL_DECAL)) {
                //processMapDecalDispBump(MTL_DECAL, line);
            } else if (line.startsWith(MTL_BUMP)) {
                //processMapDecalDispBump(MTL_BUMP, line);
            } else if (line.startsWith(MTL_REFL)) {
                //processRefl(line);
            } else {
                Log.d("ParseMTLFile", "line " + lineCount + " unknown line |" + line + "|");

            }
            lineCount++;
        }
        bufferedReader.close();

        Log.i("ParseMTLFile", "Parse.parseMtlFile: Loaded " + lineCount + " lines");
    }

    private float[] processReflectivityTransmissivity(String fieldName, String line) {
        float[] tmp =new float[4];
        tmp[3]=1.0f;
        String[] tokens = StringUtils.parseWhitespaceList(line.substring(fieldName.length()));
        if (null == tokens) {
            //log.log(SEVERE, "Got Ka line with no tokens, line = |" + line + "|");
            return tmp;
        }
        if (tokens.length <= 0) {
            //log.log(SEVERE, "Got Ka line with no tokens, line = |" + line + "|");
            return tmp;
        }
        if (tokens[0].equals("spectral")) {
            // Ka spectral file.rfl factor_num
            //log.log(WARNING, "Sorry Charlie, this parse doesn't handle \'spectral\' parsing.  (Mostly because I can't find any info on the spectra.rfl file.)");
            return tmp;
// 	    if(tokens.length < 2) {
// 		log.log(SEVERE, "Got spectral line with not enough tokens, need at least one token for spectral file and one value for factor, found "+(tokens.length-1)+" line = |"+line+"|");
// 		return;
// 	    }
        } else if (tokens[0].equals("xyz")) {
            // Ka xyz x_num y_num z_num

            if (tokens.length < 2) {
                //log.log(SEVERE, "Got xyz line with not enough x/y/z tokens, need at least one value for x, found " + (tokens.length - 1) + " line = |" + line + "|");
                return tmp;
            }
            tmp[0] = Float.parseFloat(tokens[1]);
            tmp[1] =tmp[0];
            tmp[2] =tmp[0];
            if (tokens.length > 2) {
                tmp[1] = Float.parseFloat(tokens[2]);
            }
            if (tokens.length > 3) {
                tmp[2] = Float.parseFloat(tokens[3]);
            }
            //builder.setXYZ(type, x, y, z);
        } else {
            // Ka r_num g_num b_num
            tmp[0] = Float.parseFloat(tokens[0]);
            tmp[1] = tmp[0];
            tmp[2] = tmp[0];
            if (tokens.length > 1) {
                tmp[1] = Float.parseFloat(tokens[1]);
            }
            if (tokens.length > 2) {
                tmp[2] = Float.parseFloat(tokens[2]);
            }
        }
        return tmp;
    }


    private class VerticePointer{
        int coord_i;
        int norm_i;
        int text_i;

        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + coord_i;
            hash = hash * 31 + norm_i;
            hash = hash * 31 + text_i;
            return hash;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (!(other instanceof VerticePointer))return false;
            VerticePointer otherClass = (VerticePointer) other;
            return (otherClass.coord_i==coord_i &&
                    otherClass.norm_i==norm_i &&
                    otherClass.text_i==text_i );
        }
    }

    private class Vertice{
        int i;
        float[] coord;
        float[] norm;
        float[] tex;
        public Vertice (int i){
            coord = new float[3];
            norm = new float[3];
            tex = new float[2];
            this.i = i;
        }
    }


}

package com.example.roi.climaar.modelo.figuras;

import android.content.Context;

import com.example.roi.climaar.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Created by roi on 27/06/17.
 */

public class Rectangulo extends VboClient {

    private float x,y,z,width,height;


    public Rectangulo(float x, float y,float z, float width, float height) {
        super(FigureType.RECTANGLE);
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        color[0]=1f;
        color[1]=1f;
        color[2]=1f;
        color[3]=1f;
    }

    @Override
    public void loadFigura(Context context) {
        textura = new Textura(context, R.drawable.texture_default);
        updateAtributes();
    }

    public void updateAtributes(){
        ByteBuffer bb = ByteBuffer.allocateDirect(6*8*4);//6vertex*8floatperVertex*4bytesperfloat
        bb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        vertexBuffer = bb.asFloatBuffer();
        numVertices = 6;
        float[] fVertexData = new float[6*8];
        drawRectangleHelper(fVertexData);
        vertexBuffer.put(fVertexData);
        vertexBuffer.position(0);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    private void drawRectangleHelper(float[] fVertexData){
        for (int i = 0; i <6; i++) {
            fVertexData[8*i+2]=z;//z
            fVertexData[8*i+3]=0.0f;//norm_x
            fVertexData[8*i+4]=0.0f;//norm_y
            fVertexData[8*i+5]=1.0f;//norm_z
            fVertexData[8*i+6]=0f;//Texture
            fVertexData[8*i+7]=0f;//Texture
        }
        //B-
        fVertexData[0]=x;
        fVertexData[1]=y;
        //C-
        fVertexData[8]=x+width;
        fVertexData[8+1]=y+height;
        //A-
        fVertexData[16]=x;
        fVertexData[16+1]=y+height;
        //B-
        fVertexData[24]=x;
        fVertexData[24+1]=y;
        //D-
        fVertexData[32]=x+width;
        fVertexData[32+1]=y;
        //C-
        fVertexData[40]=x+width;
        fVertexData[40+1]=y+height;
    }
}

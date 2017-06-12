package com.example.roi.climaar.modelo.figuras.Texto;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.climaar.vista.Shader;
import com.example.roi.climaar.modelo.figuras.Textura;
import com.example.roi.climaar.modelo.figuras.VboClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by roi on 30/04/17.
 */

public class GLText extends VboClient {

    private String text;
    private String fuenteTTF;
    private int size;
    private int padX, padY;
    private float scaleX, scaleY, posX, posY;


    private static final String FONT_DEFAULT = "Roboto-Regular.ttf";
    private static final int SIZE_DEFAULT = 20;


    private int numChars;
    private int maxLength;

    public GLText(String texto){
        this(texto,FONT_DEFAULT,SIZE_DEFAULT);
    }

    public GLText( String text, String fuenteTTF, int size) {
        this(text,0,0,fuenteTTF,size);
    }

    public GLText( String text, int posX, int posY, int size){
        this(text,posX,posY,FONT_DEFAULT,size);
    }

    public GLText( String text, int posX, int posY, String fuenteTTF, int size){
        super(FigureType.TEXT);
        this.text = text;
        this.fuenteTTF = fuenteTTF;
        this.size = size;
        this.padX=0;
        this.padY=0;
        this.scaleX=0;
        this.scaleY=0;
        this.posX=posX;
        this.posY=posY;
        this.maxLength = text.length();
    }

    @Override
    public void loadFigura(Context context) {
        ByteBuffer bb = ByteBuffer.allocateDirect(maxLength*6*8*4);//numChars*6vertexperChar*8floatperVertex*4bytesperfloat
        bb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        vertexBuffer = bb.asFloatBuffer();
        numChars = 0;
        numVertices = 0;
        color[0]=1f;
        color[1]=0f;
        color[2]=0f;
        color[3]=1f;

        GLTextDrawer glTextDrawer = new GLTextDrawer(context.getAssets());
        glTextDrawer.load(fuenteTTF,size,padX,padY);
        glTextDrawer.setScale(scaleX,scaleY);
        glTextDrawer.drawC(this,text,posX,posY);
        isLoaded = true;
    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        GLES20.glEnable( GLES20.GL_BLEND );
        GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
        super.dibujar(shader, modelViewMatrix);
        GLES20.glDisable( GLES20.GL_BLEND );
    }


    public void editText(float[] vertexData, int numChars, Textura textura){
        if(numChars>maxLength){
            Log.e("GLText", "Cadena de mayor tamaño");
        }
        this.textura = textura;
        vertexBuffer.clear();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
        this.numChars = numChars;
        numVertices = numChars*6;
    }

    public String getFuenteTTF() {
        return fuenteTTF;
    }

    public void setFuenteTTF(String fuenteTTF) {
        this.fuenteTTF = fuenteTTF;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPadX() {
        return padX;
    }

    public void setPadX(int padX) {
        this.padX = padX;
    }

    public int getPadY() {
        return padY;
    }

    public void setPadY(int padY) {
        this.padY = padY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getNumChars() {
        return numChars;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}

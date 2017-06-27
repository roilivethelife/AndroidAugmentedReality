package com.example.roi.climaar.modelo.figuras.Texto;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.climaar.modelo.figuras.Rectangulo;
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
    private float posX, posY;


    private int numChars;

    private boolean drawBackground;
    private static final float BORDE_BACKGROUND = 1f;
    private Rectangulo background;

    private transient GLTextDrawer glTextDrawer;

    public GLText(String text) {
        this(text,0,0);
    }


    public GLText( String text, int posX, int posY){
        this(text,posX,posY,false);
    }

    public GLText( String text, int posX, int posY, boolean drawBackground){
        super(FigureType.TEXT);
        this.text = text;
        this.posX=posX;
        this.posY=posY;
        this.drawBackground = drawBackground;
    }

    @Override
    public void loadFigura(Context context) {
        updateAtributes();
        color[0]=1f;
        color[1]=0f;
        color[2]=0f;
        color[3]=1f;

        glTextDrawer = GLTextDrawer.loadText(context.getAssets());
        glTextDrawer.draw(this,text,posX,posY);

        if(drawBackground){
            background = new Rectangulo(
                    posX-BORDE_BACKGROUND,
                    posY+BORDE_BACKGROUND,
                    -1f,
                    glTextDrawer.getLength(text)+BORDE_BACKGROUND,
                    -glTextDrawer.getHeight(text)-BORDE_BACKGROUND);
            background.loadFigura(context);
        }
        isLoaded = true;
    }

    private void updateAtributes() {
        if(vertexBuffer!=null) {
            vertexBuffer.clear();
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(text.length()*6*8*4);//numChars*6vertexperChar*8floatperVertex*4bytesperfloat
        bb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        vertexBuffer = bb.asFloatBuffer();
        numChars = 0;
        numVertices = 0;
    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        if(drawBackground && background!=null){
            background.dibujar(shader, modelViewMatrix);
        }
        GLES20.glEnable( GLES20.GL_BLEND );
        GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
        super.dibujar(shader, modelViewMatrix);
        GLES20.glDisable( GLES20.GL_BLEND );


    }

    public void newText(String text){
        Log.d("GLText","NewText="+text);
        this.text = text;
        updateAtributes();
        background.setHeight(-glTextDrawer.getHeight(text)-BORDE_BACKGROUND);
        background.setWidth(glTextDrawer.getLength(text)+BORDE_BACKGROUND);
        background.updateAtributes();
        glTextDrawer.draw(this,text,posX,posY);
    }


    public void editText(float[] vertexData, int numChars, Textura textura){
        /*if(numChars>maxLength){
            Log.e("GLText", "Cadena de mayor tamaño");
        }*/
        this.textura = textura;
        vertexBuffer.clear();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
        this.numChars = numChars;
        numVertices = numChars*6;
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

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public float getCharHeight() {
        return glTextDrawer.getCharHeight();
    }

    public float getTextHeight(){
        return glTextDrawer.getHeight(text);
    }
}

package com.example.roi.climaar.modelo.figuras;

import android.content.Context;
import android.opengl.Matrix;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.JsonRest.DynamicMapElement;
import com.example.roi.climaar.modelo.JsonRest.WebRestDataInterface;
import com.example.roi.climaar.modelo.figuras.Texto.GLText;
import com.example.roi.climaar.modelo.mapa.MapElement;
import com.example.roi.climaar.vista.Shader;

import java.io.Serializable;

import static java.lang.Float.NaN;

/**
 * Created by roi on 24/04/17.
 */

public class SueloRadiante extends Figura implements Serializable, DynamicMapElement {


    private transient Obj pipe;
    private transient Obj valvula;
    private transient Obj valvulaPipe;
    private transient GLText glText;

    private static final float[] COLOR_BLUE = {0.2f, 0.4f, 1f, 1f};
    private static final float[] COLOR_RED = {1f, 0.07f, 0f, 1f};
    private boolean colorRed = false;

    private static final float ANCHO_PIPE = 5.0f;
    private static final float LARGO_PIPE = 5.0f;
    private static final float SEP_TUBERIAS = 30f;


    private float ancho;
    private float largo;
    private transient float posXValvula;


    private static int resourceIDpipe = R.raw.pipe;
    private static int resourceIDvalvPipe = R.raw.valvulapipe;
    private static int resourceIDvalv = R.raw.valvula;

    private static final String VALVULA_CERRADA = "VALVULA\nCERRADA";
    private static final String VALVULA_ABIERTA = "VALVULA\nABIERTA";

    //VariablesWebRest
    private boolean bSRvalvulaAbierta; //CITIUS_SR_P2_Actuadores_209
    private boolean bSRmodoFrio;//CITIUS_BC_Sel_Calor_Frio_SR"
    private float fSRtempImpulsion; //CITIUS_BC_Temp_Impulsion_SR
    private float fSRtempRetorno; //CITIUS_BC_Temp_Retorno_SR


    public SueloRadiante(int ancho, int largo) {
        super(FigureType.SUELO_RADIANTE);
        this.ancho = ancho;
        this.largo = largo;
        isDynamic = true;

        bSRvalvulaAbierta=false;
        bSRmodoFrio=false;
        fSRtempImpulsion=NaN;
        fSRtempRetorno=NaN;
    }


    public void setColorRed(boolean red) {
        colorRed = red;
    }

    @Override
    public void loadFigura(Context context) {
        pipe = new Obj(resourceIDpipe);
        valvula = new Obj(resourceIDvalv);
        valvulaPipe = new Obj(resourceIDvalvPipe);
        glText = new GLText(VALVULA_CERRADA,0,0,true);
        pipe.loadFigura(context);
        valvula.loadFigura(context);
        valvulaPipe.loadFigura(context);
        glText.loadFigura(context);
        posXValvula = (float)Math.floor(ancho/SEP_TUBERIAS/2) * SEP_TUBERIAS - SEP_TUBERIAS/1.5f;
        isLoaded = true;
    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        float[] modelViewTemp = new float[16];


        //Configuramos color
        if (colorRed) {
            pipe.color = COLOR_RED;
        } else {
            pipe.color = COLOR_BLUE;
        }

        //Dibujar valvula
        Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, posXValvula, 0, 0);
        valvulaPipe.dibujar(shader, modelViewTemp);
        if(!bSRvalvulaAbierta) {
            Matrix.rotateM(modelViewTemp, 0, -90, 0, 1, 0);//Rotamos 90º
        }
        valvula.dibujar(shader, modelViewTemp);


        //Suponer que sueloRadiante no tiene escala
        float elevacionTexto = 10+glText.getTextHeight();
        Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, posXValvula, elevacionTexto, 0);
        removeRotation(modelViewTemp);
        glText.dibujar(shader,modelViewTemp);




        //Dibujar tuberias
        for (int x = 0, j = 0; x < (ancho); x += (int) SEP_TUBERIAS, j++) {
            Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, x, 0, 0);
            Matrix.scaleM(modelViewTemp, 0, 1f, 1f, largo / LARGO_PIPE);
            pipe.dibujar(shader, modelViewTemp);

            //Dibujamos lateral
            if (j % 2 == 0) {//si es par dibujamos lateral en 0
                Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, x, 0, 0);
            } else {//si es impar en largo
                Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, x, 0, largo);
            }
            Matrix.rotateM(modelViewTemp, 0, 90, 0, 1, 0);
            Matrix.scaleM(modelViewTemp, 0, 1f, 1f, SEP_TUBERIAS / LARGO_PIPE);
            pipe.dibujar(shader, modelViewTemp);
        }
    }

    private void removeRotation(float[] modelViewTemp) {
        double ang = -Math.toDegrees(Math.atan2(modelViewTemp[4],modelViewTemp[0]));
        modelViewTemp[0]=1f;
        modelViewTemp[1]=0f;
        modelViewTemp[2]=0f;
        modelViewTemp[4]=0f;
        modelViewTemp[5]=-1f;
        modelViewTemp[6]=0f;
        modelViewTemp[8]=0f;
        modelViewTemp[9]=0f;
        modelViewTemp[10]=-1f;
        Matrix.rotateM(modelViewTemp,0,(float)ang,0,0,1);
    }


    @Override
    public void recargarInformacion(WebRestDataInterface dataInterface) {
        if(!isLoaded()) return;
        bSRmodoFrio = dataInterface.isbSRmodoFrio();
        fSRtempRetorno = dataInterface.getfSRtempRetorno();
        fSRtempImpulsion = dataInterface.getfSRtempImpulsion();

        boolean newValvAbierta = dataInterface.isbSRvalvulaAbierta();
        if (bSRvalvulaAbierta != newValvAbierta) {
            bSRvalvulaAbierta = newValvAbierta;
            if (bSRvalvulaAbierta) {
                glText.newText(VALVULA_ABIERTA + "\n" + fSRtempImpulsion);
            } else {
                glText.newText(VALVULA_CERRADA + "\n" + fSRtempImpulsion);
            }
        }
    }
}

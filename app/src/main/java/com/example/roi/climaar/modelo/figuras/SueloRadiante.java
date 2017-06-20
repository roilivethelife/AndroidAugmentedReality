package com.example.roi.climaar.modelo.figuras;

import android.content.Context;
import android.opengl.Matrix;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.JsonRest.DynamicMapElement;
import com.example.roi.climaar.modelo.JsonRest.WebRestDataInterface;
import com.example.roi.climaar.vista.Shader;

import java.io.Serializable;

import static java.lang.Float.NaN;

/**
 * Created by roi on 24/04/17.
 */

public class SueloRadiante extends Figura implements Serializable, DynamicMapElement {


    private transient Obj pipe;

    private static final float[] COLOR_BLUE = {0.2f, 0.4f, 1f, 1f};
    private static final float[] COLOR_RED = {1f, 0.07f, 0f, 1f};
    private boolean colorRed = false;

    private static final float ANCHO_PIPE = 5.0f;
    private static final float LARGO_PIPE = 5.0f;


    private float ancho;
    private float largo;

    private static int resourceID = R.raw.pipe;


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
        pipe = new Obj(resourceID);
        pipe.loadFigura(context);
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

        //50cm de separacion entre tuberias
        float separacionTuberias = 30f;
        for (int x = 0, j = 0; x < (ancho); x += (int) separacionTuberias, j++) {
            Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, x, 0, 0);
            Matrix.scaleM(modelViewTemp, 0, 0.5f, 0.5f, largo / LARGO_PIPE);
            pipe.dibujar(shader, modelViewTemp);

            //Dibujamos lateral
            if (j % 2 == 0) {//si es par dibujamos lateral en 0
                Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, x, 0, 0);
            } else {//si es impar en largo
                Matrix.translateM(modelViewTemp, 0, modelViewMatrix, 0, x, 0, largo);
            }
            Matrix.rotateM(modelViewTemp, 0, 90, 0, 1, 0);
            Matrix.scaleM(modelViewTemp, 0, 0.5f, 0.5f, separacionTuberias / LARGO_PIPE);
            pipe.dibujar(shader, modelViewTemp);
        }
    }

    @Override
    public void recargarInformacion(WebRestDataInterface dataInterface) {
        bSRmodoFrio = dataInterface.isbSRmodoFrio();
        bSRvalvulaAbierta = dataInterface.isbSRvalvulaAbierta();
        fSRtempRetorno = dataInterface.getfSRtempRetorno();
        fSRtempImpulsion = dataInterface.getfSRtempImpulsion();
    }
}

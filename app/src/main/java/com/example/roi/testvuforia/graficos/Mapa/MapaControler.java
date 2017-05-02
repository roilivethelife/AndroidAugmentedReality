package com.example.roi.testvuforia.graficos.Mapa;

import android.content.Context;
import android.opengl.Matrix;

import com.example.roi.testvuforia.AppInstance;
import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.Texto.GLText;
import com.example.roi.testvuforia.graficos.Texto.GLTextDrawer;
import com.example.roi.testvuforia.graficos.figuras.Obj;
import com.example.roi.testvuforia.graficos.figuras.SueloRadiante;
import com.example.roi.testvuforia.graficos.figuras.Ventilador;

/**
 * Created by roi on 19/12/16.
 */

public class MapaControler {

    private Mapa mapActual;
    Context context;

    public MapaControler(Context context,Mapa mapa){
        this.context=context;
        this.mapActual = mapa;
    }

    public Mapa getMapActual() {
        return mapActual;
    }

    public static Mapa createLoadDefaultMap(){
        float[] tamMap = new float[3];
        tamMap[0] = 421.6f;
        tamMap[1] = 227.6f;
        tamMap[2] = 337.8f;
        float[] markerPos = new float[3];
        markerPos[0]=114f;
        markerPos[1]=89.3f;
        markerPos[2]=40f;
        Mapa map = new Mapa("CuartoFondo",tamMap,markerPos);
        map.setDescripcion("DefaultMap: cuartoFondo");
        MapaElement cuboCentro= null;
        MapaElement habitacionElement = null;
        MapaElement sueloRadiante = null;
        cuboCentro = new MapaElement("Centro",new Obj(R.raw.cubo));
        cuboCentro.pos[0]=markerPos[0];
        cuboCentro.pos[1]=markerPos[1];
        cuboCentro.pos[2]=markerPos[2];
        map.mapaElements.add(cuboCentro);
        /*
        sueloRadiante = new MapaElement("Suelo",new SueloRadiante(421,337));
        map.mapaElements.add(sueloRadiante);
        map.mapaElements.add(cuboCentro);
        MapaElement fan = new MapaElement("Ventilador",new Ventilador(true));
        //Posicion = posicion lampara
        fan.pos[0] = 197f;
        fan.pos[1] = 227.6f;
        fan.pos[2] = 166.8f;
        map.mapaElements.add(fan);
        //map.mapaElements.add(habitacionElement);
        */
        GLTextDrawer glTextDrawer = new GLTextDrawer(AppInstance.getInstance().getContext().getAssets());
        glTextDrawer.load("Roboto-Regular.ttf",40,0,0);
        //glTextDrawer.setScale(0.1f,0.1f);
        String text = "Esto es una prueba";
        GLText glText = new GLText(text.length());
        glTextDrawer.drawC(glText,text,0,0);
        //glTextDrawer.drawTexture(glText,20,20);
        MapaElement texto = new MapaElement("Texto",glText);
        texto.pos[0]=markerPos[0];
        texto.pos[1]=markerPos[1];
        texto.pos[2]=markerPos[2];
        map.mapaElements.add(texto);
        return map;
    }

    public void setMapActual(Mapa mapActual) {
        this.mapActual = mapActual;
    }

    public void dibujar(Shader shader, float[] modelViewMatrix){
        if(modelViewMatrix[15]>0.99f) {//vuforia track ok
            Matrix.translateM(modelViewMatrix, 0, -mapActual.markerPos[0], -mapActual.markerPos[1], -mapActual.markerPos[2]);
            for (MapaElement mapaElement : mapActual.mapaElements) {
                float[] tempModelViewMatrix = modelViewMatrix.clone();
                Matrix.translateM(tempModelViewMatrix, 0, mapaElement.pos[0], mapaElement.pos[1], mapaElement.pos[2]);
                Matrix.scaleM(tempModelViewMatrix, 0, mapaElement.scale[0], mapaElement.scale[1], mapaElement.scale[2]);
                mapaElement.dibujar(shader, tempModelViewMatrix);
            }
        }
    }
}





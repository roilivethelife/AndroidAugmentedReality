package com.example.roi.testvuforia.graficos.Mapa;

import android.content.Context;
import android.opengl.Matrix;

import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.Shader;
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
        /*habitacionElement = new MapaElement("Wireframe",new Obj(R.raw.pared));
        habitacionElement.scale[0]=200;
        habitacionElement.scale[1]=200;
        habitacionElement.scale[2]=200;*/
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
        return map;
    }

    public void setMapActual(Mapa mapActual) {
        this.mapActual = mapActual;
    }

    public void dibujar(Shader shader, float[] modelViewMatrix){
        Matrix.translateM(modelViewMatrix,0,-mapActual.markerPos[0],-mapActual.markerPos[1],-mapActual.markerPos[2]);
        for (MapaElement mapaElement : mapActual.mapaElements) {
            float[] tempModelViewMatrix = modelViewMatrix.clone();
            Matrix.translateM(tempModelViewMatrix,0, mapaElement.pos[0], mapaElement.pos[1], mapaElement.pos[2]);
            Matrix.scaleM(tempModelViewMatrix,0, mapaElement.scale[0], mapaElement.scale[1], mapaElement.scale[2]);
            mapaElement.dibujar(shader,tempModelViewMatrix);
        }
    }
}





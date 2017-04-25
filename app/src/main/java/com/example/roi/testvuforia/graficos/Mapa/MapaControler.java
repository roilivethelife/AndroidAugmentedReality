package com.example.roi.testvuforia.graficos.Mapa;

import android.content.Context;
import android.opengl.Matrix;

import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.figuras.Obj;
import com.example.roi.testvuforia.graficos.figuras.SueloRadiante;

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
        Mapa map = new Mapa("default",200,200,200);
        map.setDescripcion("Mapa por defecto para hacer pruebas");
        MapaElement cuboCentro= null;
        MapaElement habitacionElement = null;
        MapaElement sueloRadiante = null;
        cuboCentro = new MapaElement("Centro",new Obj(R.raw.cubo));
        cuboCentro.pos[0]=10;
        cuboCentro.pos[1]=10;
        cuboCentro.pos[2]=5;
        habitacionElement = new MapaElement("Wireframe",new Obj(R.raw.pared));
        habitacionElement.scale[0]=200;
        habitacionElement.scale[1]=200;
        habitacionElement.scale[2]=200;
        sueloRadiante = new MapaElement("Suelo",new SueloRadiante(200,200));
        map.mapaElements.add(sueloRadiante);
        map.mapaElements.add(cuboCentro);
        //map.mapaElements.add(habitacionElement);
        return map;
    }

    public void setMapActual(Mapa mapActual) {
        this.mapActual = mapActual;
    }

    public void dibujar(Shader shader, float[] modelViewMatrix){
        for (MapaElement mapaElement : mapActual.mapaElements) {
            float[] tempModelViewMatrix = modelViewMatrix.clone();
            Matrix.translateM(tempModelViewMatrix,0, mapaElement.pos[0], mapaElement.pos[1], mapaElement.pos[2]);
            Matrix.scaleM(tempModelViewMatrix,0, mapaElement.scale[0], mapaElement.scale[1], mapaElement.scale[2]);
            mapaElement.dibujar(shader,tempModelViewMatrix);
        }
    }
}




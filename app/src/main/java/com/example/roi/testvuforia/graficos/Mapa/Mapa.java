package com.example.roi.testvuforia.graficos.Mapa;

import com.example.roi.testvuforia.graficos.AABB;
import com.example.roi.testvuforia.graficos.Shader;

import java.util.ArrayList;

/**
 * Created by roi on 14/04/17.
 */

public class Mapa {
    private ArrayList<MapaElement> mapaElements;
    private float[] tamHabitacion = {360,300,450};//cm
    //AABB con medidas habitación
    private AABB aabb;

    public Mapa(){
        mapaElements = new ArrayList<>();
    }


    public void dibujar(Shader shader){
        for (MapaElement mElement :mapaElements) {
            mElement.dibujar(shader);
        }
    }

}

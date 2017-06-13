package com.example.roi.climaar.modelo;

import android.content.Context;
import android.graphics.PorterDuff;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.figuras.Obj;
import com.example.roi.climaar.modelo.figuras.SueloRadiante;
import com.example.roi.climaar.modelo.figuras.Texto.GLText;
import com.example.roi.climaar.modelo.figuras.Ventilador;
import com.example.roi.climaar.modelo.mapa.MapElement;
import com.example.roi.climaar.modelo.mapa.Mapa;
import com.example.roi.climaar.old.AppInstance;

import java.util.ArrayList;

/**
 * Created by roi on 11/06/17.
 */

public class Modelo{

    private static Modelo instance = new Modelo();
    private Context context;
    private ArrayList<Mapa> mapas;

    private Modelo(){
        mapas = new ArrayList<>();
    }

    public static Modelo getInstance(){
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<Mapa> getMapas() {
        if(mapas.size()==0){
            mapas.add(createLoadDefaultMap());
        }
        return mapas;
    }

    public Mapa createLoadDefaultMap(){
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
            MapElement cuboCentro= null;
            MapElement habitacionElement = null;
            MapElement sueloRadiante = null;
            cuboCentro = new MapElement("Centro",new Obj(R.raw.cubo));
            cuboCentro.pos[0]=markerPos[0];
            cuboCentro.pos[1]=markerPos[1];
            cuboCentro.pos[2]=markerPos[2];
            map.mapaElements.add(cuboCentro);

            sueloRadiante = new MapElement("Suelo",new SueloRadiante(421,337));
            map.mapaElements.add(sueloRadiante);
            map.mapaElements.add(cuboCentro);
            MapElement fan = new MapElement("Ventilador",new Ventilador(true));
            //Posicion = posicion lampara
            fan.pos[0] = 197f;
            fan.pos[1] = 227.6f;
            fan.pos[2] = 166.8f;
            map.mapaElements.add(fan);
            //map.mapaElements.add(habitacionElement);
            GLText glText = new GLText("Esto es una prueba");
            glText.setScaleX(0.1f);
            glText.setScaleY(0.1f);
            MapElement texto = new MapElement("Texto",glText);
            texto.pos[0]=markerPos[0];
            texto.pos[1]=markerPos[1];
            texto.pos[2]=markerPos[2];
            map.mapaElements.add(texto);
            return map;
        }
}
